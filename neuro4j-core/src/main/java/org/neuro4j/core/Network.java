package org.neuro4j.core;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.inmemory.qp.NQLProcessorInMemory2;
import org.neuro4j.storage.qp.NQLParser;
import org.neuro4j.storage.qp.NQLProcessor;
import org.neuro4j.storage.qp.ParseException;
import org.neuro4j.utils.ClassUtils;
import org.neuro4j.utils.StringUtils;


public class Network implements Serializable {

	transient Logger logger = Logger.getLogger(Network.class.getName());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;

	private boolean modified = false;

	// id - entity
	private Map<String, Entity> ideMap = new HashMap<String, Entity>();
	
	// id - Relation
	private Map<String, Relation> idRelationMap = new HashMap<String, Relation>();
	
	// deleted entities ids - used when persist network
	private Set<String> deletedEntityIds = new HashSet<String>();
	// deleted relations ids - used when persist network
	private Set<String> deletedRelationIds = new HashSet<String>();

	
	public Network() {
		super();
	}


	public Network query(String q) throws NQLException
	{
	
		long start = System.currentTimeMillis();
    	NQLProcessor nqlProcessor = new NQLProcessorInMemory2(this, null); 
		
		NQLParser eqp = new NQLParser(q, nqlProcessor);

		Network outNet = null;
		
		try {
			outNet = eqp.parse();
			
			outNet.deletedEntityIds.clear(); // here is some ids as result of computation
			
			long end = System.currentTimeMillis();
	    	logger.finest("QTime " + (end - start) + " ms. q = " + URLDecoder.decode(q) );
	    	if ((end - start) > 3000)
	    		logger.warning("Slow Query, QTime " + (end - start) + " ms. q = " + URLDecoder.decode(q) );
	    	
		} catch (ParseException e) {
			throw new NQLException("Wrong NQL: " + q + "; \n " + e.getMessage(), e);
		} catch (StorageException e) {
			throw new NQLException("Error during execution NQL: " + q + "; \n " + e.getMessage(), e);
		}

		return outNet;		
	}	

	private void addTail(Entity... entities)
	{
		

		for (Entity entity : entities)
		{
			Entity current = ideMap.get(entity.getUuid());
			if (null == current)
			{
				ideMap.put(entity.getUuid(), entity);
			} else {
				// already exist -> ! do not override it
				// update properties ??
			}
		}

		return;
	}
	
	public void add(ERBase... items)
	{
		add(true, items);
		return;
	}

	public void add(boolean withConnected, ERBase... items)
	{
		for(ERBase item : items)
		{
			if (item instanceof Entity)
				add(withConnected, (Entity) item);
			
			else if (item instanceof Relation)
				add(withConnected, (Relation) item);
		}
		return;
	}
	
	public void add(Network net)
	{
		for(ERBase er : net.getERBases())
			add(er);		
		
		return;
	}
	
	public void add(Entity... entities)
	{
		add(true, entities);
	}
	
	public void add(boolean withConnected, Entity... entities)
	{
		addTail(entities);

		for (Entity entity : entities)
		{
			for (String relationUUID : entity.getRelationsKeys())
			{
				Relation relation = idRelationMap.get(relationUUID);

				if (withConnected)
				{
					// if relation not in Network map -> search it in entity -> and then put to Network map
					if (null == relation)
					{
						relation = entity.getRelation(relationUUID);
						if (null != relation)
							idRelationMap.put(relationUUID, relation);
					}
				}
				
				if (null != relation)
				{
					// bind if not binded already
					if (entity != relation.getParticipant(entity.getUuid()))
						relation.addParticipant(entity);
				}
			}
		}

		return;
	}
	
	public void remove(ERBase... items)
	{
		for(ERBase item : items)
		{
			if (item instanceof Entity)
				remove((Entity) item);
			
			else if (item instanceof Relation)
				remove((Relation) item);
		}
		return;
	}
	
	public void remove(ERBase item, boolean force)
	{
		if (item instanceof Entity)
			remove((Entity) item, force);
		
		else if (item instanceof Relation)
			remove((Relation) item);

		return;
	}
	
