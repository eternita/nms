package org.neuro4j.storage.inmemory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;

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
		List<Entity> e4Removal = new ArrayList<Entity>();
		for (String eid : net1.getEntities())
		{
			if (null == net2.getEntityByUUID(eid))
				e4Removal.add(net1.getEntityByUUID(eid));
		}
		
//		net1.remove(e4Removal.toArray(new Entity[]{}));
		for (Entity entity : e4Removal)
			net1.remove(entity, true);


		// relations
		List<Relation> r4Removal = new ArrayList<Relation>();
		for (String rid : net1.getRelations())
		{
			if (null == net2.getRelationByUUID(rid))
				r4Removal.add(net1.getRelationByUUID(rid));
		}
		
		net1.remove(r4Removal.toArray(new Relation[]{}));
		
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
		for (String eid : net.getEntities())
		{
			Entity e = net.getEntityByUUID(eid);
			if (!value.equals(e.getProperty(key)))
			{
				net.remove(e, true);
			}
		}
		return;
	}
	
	/**
	 * useful for AND operation
	 * 
	 * @param net
	 * @param key
	 * @param value
	 */
	public static void filterRelations(Network net, String key, String value) {
		for (String rid : net.getRelations())
		{
			Relation r = net.getRelationByUUID(rid);
			if (!value.equals(r.getProperty(key)))
			{
				net.remove(r);
			}
		}
		return;
	}
	
	public static void saveOrUpdate(Network net, Relation newRelation) {
		Relation currentRelation = net.getRelationByUUID(newRelation.getUuid());
		if (null == currentRelation)
		{
			// TODO do not make deep copy - it can lead to object duplications (through entities in relations)
//			currentRelation = (Relation) ClassUtils.deepCloneBySerialization(newRelation);
			// create new relation - all entities should be in network already  
			currentRelation = createNewRelationUsingExistingEntitiesIfPossible(net, newRelation);
			net.add(currentRelation);
		} else {
			// update existing relation - all entities should be in network already  
			updateCurrentRelationUsingExistingEntitiesIfPossible(net, currentRelation, newRelation);
		}
		
		return;
	}

	/**
	 * 
	 * 
	 * @param net
	 * @param newEntity
	 */
	public static void saveOrUpdate(Network net, Entity newEntity) {
		
		Entity currentEntity = net.getEntityByUUID(newEntity.getUuid());
		if (null == currentEntity)
		{
			// TODO do not make deep copy - it can lead to object duplications (through entities in relations)
//			currentEntity = (Entity) ClassUtils.deepCloneBySerialization(newEntity);
			currentEntity = newEntity.cloneBase();
			net.add(currentEntity);

		} else {
			// update existing entity 
			currentEntity.setLastModifiedDate(new Date());
			
			// properties
			currentEntity.removeProperties();
			for (String key : newEntity.getPropertyKeysWithRepresentations())
				currentEntity.setProperty(key, newEntity.getProperty(key));
				
		}

		// relations
		for (String nrid : newEntity.getConnectedKeys())
		{
			Relation currentRel = currentEntity.getRelation(nrid);
			if (null == currentRel)
			{
				// new relation should be loaded (because it's new)
				Relation newRel = newEntity.getRelation(nrid);
				// create similar relation - use existing entities (if possible)
				currentRel = createNewRelationUsingExistingEntitiesIfPossible(net, newRel);
				net.add(currentRel);
			}
		}

		for (String crid : currentEntity.getConnectedKeys())
		{
			Relation newRel = newEntity.getRelation(crid);
			if (null == newRel)
			{
				// relation has been deleted
				// do nothing - it's handled in NeuroStorage.save()
			}
		}
	
	
/*		
		// relations
		for (Relation newRel : newEntity.getAllRelations())
		{
			Relation currentRel = currentEntity.getRelation(newRel.getUuid());
			if (null == currentRel)
			{
				// new relation
				// create similar relation - use existing entities (if possible)
				currentRel = createNewRelationUsingExistingEntitiesIfPossible(net, newRel);
				net.add(currentRel);
			} else if (!currentRel.equalsData(newRel)) {
				// updated relation
				updateCurrentRelationUsingExistingEntitiesIfPossible(net, currentRel, newRel);
			} else {
				// equals - do nothing
			}
		}
//*/		
		return;
	}

	
	private static Relation createNewRelationUsingExistingEntitiesIfPossible(Network net, Relation outsideRelation)
	{
		Relation r = outsideRelation.cloneBase(); 
		
		for (Entity outrpe : outsideRelation.getAllParticipants())
		{
			Entity e = net.getEntityByUUID(outrpe.getUuid());
			if (null == e)
			{
				// create new entity without relations
				e = outrpe.cloneBase();
			}
//			RelationPart rp = new RelationPart(outrp.getType(), e);
			r.addParticipant(e);
		}
		return r;
	}

	private static void updateCurrentRelationUsingExistingEntitiesIfPossible(Network net, Relation existingRelation, Relation outsideRelation)
	{
		existingRelation.setName(outsideRelation.getName());
		existingRelation.setLastModifiedDate(outsideRelation.getLastModifiedDate());

		existingRelation.removeProperties();
		for (String key : outsideRelation.getPropertyKeysWithRepresentations())
			existingRelation.setProperty(key, outsideRelation.getProperty(key));
		
		
		for (Entity outrpe : outsideRelation.getAllParticipants())
		{
//			Entity outrpe = outrp.getEntity();
			Entity rp = existingRelation.getParticipant(outrpe.getUuid());
			if (null != rp)
				continue; // this relation part already exist
			
			Entity e = net.getEntityByUUID(outrpe.getUuid());
			if (null == e)
			{
				// create new entity without relations
				e = new Entity(outrpe.getName());
				e.setUuid(outrpe.getUuid());
				e.setLastModifiedDate(outrpe.getLastModifiedDate());
				
				for (String key : outrpe.getPropertyKeys())
					e.setProperty(key, outrpe.getProperty(key));
			}
			existingRelation.addParticipant(e);
		}
		
		return;
	}
	
}
