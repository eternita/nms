package org.neuro4j.storage.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Some static utils
 *
 */
public class SearchIndexHandler {

	private final static Logger logger = LoggerFactory.getLogger(SearchIndexHandler.class);
	
	private static Map<String, SolrServer> solrServers = new HashMap<String, SolrServer>();
	
	private SearchIndexHandler() {
		super();
	}
	
	public static SolrServer getSolrServer(String coreURL)
    {
		SolrServer sServer = null;
		sServer = solrServers.get(coreURL);
		
		if (null == sServer)
		{
			try {
				sServer = new CommonsHttpSolrServer(coreURL);
				solrServers.put(coreURL, sServer);
			} catch (MalformedURLException e) {
	            logger.error("Can't create Solr server - wrong URL: " + coreURL, e);
			}
		}
		
    	return sServer;
    }
    
	protected static void sendData4Index(SolrServer solrServer,
			List<SolrInputDocument> batchDocumentList) throws StorageException {
		// submit the batch documents list to the server
		try {
			UpdateResponse response = solrServer.add(batchDocumentList);
			logger.info("Update -> Response" + response);
//			response = solrServer.commit(false, false);
//			logger.info("Commit -> Response" + response);

		} catch (SolrServerException e) {
            logger.error("Can't send data to Solr server ", e);
			throw new StorageException("Can't communicate to Solr.", e);
		} catch (IOException e) {
            logger.error("Can't send data to Solr server ", e);
			throw new StorageException("Can't communicate to Solr.", e);
		}
		return;
	}

	public static void commit(SolrServer solrServer)  throws StorageException {
		try {
			UpdateResponse response = solrServer.commit(true, true);
			logger.info("Commit -> Response" + response);
		} catch (SolrServerException e) {
            logger.error("Can't commit", e);
			throw new StorageException("Can't communicate to Solr.", e);
		} catch (IOException e) {
            logger.error("Can't commit", e);
			throw new StorageException("Can't communicate to Solr.", e);
		}
		return;
	}

	public static void clearIndex(SolrServer solrServer)  throws StorageException
	{
        //rebuild, delete everything
        try
        {
            UpdateResponse response = solrServer.deleteByQuery("*:*");
            logger.info("Delete -> Response" + response);
            response = solrServer.commit(true, true);
            System.out.println("Commit -> Response" + response);             
            response = solrServer.optimize(true, false);
            System.out.println("Optimize -> Response" + response);       
        }
        catch (SolrServerException e)
        {
            logger.error("Can't clear index", e);
			throw new StorageException("Can't communicate to Solr.", e);
        }
        catch (IOException e)
        {
            logger.error("Can't clear index", e);
			throw new StorageException("Can't communicate to Solr.", e);
        }		
	}


	public static SolrInputDocument createSolrInputDocument(Connected er)
	{
        SolrInputDocument doc = new SolrInputDocument(); 

    	doc.addField(SearchIndexConfiguration.FIELD_UUID, er.getUuid());	
    	doc.addField(SearchIndexConfiguration.FIELD_NAME, er.getName());	

    	for (String propertyKey : er.getPropertyKeysWithRepresentations())
        	doc.addField(SearchIndexConfiguration.PROPERTY_PREFIX + propertyKey, er.getProperty(propertyKey));	
    	
    	for (String cid : er.getConnectedKeys())
        	doc.addField(SearchIndexConfiguration.CONNECTED, cid);	

    	
    	return doc;		
	}
	
	public static String doc2id(SolrDocument doc)
	{
		return (String) doc.getFieldValue(SearchIndexConfiguration.FIELD_UUID);
	}

	public static void doc2erbase(Connected er, SolrDocument doc)
	{
		er.setUuid((String) doc.getFieldValue(SearchIndexConfiguration.FIELD_UUID));
		er.setName((String) doc.getFieldValue(SearchIndexConfiguration.FIELD_NAME));
		
		for (String propName : doc.getFieldNames())
		{
			if (propName.startsWith(SearchIndexConfiguration.PROPERTY_PREFIX))
				er.setProperty(propName.substring(SearchIndexConfiguration.PROPERTY_PREFIX.length()), (String) doc.get(propName));
		}
		
		List<String> eids = (List<String>) doc.getFieldValue(SearchIndexConfiguration.CONNECTED);
		if (null != eids)
			for (String eid : eids)
				er.addConnected(eid);
		
		return;
	}

	public static Connected doc2erbase(SolrDocument doc)
	{
		Connected er = new Connected();
		doc2erbase(er, doc);
		return er;
	}


	
	/**
	 * @deprecated use docs2net(Network net, Iterator<SolrDocument> iter)
	 * 
	 * @param net
	 * @param queryResponse
	 */
	public static void docs2net(Network net, QueryResponse queryResponse)
	{
    	if(queryResponse.getResults().getNumFound() > 0)
        {
    		for (SolrDocument doc : queryResponse.getResults())
    		{
                net.add(doc2erbase(doc));
    		}
        }		
	}	
	
	public static void docs2net(Network net, Iterator<SolrDocument> iter)
	{
		while (iter.hasNext())
		{
			SolrDocument doc = iter.next();
            net.add(doc2erbase(doc));
		}
	}	
	
	public static Set<String> response2Ids(Iterator<SolrDocument> iter)
	{
		Set<String> idSet = new HashSet<String>();
		while (iter.hasNext())
		{
            SolrDocument doc = iter.next(); 
            idSet.add(doc.getFieldValue(SearchIndexConfiguration.FIELD_UUID).toString());
		}
        
		return idSet;
	}
	
	/**
	 * @deprecated  use  Set<String> response2Ids(Iterator<SolrDocument> iter)
	 * 
	 * @param queryResponse
	 * @return
	 */
	public static Set<String> response2Ids(QueryResponse queryResponse)
	{
		Set<String> idSet = new HashSet<String>();
        if(queryResponse.getResults().getNumFound()>0)
        {
    		Iterator<SolrDocument> iter = queryResponse.getResults().iterator();
    		while (iter.hasNext())
    		{
                SolrDocument doc = iter.next(); 
                idSet.add(doc.getFieldValue(SearchIndexConfiguration.FIELD_UUID).toString());
    		}
        }
        
		return idSet;
	}
	
	
}