	private void remove(Entity... entities)
	{
		for (Entity entity : entities)
			remove(entity, false);
		
		return;
	}
	
	/**
	 * For internal use. 
	 * Use carefully - it may lead to relations with missed entities (e.g. SolrStorage, RDBMSStorage, ...). 
	 * 
	 * @param entity
	 * @param force
	 */
	private void remove(Entity entity, boolean force)
	{
		
		if (force)
		{
			// delete relations even they were not loaded (may lead to relations with missed entities)
			for (String rid : entity.getRelationsKeys())
			{
				Relation r = entity.getRelation(rid);
				if (null != r)
					r.removeParticipant(entity.getUuid());
			}
		} else {
			for (Relation r : entity.getAllRelations())
			{
				r.removeParticipant(entity.getUuid());
			}
		}
		
		deletedEntityIds.add(entity.getUuid());

		ideMap.remove(entity.getUuid());
		
		return;
	}
	
	private void remove(Relation... relations)
	{
		for (Relation relation : relations)
		{
			// unbind entities
			Set<String> eSet = new HashSet<String>(); // used to avoid set concurrent modification 
			for (String rp : relation.getParticipantsKeys())
				eSet.add(rp);

			for (String eid : eSet)
				relation.removeParticipant(eid);

			this.idRelationMap.remove(relation.getUuid());
			this.deletedRelationIds.add(relation.getUuid());
		}
	}
	
	/**
	 * 
	 * @param relations
	 */
	public void add(Relation... relations)
	{
		add(true, relations);
	}
	
	public void add(boolean withConnected, Relation... relations)
	{
		addTail(relations);
		
		for (Relation relation : relations)
		{
			for (String entityKey : relation.getParticipantsKeys())
			{
				Entity entity = ideMap.get(entityKey);
				
				if (withConnected)
				{
					// if entity not in Network map -> search it in relation -> and then put to Network map
					if (null == entity)
					{
						entity = relation.getParticipant(entityKey);
						if (null != entity)
							ideMap.put(entityKey, entity);
					}
				}

				if (null != entity)
				{
					// bind if not binded already
					if (relation != entity.getRelation(relation.getUuid()))
						entity.addRelation(relation);
				}
			}
		}
		
		return;
	}	
	
	private void addTail(Relation... relations)
	{
		for (Relation relation : relations)
		{
			Relation current = idRelationMap.get(relation.getUuid());
			if (null == current)
			{
				idRelationMap.put(relation.getUuid(), relation);
			} else {
				// already exist -> ! do not override it
				// update properties ??
			}
		}
		return;
	}	
	
	public String[] getRelations()
	{
		

		return (String[]) getRelationIdMap().keySet().toArray(new String[]{});
	}
	
	/**
	 * get entities by uuid, name, other property
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Set<Relation> getRelations(String key, String value)
	{
		
		Set<Relation> relations = new HashSet<Relation>();
		
		// get by NAME
		if ("name".equalsIgnoreCase(key))
		{
			for (Relation r : idRelationMap.values())
			{
				// if match key & value - return it
				if (value.equals(r.getName()))
					relations.add(r);
			}			
			
			return relations;
		}
		
		
		// get by UUID
		if ("uuid".equalsIgnoreCase(key))
		{
			Relation r = getRelationByUUID(value);
			if (null != r)
				relations.add(r);
			return relations;
		}
		
		// get by OTHER PROPERTY
		for (Relation r : idRelationMap.values())
		{
			if (value.equals(r.getProperty(key)))
				relations.add(r);
		}

		return relations;
	}	
	
	public Set<Relation> getRelationsByRegexp(String key, String regexp)
	{
		Set<Relation> relations = new HashSet<Relation>();
		
		// get by OTHER PROPERTY
		for (Relation r : idRelationMap.values())
		{
			// CHECK OTHER ID
			if ("uuid".equalsIgnoreCase(key) && StringUtils.match(r.getUuid(), regexp))
				relations.add(r);
			
			// CHECK OTHER NAME
			if ("name".equalsIgnoreCase(key) && StringUtils.match(r.getName(), regexp))
				relations.add(r);
			
			// CHECK OTHER PROPERTY
			if (StringUtils.match(r.getProperty(key), regexp))
				relations.add(r);
		}

		return relations;
	}	
	
	public Relation getRelationByUUID(String relationUUID)
	{
		return getRelationIdMap().get(relationUUID);
	}
	
	/**
	 * Returns true if there is Entity or Relation with such id in the network
	 * 
	 * @param id
	 * @return
	 */
	public boolean contains(String id)
	{
		if (ideMap.containsKey(id))
			return true;
	
		return idRelationMap.containsKey(id);
	}

