package org.neuro4j.storage.solr.qp;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.core.Path;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.qp.ERType;
import org.neuro4j.storage.qp.NQLProcessorBase;
import org.neuro4j.storage.qp.QueryProcessorFilter;
import org.neuro4j.storage.qp.QueryProcessorFilterFactory;
import org.neuro4j.storage.solr.SearchIndexConfiguration;
import org.neuro4j.storage.solr.SolrIndexMgr;
import org.neuro4j.storage.solr.SolrStorageConfig;
import org.neuro4j.utils.KVUtils;

public class NQLProcessorSolr extends NQLProcessorBase {
	
	private SolrIndexMgr siMgr = null; 
	
	private StringBuffer erAttrQuery4Solr = null;
	
	
	public NQLProcessorSolr(SolrIndexMgr siMgr, Properties properties, Storage baseStorage)
	{
		this.siMgr = siMgr;
		this.baseStorage = baseStorage;
		
		READ_ONLY_QUERIES = KVUtils.getBooleanProperty(properties, SolrStorageConfig.STORAGE_READ_ONLY, READ_ONLY_QUERIES);
//		MAX_BETWEEN_DISTANCE = KVUtils.getIntProperty(properties, "n4j.storage.solr.between_max_distance", MAX_BETWEEN_DISTANCE);
	}

	
	public Network addERAttribute(Map<String, String> params) throws StorageException
	{
		
		// if it's second pipe -> use in-memory implementation 
		if (null != pipeNet)
		{
			return addERAttributeInMemoryImpl(params);
		}
		
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		String key = (String) params.get("key");

		if ("id".equalsIgnoreCase(key))
			key = "UUID";

		if (!"UUID".equals(key) && !"name".equals(key))
			key = SearchIndexConfiguration.PROPERTY_PREFIX + key;
			
		String comparator = (String) params.get("comparator");
		String value = (String) params.get("value");
		if ("=".equals(comparator))
		{
			erAttrQuery4Solr.append(key).append(":").append("\"").append(value).append("\"");
		} else if ("like".equalsIgnoreCase(comparator)) {
			// process like
			erAttrQuery4Solr.append(key).append(":").append(value);			
		} else {
			throw new RuntimeException("Wrong comparator " + comparator);
		}
		
		return null; // there is no network - we build Solr query and will get Network for whole ER block
	}

