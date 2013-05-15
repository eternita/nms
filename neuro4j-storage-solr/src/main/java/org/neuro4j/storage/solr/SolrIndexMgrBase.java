package org.neuro4j.storage.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class SolrIndexMgrBase {

	private final static Logger logger = LoggerFactory.getLogger(SolrIndexMgrBase.class);
	
	private static Map<String, SolrServer> solrServers = new HashMap<String, SolrServer>();

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
    	return sServer;
    }
    
	protected static void sendData4Index(SolrServer solrServer,
			List<SolrInputDocument> batchDocumentList) {
		// submit the batch documents list to the server
		try {
			UpdateResponse response = solrServer.add(batchDocumentList);
			logger.info("Update -> Response" + response);
//			response = solrServer.commit(false, false);
//			logger.info("Commit -> Response" + response);

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public static void commit(SolrServer solrServer) {
		try {
			UpdateResponse response = solrServer.commit(false, false);
			logger.info("Commit -> Response" + response);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public static void clearIndex(SolrServer solrServer)
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }		
	}
	
	public SolrIndexMgrBase() {
		super();
	}

	
	
}