	/**
	 * Returns ids for all entities and relations in the network 
	 * 
	 * @return
	 */
	public String[] getERBaseIds()
	{
		Set<String> erids = new HashSet<String>();
		
		erids.addAll(ideMap.keySet());
		erids.addAll(getRelationIdMap().keySet());
		
		return (String[]) erids.toArray(new String[]{});
	}
	
	public Iterator<ERBase> getERBaseIterator()
	{
		return getERBases().iterator();
	}
	
	public Set<ERBase> getERBases()
	{
		Set<ERBase> erids = new HashSet<ERBase>();
		
		erids.addAll(ideMap.values());
		erids.addAll(getRelationIdMap().values());
		
		return erids;
	}
	
	
	public Iterator<String> getERBaseIdsIterator()
	{
		Set<String> erids = new HashSet<String>();
		
		erids.addAll(ideMap.keySet());
		erids.addAll(getRelationIdMap().keySet());
		
		return erids.iterator();
	}
	
	
	public String[] getEntities()
	{
		return (String[]) ideMap.keySet().toArray(new String[]{});
	}
	
	public Entity getEntityByUUID(String entityUUID)
	{
		return ideMap.get(entityUUID);
	}
	
	public Set<Entity> getEntitiesByRegexp(String key, String regexp)
	{
		Set<Entity> entities = new HashSet<Entity>();
		
		for (Entity e : ideMap.values())
		{
			// CHECK OTHER ID
			if ("uuid".equalsIgnoreCase(key) && StringUtils.match(e.getUuid(), regexp))
				entities.add(e);
			
			// CHECK OTHER NAME
			if ("name".equalsIgnoreCase(key) && StringUtils.match(e.getName(), regexp))
				entities.add(e);
			
			// CHECK OTHER PROPERTY
			if (StringUtils.match(e.getProperty(key), regexp))
				entities.add(e);
		}
		
		return entities;
	}
	
	public Set<Entity> getEntitiesWithProperty(String key)
	{
		Set<Entity> entities = new HashSet<Entity>();
		
		for (Entity e : ideMap.values())
		{
			if (e.properties.containsKey(key))
				entities.add(e);
		}
		
		return entities;
	}

	/**
	 * get entities by uuid, name, other property
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Set<Entity> getEntities(String key, String value)
	{
		
		
		// get by NAME
		if ("name".equalsIgnoreCase(key))
			return getEntitiesByName(value);
		
		Set<Entity> entities = new HashSet<Entity>();
		
		// get by UUID
		if ("uuid".equalsIgnoreCase(key))
		{
			Entity e = getEntityByUUID(value);
			if (null != e)
				entities.add(e);
			return entities;
		}
		
		// get by OTHER PROPERTY
		for (Entity e : ideMap.values())
		{
			if (value.equals(e.getProperty(key)))
				entities.add(e);
		}

		return entities;
	}	

	public Set<Entity> getEntitiesByName(String entityName)
	{
		
		Set<Entity> entities = new HashSet<Entity>();
		for (Entity e : ideMap.values())
		{
			// if match key & value - return it
			if (entityName.equals(e.getName()))
			{
				entities.add(e);
			}
		}

		return entities;
	}	
	
	/**
	 * return first entity with the name
	 * 
	 * @param entityName
	 * @return
	 */
	public Entity getEntityByName(String entityName)
	{
		Set<Entity> elist = getEntitiesByName(entityName);
		
		if (null != elist && elist.size() > 0)
			return elist.iterator().next();

		return null;
	}	
	