	public void startERAttributeProcessing(String erType)
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		erAttrQuery4Solr = new StringBuffer();
		this.currentERType = ERType.valueOf(erType);
	}


	/**
	 * inputNet is not used for Solr implementation
	 */
	public void finishERAttributeProcessing(Network currentERNetwork,
			Map<String, String> techParams, boolean optional) throws StorageException
	{
		// if it's second pipe -> use in-memory implementation 
		if (null != pipeNet)
		{
			finishERAttributeProcessingInMemoryImpl(currentERNetwork, techParams, optional);
			return;
		}
		
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		// check for technical parameters e.g. [type='virtual' flow='']
		if (null != techParams && techParams.size() > 0)
		{

			if (VIRTUAL.equalsIgnoreCase((String) techParams.get(TYPE))
					&& null != techParams.get(FLOW))
			{
				if (null == currentERNetwork)
					currentERNetwork = new Network();
				
				virtualERProcessing(currentERNetwork, techParams);
			}
			
		}
		
		if (ERType.filter == currentERType)
		{
			String filterClass = (String) techParams.get(FILTER_CLASS); 
			if (null == filterClass)
			{
				throw new StorageException("Filter class is not specified. Use: f([class='your.package.YourQueryProcessorFilterImpl']) ");
			}

			QueryProcessorFilter qpf = QueryProcessorFilterFactory.getQueryProcessorFilter(filterClass);
			qpStream = new SolrNQLProcessorStreamFilter(qpf, this.baseStorage, this.siMgr, currentMatchedPaths, qpStream);
			
			return;
		}

		
		// ((pr_age:27 AND name:Serega1) OR name:Serega2) AND pr_LIKE:coins
		String solrQuery = erAttrQuery4Solr.toString();
		if (0 == solrQuery.length())
			solrQuery = "*:*"; //  empty e[] or r[] means get all


	
		if (null == qpStream)
		{
			qpStream = new SolrNQLProcessorStreamQuery(solrQuery, this.siMgr, this.filterSet, currentMatchedPaths, qpStream, optional, outputNetworkLimit);
		} else {
			qpStream = new SolrNQLProcessorStreamQuery(solrQuery, this.siMgr, this.filterSet, qpStream, optional, outputNetworkLimit);
		}

		return;
	}
	
	public Network finishERParse() throws StorageException
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		outputNet = new Network();
		
		long netSizeCont = 0;
		// check for output limit
		while (qpStream.hasNext())
		{
			qpStream.next();
			netSizeCont++;
			
			if (-1 < outputNetworkLimit && netSizeCont >= outputNetworkLimit)
				break;
			
		}
		
		currentMatchedPaths = qpStream.getCurrentMatchedPaths(); 
		Set<String> netIds = new HashSet<String>();

		for (Path p : currentMatchedPaths)
		{
			for (String s : p.getItems())
			{
				netIds.add(s);
				if (-1 < outputNetworkLimit && netIds.size() >= outputNetworkLimit)
					break;
			}
			
			if (-1 < outputNetworkLimit && netIds.size() >= outputNetworkLimit)
				break;
		}
		

		// create net from matched paths
		int reqCnt = 0;
		Set<String> reqIds = new HashSet<String>();
		for (String id : netIds)
		{
			reqCnt++;
			reqIds.add(id);
			if (reqCnt >= OUTPUT_NET_REQUEST_IDS_LIMIT)
			{
				for (Connected er : siMgr.getByIds(reqIds))
					outputNet.add(er);
				
				reqCnt = 0;
				reqIds = new HashSet<String>();
			}
		}
		
		// serve last page
		if (reqIds.size() > 0)
		{
			for (Connected er : siMgr.getByIds(reqIds))
				outputNet.add(er);
		}

		return outputNet;
	}


	public void startERAttributeExpression(Map<String, String> params)
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;

		erAttrQuery4Solr.append(" ( ");
	}
	
	public void addERAttributeExpression(String str) // AND | OR
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		erAttrQuery4Solr.append(" ").append(str).append(" ");
	}

	public Network finishERAttributeExpression(Map<String, Object> params)
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;

		// close brace 	
		erAttrQuery4Solr.append(" ) ");
		
		return null; // there is no network - we build Solr query and will get Network for whole ER block
	}
	
	public Network doSimpleERAttributeExpression(Map<String, Object> params)
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		return null; // there is no network - we build Solr query and will get Network for whole ER block
	}
	
	
	/**
	 * 
	 */
	public void finishDelete() throws StorageException
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		if (READ_ONLY_QUERIES)
			throw new StorageException("Storage is run in read only mode");
			
		siMgr.deleteById(outputNet.getIds());
		
		return;
	}
	

	public Connected getById(String id)
	{
		return siMgr.getById(id);
	}

	

	public void recursiveERSubpath(Map<String, String> params) throws StorageException
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		String depthStr = null;
		int depth = 0;
		
		try {
			depthStr = params.get("depth");
			depth = Integer.parseInt(depthStr);
			
			for (int i = 0; i < depth; i++)
			{
				String solrQuery = "*:*";
				
				if (useOnlyAttrMap.size() > 0)
					solrQuery = getUseOnlyQuery(); // getOpositeER(qpStream.getERQueryType())

				if (ignoreAttrMap.size() > 0)
					solrQuery = getIgnoreQuery(); // getOpositeER(qpStream.getERQueryType())
				
				qpStream = new SolrNQLProcessorStreamQuery(solrQuery, this.siMgr, this.filterSet, qpStream, true, outputNetworkLimit);
			}
			
		} catch (Exception ex) {
			logger.severe("Wrong depth");
			throw new StorageException("Wrong depth " + depthStr);
		}
		
		return;
	}

	private String getUseOnlyQuery() // ERType queryType
	{		
    	StringBuffer sqSB = new StringBuffer();

    	boolean first = true;
    	for (String key : useOnlyAttrMap.keySet())
    	{
    		String qkey = key;

/*    		if (key.startsWith("r."))
    		{
    			if (queryType != ERType.relation)
    				continue;
    			
    			qkey = key.substring("r.".length());
    		}
    		
    		if (key.startsWith("e."))
    		{
    			if (queryType != ERType.entity)
    				continue;
    			
    			qkey = key.substring("e.".length());
    		}*/
    		
    		Set<String> values = useOnlyAttrMap.get(key);
    		for (String value : values)
    		{
            	if (!first)
            		sqSB.append(" OR ");
        		sqSB.append(qkey).append(":").append(value);
        		first = false;
    		}
    	}

		if (sqSB.length() == 0)
			return "*:*";

		return sqSB.toString();
	}

	private String getIgnoreQuery() // ERType queryType
	{		
    	StringBuffer sqSB = new StringBuffer();

    	boolean first = true;
    	for (String key : ignoreAttrMap.keySet())
    	{
    		String qkey = key;

/*    		if (key.startsWith("r."))
    		{
    			if (queryType != ERType.relation)
    				continue;
    			
    			qkey = key.substring("r.".length());
    		}
    		
    		if (key.startsWith("e."))
    		{
    			if (queryType != ERType.entity)
    				continue;
    			
    			qkey = key.substring("e.".length());
    		}
*/    		
    		Set<String> values = ignoreAttrMap.get(key);
    		for (String value : values)
    		{
            	if (!first)
            		sqSB.append(" AND ");
        		sqSB.append(qkey).append(":").append("* NOT " + value);
        		first = false;
    		}
    	}
//    	sqSB.append(")");

		if (sqSB.length() == 0)
			return "*:*";

//		String solrQuery = sqSB.toString();
			
		return sqSB.toString();
	}
	
}


