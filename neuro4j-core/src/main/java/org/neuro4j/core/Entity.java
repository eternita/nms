package org.neuro4j.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Entity extends ERBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;
	
	public Entity() {
		super();
	}

	public Entity(String name) {
		super(name);
	}
	
	/**
	 * May not return all relations!!
	 * Returns loaded relations only
	 * 
	 * @return
	 */
	public Set<Relation> getRelations() {
		Set<Relation> relations = new LinkedHashSet<Relation>();
		for (ERBase r : this.connected.values())
		{
			if (null != r) // can be null - if not loaded
				relations.add((Relation) r);
		}
		return relations;
	}
	
	/**
	 * May not return all relations!!
	 * Returns loaded relations only
	 * 
	 * @return
	 */
	public Set<Relation> getRelations(String propertyName, String propertyValue) {
		Set<Relation> relations = new LinkedHashSet<Relation>();
		for (ERBase r : getConnected(propertyName, propertyValue))
			relations.add((Relation) r);

		return relations;
	}
	
	public Set<Relation> getAllRelations() {
		if (!isCompleteLoaded())
			throw new RuntimeException("Entity " + uuid + " is not complete loaded)");

		return getRelations();
	}

	public Set<String> getRelationsKeys() {
		return connected.keySet();
	}
	
	public void removeRelation(String ruuid)
	{
		ERBase relation = connected.remove(ruuid);
		if (relation instanceof Relation)
		{
			((Relation) relation).removeParticipantTail(relation.getUuid());
			setModified(true);
		}
		return;
	}
	
	/**
	 *  something -> r.removeParticipant() -> removeRelationTail()
	 * 
	 * @param ruuid
	 */
	public void removeRelationTail(String ruuid)
	{
		if(null != connected.remove(ruuid))
			setModified(true);
		
		return;
	}
	
	public List<Relation> getRelations(String name) {
		List<Relation> rl = new ArrayList<Relation>();
		for (ERBase r : this.connected.values())
		{
			if(null == r)
				continue;
			
			if (name.equals(r.getName()))
				rl.add((Relation) r);
		}
		
		return rl;
	}
	
	public Relation getRelation(String ruuid) {
		return (Relation) this.connected.get(ruuid);
	}
	
	/**
	 * add stub only
	 * 
	 * @param ruuid
	 */
	public void addRelation(String ruuid)
	{
		if (null == connected.get(ruuid))
		{
			connected.put(ruuid, null);
			setModified(true);
		}
	}

	
	public void addRelation(Relation r)
	{
		connected.put(r.getUuid(), r);
		
		if (null != r)
			addConnectedTail(r);
		
		setModified(true);
	}
	

	
	@Override
	public String toString() {
		return "Entity [name=" + getName() + "]";
	}


	public Map<String, List<Relation>> groupRelationsByName()
	{
		Map<String, List<Relation>> groupMap = new HashMap<String, List<Relation>>();
		for (ERBase r : connected.values())
		{
			if (null == r) // if not loaded - skip it
				continue;
			
			String rName = r.getName();
			List<Relation> rList = groupMap.get(rName);
			if (null == rList)
			{
				rList = new ArrayList<Relation>();
				groupMap.put(rName, rList);
			}
			
			rList.add((Relation) r);
		} // for (Relation r : relations)
		
		return groupMap;
	}
	
	public Entity cloneBase()
	{
		return (Entity) super.cloneBase();
	}	
	
	public Entity copyBase()
	{
		return (Entity) super.copyBase();
	}	
	
//	public Entity cloneWithConnectedKeys()
//	{
//		return (Entity) super.cloneWithConnectedKeys();
//	}	
}