	/**
	 * read only
	 * for add use public void add(Relation... relations)
	 * 
	 * @return
	 */
	private Map<String, Relation> getRelationIdMap() {
		return idRelationMap;
	}
	
	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public Set<String> getDeletedEntityIds() {
		return deletedEntityIds;
	}

	public Set<String> getDeletedRelationIds() {
		return deletedRelationIds;
	}	
		


	public Network copy()
	{
		Network clonedNet = (Network) ClassUtils.deepCloneBySerialization(this);

		return clonedNet;
	}

	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("size ");
		sb.append(getSize());
		sb.append("(" + ideMap.size() + "/ " + idRelationMap.size() + " )");

		sb.append("(e[ ");
		for (String eid : ideMap.keySet())
			sb.append(eid).append(" ");
		
		sb.append("] ");

		sb.append("r[ ");
		for (String rid : idRelationMap.keySet())
			sb.append(rid).append(" ");
			
		sb.append("])");
		
		return sb.toString();
	}

	public String toIds() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		for (String eid : ideMap.keySet())
		{
			if (first)
				first = false;
			else
				sb.append(" ");
				
			sb.append(eid);
		}
		
		first = true;
		for (String rid : idRelationMap.keySet())
		{
			if (first)
				first = false;
			else
				sb.append(" ");
				
			sb.append(rid);
		}
			
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((deletedEntityIds == null) ? 0 : deletedEntityIds.hashCode());
		result = prime
				* result
				+ ((deletedRelationIds == null) ? 0 : deletedRelationIds
						.hashCode());
		result = prime * result
				+ ((idRelationMap == null) ? 0 : idRelationMap.hashCode());
		result = prime * result + ((ideMap == null) ? 0 : ideMap.hashCode());
		result = prime * result + (modified ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Network other = (Network) obj;
		if (deletedEntityIds == null) {
			if (other.deletedEntityIds != null)
				return false;
		} else if (!deletedEntityIds.equals(other.deletedEntityIds))
			return false;
		if (deletedRelationIds == null) {
			if (other.deletedRelationIds != null)
				return false;
		} else if (!deletedRelationIds.equals(other.deletedRelationIds))
			return false;
		if (idRelationMap == null) {
			if (other.idRelationMap != null)
				return false;
		} else if (!idRelationMap.equals(other.idRelationMap))
			return false;
		if (ideMap == null) {
			if (other.ideMap != null)
				return false;
		} else if (!ideMap.equals(other.ideMap))
			return false;
		if (modified != other.modified)
			return false;
		return true;
	}
	
	/**
	 * returns true if empty
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		if (0 == ideMap.size() && 0 == idRelationMap.size())
			return true;
			
		return false;
	}
	
	/**
	 * return Entity or Relation by Id (first search for entity, if not found -> search for relation)
	 * 
	 * @param id
	 * @return
	 */
	public ERBase getById(String id)
	{
		ERBase erBase = ideMap.get(id);
		
		if (null != erBase)
			return erBase;

		return idRelationMap.get(id);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Set<ERBase> get(String key, String value)
	{
		Set<ERBase> erset = new HashSet<ERBase>();
		erset.addAll(getEntities(key, value));
		erset.addAll(getRelations(key, value));
		
		return erset;
	}
	
	public ERBase getFirst(String key, String value)
	{
		Set<ERBase> erset = new HashSet<ERBase>();
		erset.addAll(getEntities(key, value));
		if (erset.size() > 0)
			return erset.iterator().next();
		
		erset.addAll(getRelations(key, value));
		
		if (erset.size() > 0)
			return erset.iterator().next();
	
		return null;
	}
	
	/**
	 * Gets network size
	 * 
	 * @return
	 */
	public long getSize()
	{
		long size = ideMap.size() + idRelationMap.size();
		
		return size;
	}

	/**
	 * 
	 * 
	 */
	public void cleanup()
	{
		deletedEntityIds.clear();
		deletedRelationIds.clear();
		return;
	}

	/**
	 * restore logger after de-serialization
	 * 
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException
	{
		logger = Logger.getLogger(Network.class.getName());
		return this;
	}

}