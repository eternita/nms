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
import org.neuro4j.core.ERBase;
import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
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
		} catch (IOException e) {
            logger.error("Can't send data to Solr server ", e);
		}
		return;
	}

	public static void commit(SolrServer solrServer)  throws StorageException {
		try {
			UpdateResponse response = solrServer.commit(false, false);
			logger.info("Commit -> Response" + response);
		} catch (SolrServerException e) {
            logger.error("Can't commit", e);
		} catch (IOException e) {
            logger.error("Can't commit", e);
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
        }
        catch (IOException e)
        {
            logger.error("Can't clear index", e);
        }		
	}


	public static SolrInputDocument createSolrInputDocument(ERBase er)
	{
        SolrInputDocument doc = new SolrInputDocument(); 

    	doc.addField(SearchIndexConfiguration.FIELD_UUID, er.getUuid());	
    	doc.addField(SearchIndexConfiguration.FIELD_NAME, er.getName());	

    	for (String propertyKey : er.getPropertyKeysWithRepresentations())
        	doc.addField(SearchIndexConfiguration.PROPERTY_PREFIX + propertyKey, er.getProperty(propertyKey));	
    	
    	if (er instanceof Entity)
    	{
        	doc.addField(SearchIndexConfiguration.FIELD_ER_TYPE, SearchIndexConfiguration.ER_TYPE_ENTITY);	
    		
        	for (String rid : ((Entity) er).getRelationsKeys())
            	doc.addField(SearchIndexConfiguration.RELATIONS, rid);	
    	}
    	
    	if (er instanceof Relation)
    	{
        	doc.addField(SearchIndexConfiguration.FIELD_ER_TYPE, SearchIndexConfiguration.ER_TYPE_RELATION);	
        	for (String rid : ((Relation) er).getParticipantsKeys())
            	doc.addField(SearchIndexConfiguration.ENTITIES, rid);	
    	}
    	
    	return doc;		
	}

//	public static SolrInputDocument createSolrInputDocument(Entity entity)
//	{
//        SolrInputDocument doc = createSolrInputDocument((ERBase) entity);
//
//    	doc.addField(SearchIndexConfiguration.FIELD_ER_TYPE, SearchIndexConfiguration.ER_TYPE_ENTITY);	
//
//    	for (String rid : entity.getRelationsKeys())
//        	doc.addField(SearchIndexConfiguration.RELATIONS, rid);	
//    	
//    	return doc;		
//	}
//	
//	public static SolrInputDocument createSolrInputDocument(Relation relation)
//	{
//        SolrInputDocument doc = createSolrInputDocument((ERBase) relation);
//
//    	doc.addField(SearchIndexConfiguration.FIELD_ER_TYPE, SearchIndexConfiguration.ER_TYPE_RELATION);	
//
//    	for (String rid : relation.getParticipantsKeys())
//        	doc.addField(SearchIndexConfiguration.ENTITIES, rid);	
//    	
//    	return doc;		
//	}
	
	public static String doc2id(SolrDocument doc)
	{
		return (String) doc.getFieldValue(SearchIndexConfiguration.FIELD_UUID);
	}

	public static void doc2erbase(ERBase er, SolrDocument doc)
	{
		er.setUuid((String) doc.getFieldValue(SearchIndexConfiguration.FIELD_UUID));
		er.setName((String) doc.getFieldValue(SearchIndexConfiguration.FIELD_NAME));
		
		for (String propName : doc.getFieldNames())
		{
			if (propName.startsWith(SearchIndexConfiguration.PROPERTY_PREFIX))
				er.setProperty(propName.substring(SearchIndexConfiguration.PROPERTY_PREFIX.length()), (String) doc.get(propName));
		}
		return;
	}

	public static ERBase doc2erbase(SolrDocument doc)
	{
		String docERType = (String) doc.getFieldValue(SearchIndexConfiguration.FIELD_ER_TYPE);
		ERBase er = null;
		if(SearchIndexConfiguration.ER_TYPE_ENTITY.equals(docERType))
		{
            er = SearchIndexHandler.doc2entity(doc);
		} else if (SearchIndexConfiguration.ER_TYPE_RELATION.equals(docERType)) {
            er = SearchIndexHandler.doc2relation(doc);
		} else {
			throw new RuntimeException("Wrong doc type (FIELD_ER_TYPE)");
		}
		return er;
	}

	public static Entity doc2entity(SolrDocument doc)
	{
		if (null == doc)
			return null;
		
		Entity e = new Entity();
		doc2erbase(e, doc);
		
		List<String> rids = (List<String>) doc.getFieldValue(SearchIndexConfiguration.RELATIONS);
		if (null != rids)
			for (String rid : rids)
				e.addRelation(rid);

		return e;
	}

	public static Relation doc2relation(SolrDocument doc)
	{
		if (null == doc)
			return null;
		
		Relation r = new Relation("");
		doc2erbase(r, doc);
		
		List<String> eids = (List<String>) doc.getFieldValue(SearchIndexConfiguration.ENTITIES);
		if (null != eids)
			for (String eid : eids)
				r.addParticipant(eid);

		return r;
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
    			String docERType = (String) doc.getFieldValue(SearchIndexConfiguration.FIELD_ER_TYPE);
    			if(SearchIndexConfiguration.ER_TYPE_ENTITY.equals(docERType))
    			{
                    Entity e = doc2entity(doc);
                    net.add(e);
    			} else if (SearchIndexConfiguration.ER_TYPE_RELATION.equals(docERType)) {
                    Relation r = doc2relation(doc);
                    net.add(r);
    				
    			} else {
    				throw new RuntimeException("Wrong doc type (FIELD_ER_TYPE)");
    			}
    		}
        }		
	}	
	
	public static void docs2net(Network net, Iterator<SolrDocument> iter)
	{
		while (iter.hasNext())
		{
			SolrDocument doc = iter.next();
		
			String docERType = (String) doc.getFieldValue(SearchIndexConfiguration.FIELD_ER_TYPE);
			if(SearchIndexConfiguration.ER_TYPE_ENTITY.equals(docERType))
			{
                Entity e = doc2entity(doc);
                net.add(e);
			} else if (SearchIndexConfiguration.ER_TYPE_RELATION.equals(docERType)) {
                Relation r = doc2relation(doc);
                net.add(r);
				
			} else {
				throw new RuntimeException("Wrong doc type (FIELD_ER_TYPE)");
			}
		}
	}	
	
	public static boolean isEntity(SolrDocument doc)
	{
		String docERType = (String) doc.getFieldValue(SearchIndexConfiguration.FIELD_ER_TYPE);
		if(SearchIndexConfiguration.ER_TYPE_ENTITY.equals(docERType))
			return true;
			
		return false;
	}

	public static boolean isRelation(SolrDocument doc)
	{
		String docERType = (String) doc.getFieldValue(SearchIndexConfiguration.FIELD_ER_TYPE);
		if(SearchIndexConfiguration.ER_TYPE_RELATION.equals(docERType))
			return true;
			
		return false;
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