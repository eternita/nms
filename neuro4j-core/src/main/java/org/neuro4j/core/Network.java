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

	// id - er
	private Map<String, ERBase> ideMap = new HashMap<String, ERBase>();
	
	// deleted ER ids - used when persist network
	private Set<String> deletedEntityIds = new HashSet<String>();

	
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

	private void addTail(ERBase... entities)
	{
		

		for (ERBase entity : entities)
		{
			ERBase current = ideMap.get(entity.getUuid());
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
	
/*	
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
*/	
	public void add(ERBase... items)
	{
		add(true, items);
		return;
	}


	public void add(Network net)
	{
		for(ERBase er : net.getERBases())
			add(er);		
		
		return;
	}
	
	
	public void add(boolean withConnected, ERBase... ers)
	{
		addTail(ers);

		for (ERBase er : ers)
		{
			for (String connectedID : er.getConnectedKeys())
			{
				ERBase connected = ideMap.get(connectedID);

				if (withConnected)
				{
					// if connected not in Network map -> search it in er -> and then put to Network map
					if (null == connected)
					{
						connected = er.getConnected(connectedID);
						if (null != connected)
							ideMap.put(connectedID, connected);
					}
				}
				
				if (null != connected)
				{
					// bind if not binded already
					if (er != connected.getConnected(er.getUuid()))
						connected.addConnected(er);
				}
			}
		}

		return;
	}
	
	public void remove(ERBase... items)
	{
		for(ERBase item : items)
		{
			remove(item, false);
		}
		return;
	}
	

	
	/**
	 * For internal use. 
	 * Use carefully - it may lead to relations with missed entities (e.g. SolrStorage, RDBMSStorage, ...). 
	 * 
	 * @param entity
	 * @param force
	 */
	public void remove(ERBase entity, boolean force)
	{
		
		if (force)
		{
			// delete relations even they were not loaded (may lead to relations with missed entities)
			for (String rid : entity.getConnectedKeys())
			{
				ERBase r = entity.getConnected(rid);
				if (null != r)
					r.removeConnected(entity.getUuid());
			}
		} else {
			for (ERBase r : entity.getAllConnected())
			{
				r.removeConnected(entity.getUuid());
			}
		}
		
		deletedEntityIds.add(entity.getUuid());

		ideMap.remove(entity.getUuid());
		
		return;
	}
	
//	private void remove(Relation... relations)
//	{
//		for (Relation relation : relations)
//		{
//			// unbind entities
//			Set<String> eSet = new HashSet<String>(); // used to avoid set concurrent modification 
//			for (String rp : relation.getParticipantsKeys())
//				eSet.add(rp);
//
//			for (String eid : eSet)
//				relation.removeParticipant(eid);
//
//			this.idRelationMap.remove(relation.getUuid());
//			this.deletedRelationIds.add(relation.getUuid());
//		}
//	}
	
//	/**
//	 * 
//	 * @param relations
//	 */
//	public void add(Relation... relations)
//	{
//		add(true, relations);
//	}
	
/*	public void add(boolean withConnected, Relation... relations)
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
	}*/	
	

	
	/**
	 * Returns true if there is Entity or Relation with such id in the network
	 * 
	 * @param id
	 * @return
	 */
	public boolean contains(String id)
	{	
		return ideMap.containsKey(id);
	}

	/**
	 * Returns ids for all entities and relations in the network 
	 * 
	 * @return
	 */
	public String[] getIds()
	{
		return (String[]) ideMap.keySet().toArray(new String[]{});
	}
	
	public Iterator<ERBase> getERBaseIterator()
	{
		return getERBases().iterator();
	}
	
	public Set<ERBase> getERBases()
	{
		Set<ERBase> erids = new HashSet<ERBase>();
		
		erids.addAll(ideMap.values());
		
		return erids;
	}
	
	
	public Iterator<String> getERBaseIdsIterator()
	{
		return ideMap.keySet().iterator();
	}
	
//	
//	public String[] getEntities()
//	{
//		return (String[]) ideMap.keySet().toArray(new String[]{});
//	}

	
	public Set<ERBase> getByRegexp(String key, String regexp)
	{
		Set<ERBase> entities = new HashSet<ERBase>();
		
		for (ERBase e : ideMap.values())
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
	
	public Set<ERBase> getWithProperty(String key)
	{
		Set<ERBase> entities = new HashSet<ERBase>();
		
		for (ERBase e : ideMap.values())
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
	public Set<ERBase> get(String key, String value)
	{
		
		// get by NAME
		if ("name".equalsIgnoreCase(key))
		{
			Set<ERBase> entities = new HashSet<ERBase>();
			for (ERBase e : ideMap.values())
			{
				// if match key & value - return it
				if (value.equals(e.getName()))
				{
					entities.add(e);
				}
			}

			return entities;
		}
		
		Set<ERBase> entities = new HashSet<ERBase>();
		
		// get by UUID
		if ("uuid".equalsIgnoreCase(key))
		{
			ERBase e = getById(value);
			if (null != e)
				entities.add(e);
			return entities;
		}
		
		// get by OTHER PROPERTY
		for (ERBase e : ideMap.values())
		{
			if (value.equals(e.getProperty(key)))
				entities.add(e);
		}

		return entities;
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
		sb.append("(" + ideMap.size() + " )");

		sb.append("(e[ ");
		for (String eid : ideMap.keySet())
			sb.append(eid).append(" ");
		
		sb.append("] ");
		
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
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((deletedEntityIds == null) ? 0 : deletedEntityIds.hashCode());
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
		if (0 == ideMap.size())
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
		return ideMap.get(id);
	}


	
	public ERBase getFirst()
	{
		if (!ideMap.values().isEmpty())
			return ideMap.values().iterator().next();

		return null;
	}
	
	public ERBase getFirst(String key, String value)
	{
		Set<ERBase> erset = get(key, value);
		
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
		long size = ideMap.size();
		
		return size;
	}

	/**
	 * 
	 * 
	 */
	public void cleanup()
	{
		deletedEntityIds.clear();
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