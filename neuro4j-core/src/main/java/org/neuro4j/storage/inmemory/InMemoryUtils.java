package org.neuro4j.storage.inmemory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;

public class InMemoryUtils {

	/**
	 * for AND operation
	 * 
	 * @param net1
	 * @param net2
	 * @return
	 */
	public static Network andNetworks(Network net1, Network net2)
	{
		List<ERBase> e4Removal = new ArrayList<ERBase>();
		for (String eid : net1.getIds())
		{
			if (null == net2.getById(eid))
				e4Removal.add(net1.getById(eid));
		}
		
//		net1.remove(e4Removal.toArray(new Entity[]{}));
		for (ERBase entity : e4Removal)
			net1.remove(entity, true);


//		// relations
//		List<Relation> r4Removal = new ArrayList<Relation>();
//		for (String rid : net1.getRelations())
//		{
//			if (null == net2.getRelationByUUID(rid))
//				r4Removal.add(net1.getRelationByUUID(rid));
//		}
//		
//		net1.remove(r4Removal.toArray(new Relation[]{}));
		
		return net1;
	}	

	/**
	 * useful for AND operation 
	 * 
	 * @param net
	 * @param key
	 * @param value
	 */
	public static void filterEntities(Network net, String key, String value) {
		for (String eid : net.getIds())
		{
			ERBase e = net.getById(eid);
			if (!value.equals(e.getProperty(key)))
			{
				net.remove(e, true);
			}
		}
		return;
	}

	/**
	 * 
	 * 
	 * @param net
	 * @param newEntity
	 */
	public static void saveOrUpdate(Network net, ERBase newEntity) {
		
		ERBase currentEntity = net.getById(newEntity.getUuid());
		if (null == currentEntity)
		{
			// TODO do not make deep copy - it can lead to object duplications (through entities in relations)
//			currentEntity = (Entity) ClassUtils.deepCloneBySerialization(newEntity);

			currentEntity = newEntity.cloneWithConnectedKeys();
			net.add(currentEntity);

		} else {
			// update existing entity 
			currentEntity.setLastModifiedDate(new Date());
			
			// properties
			currentEntity.removeProperties();
			for (String key : newEntity.getPropertyKeysWithRepresentations())
				currentEntity.setProperty(key, newEntity.getProperty(key));
			
			// connected
			currentEntity.removeConnected();
			
			for (String nrid : newEntity.getConnectedKeys())
			{
				ERBase currentConnected = currentEntity.getConnected(nrid);
				if (null == currentConnected)
				{
					// new relation should be loaded (because it's new)
					ERBase newConnected = newEntity.getConnected(nrid);
					// it can be null. E.g. in case of export/import ERs can have orphan connections  
					if (null != newConnected)
						currentEntity.addConnected(newConnected);
				}
			}			
				
		}

		return;
	}

}
