package org.neuro4j.storage.solr;

import java.net.URLDecoder;
import java.util.Properties;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.StorageBase;
import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.qp.NQLParser;
import org.neuro4j.storage.qp.ParseException;
import org.neuro4j.storage.solr.qp.NQLProcessorSolr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrStorage extends StorageBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private SolrIndexMgr siMgr = null; 

	
	public SolrStorage()   
	{
		super();
	}

	public void init(Properties properties) throws StorageException {
		super.init(properties);
		
		siMgr = new SolrIndexMgr(properties);
		return;
	}

	public boolean save(Network network)  throws StorageException, StorageException {
		{ // entities
			// handle deleted
			for (String eid : network.getDeletedEntityIds())
				siMgr.deleteById(eid);
			
			for (String eid : network.getIds())
			{
				Connected e = network.getById(eid);
				if (e.isModified())
				{
					siMgr.saveOrUpdate(e);				
				}
			} // for (Entity e : network.getEIdMap().values())
		}
		
		
		siMgr.commit();
		return true;
	}


	public Network query(String q) throws NQLException, StorageException  {

		long start = System.currentTimeMillis();

    	NQLProcessorSolr nqlProcessor = new NQLProcessorSolr(this.siMgr, this.properties, this);
    	NQLParser eqp = new NQLParser(q, nqlProcessor);

		Network outNet;
		
		try {
			outNet = eqp.parse();
			long end = System.currentTimeMillis();
	    	logger.info("QTime " + (end - start) + " ms. q = " + URLDecoder.decode(q) );
	    	if ((end - start) > 3000)
	    		logger.warn("Slow Query, QTime " + (end - start) + " ms. q = " + URLDecoder.decode(q) );
	    	
		} catch (ParseException e) {
			throw new NQLException("Wrong NQL: " + q, e);
		}
		
		return outNet;
	}

	@Override
	public void ping() throws StorageException {
		siMgr.ping();
	}
	


}
