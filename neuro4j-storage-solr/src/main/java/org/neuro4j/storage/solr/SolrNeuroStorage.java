package org.neuro4j.storage.solr;

import java.net.URLDecoder;
import java.util.Properties;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.StorageBase;
import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.qp.NQLParser;
import org.neuro4j.storage.qp.ParseException;
import org.neuro4j.storage.solr.qp.NQLProcessorSolr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrNeuroStorage extends StorageBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private SolrIndexMgr siMgr = null; 

	private Properties properties;
	
	public SolrNeuroStorage()   
	{
		super();
	}

	public void init(Properties properties) {
		siMgr = new SolrIndexMgr(properties);
		this.properties = properties;
		return;
	}

	public boolean save(Network network)  throws StorageException, StorageException {
		{ // entities
			// handle deleted
			for (String eid : network.getDeletedEntityIds())
				siMgr.deleteById(eid);
			
			for (String eid : network.getEntities())
			{
				Entity e = network.getEntityByUUID(eid);
				if (e.isModified())
				{
					siMgr.saveOrUpdate(e);				
				}
			} // for (Entity e : network.getEIdMap().values())
		}
		
		{ // relations
			// handle deleted
			for (String rid : network.getDeletedRelationIds())
				siMgr.deleteById(rid);

			for (String rid : network.getRelations())
			{
				Relation r = network.getRelationByUUID(rid);
				if (r.isModified())
				{
					siMgr.saveOrUpdate(r);
				} // if (r.isModified())
			}
			
		}
		
		siMgr.commit();
		return true;
	}


	public Network query(String q) throws NQLException, StorageException  {
		

		long start = System.currentTimeMillis();
//    	NQLProcessorSolr nqlProcessor = new NQLProcessorSolr(this.siMgr, this.properties);		
//		NQLParser eqp = new NQLParser(q, nqlProcessor);

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
			// TODO Auto-generated catch block
			throw new NQLException("Wrong NQL: " + q, e);
		}

		return outNet;
	}
	

	
	/**
	 * load all network 
	 * 
	 * @param network
	 */
	private void load(Network network, int maxEntities) 
	{
		
/*		Set<String> relationIdSet = new HashSet<String>(); // to handle max count
		
		String[] eIds = eMgr.getEntityList();
		for (int i = 0; i < eIds.length; i++)
		{
			if (i > maxEntities)
				break;
				
			String id = eIds[i];
			Entity e = eMgr.getById(id);
			
			network.add(e);
		}
		
		eIds = network.getEntities();
		
//		String[] rIds = rMgr.getRelationList();
		String[] rIds = relationIdSet.toArray(new String[]{});
		for (String id : rIds)
		{
			Relation r = rMgr.getById(id, network);
			network.add(r);
		}
		
		for (String id : eIds)
		{
			Entity e = network.getEntityByUUID(id);
			e.setModified(false);
		}
		
		for (String id : rIds)
		{
			Relation r = network.getRelationByUUID(id);
			r.setModified(false);
		}
		*/
		return;
	}

}
