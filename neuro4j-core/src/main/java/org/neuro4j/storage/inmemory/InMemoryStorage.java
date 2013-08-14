package org.neuro4j.storage.inmemory;

import java.util.Properties;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.StorageBase;
import org.neuro4j.storage.StorageException;


/**
 * 
 * In Memory Storage
 *
 */
public class InMemoryStorage extends StorageBase {

	protected Network instance = new Network();
	
	public void init(Properties properties) throws StorageException {
		super.init(properties);
	}

	public boolean save(Network network) throws StorageException {

		// TODO: rework e & r
		
		{ // entities
			// handle deleted
			for (String eid : network.getDeletedEntityIds())
				instance.remove(network.getEntityByUUID(eid));
//				eMgr.deleteById(eid, true, true);
			
			for (String eid : network.getEntities())
			{
				Entity e = network.getEntityByUUID(eid);
				if (e.isModified())
				{
//					if (!network.getUuid().equals(e.getNetworkId()))
//						e.setNetworkId(network.getUuid()); // set uuid if schema was just persisted
					InMemoryUtils.saveOrUpdate(instance, e);				
				}
			} // for (Entity e : network.getEIdMap().values())
		}

		
		{ // relations
			// handle deleted
			for (String rid : network.getDeletedRelationIds())
				instance.remove(network.getRelationByUUID(rid));

			for (String rid : network.getRelations())
			{
				Relation r = network.getRelationByUUID(rid);
				if (r.isModified())
				{
					InMemoryUtils.saveOrUpdate(instance, r);
				} // if (r.isModified())
			}
			
		}
		
		return true;
	}

	public Network query(String q) throws NQLException {
		
		return instance.query(q);

	}
	
}
