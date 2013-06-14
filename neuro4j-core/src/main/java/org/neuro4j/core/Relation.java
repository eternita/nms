package org.neuro4j.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public class Relation extends ERBase implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;
	
	public Relation() {
		super();
	}

	public Relation(String name) {
		super(name);
	}

	
	public Relation(String name, Entity... participants) {
		this(name);
		Map<String, ERBase> participantsMap = new HashMap<String, ERBase>();
		for (Entity rp : participants)
		{
			participantsMap.put(rp.getUuid(), rp);
			rp.addRelation(this);
		}
		this.connected = participantsMap;
        setModified(true);
	}
	
	
	/**
	 * May not return all entities!!
	 * Returns loaded entities only
	 * 
	 * @return
	 */
	public Collection<Entity> getParticipants() {
		
		Set<Entity> participants = new LinkedHashSet<Entity>();
		for (ERBase r : this.connected.values())
		{
			if (null != r) // can be NULL if not loaded
				participants.add((Entity) r);
		}
		
		return participants;
	}

	/**
	 * May not return all entities!!
	 * Returns loaded entities only
	 * 
	 * @return
	 */
	public Set<Entity> getRelations(String propertyName, String propertyValue) {
		Set<Entity> entities = new LinkedHashSet<Entity>();
		for (ERBase e : getConnected(propertyName, propertyValue))
			entities.add((Entity) e);

		return entities;
	}
	
	public Collection<Entity> getAllParticipants() {
		if (!isCompleteLoaded())
			throw new RuntimeException("Relation " + uuid + " is not complete loaded)");
		
		return getParticipants();
	}

	public Set<String> getParticipantsKeys() {
		return getConnectedKeys();
	}
	
	
	public Entity getParticipant(String eid) {
		return (Entity) connected.get(eid);
	}

	
	/**
	 * ! in case of self-relation (sameId -> sameId) will exclude one relation part with sameId 
	 * 
	 * @param excludeEntityName to exclude
	 * @return
	 */
	public Set<Entity> getParticipants(String excludeEntityUUID) {
		
		Set<Entity> participants = new LinkedHashSet<Entity>();
		int excludeCount = 0;
		for (ERBase rp : this.connected.values())
		{

			if (null == rp)
				continue;
				
			if (excludeCount > 0 || !rp.getUuid().equals(excludeEntityUUID))
			{
				participants.add((Entity) rp);
			}

			if (rp.getUuid().equals(excludeEntityUUID))
				excludeCount ++;
		}
		return participants;
	}
	
	public Set<Entity> getAllParticipants(String excludeEntityUUID) {
		
		if (!isCompleteLoaded())
			throw new RuntimeException("Relation " + uuid + " is not complete loaded)");
			
		return getParticipants(excludeEntityUUID);
	}

	public Set<String> getParticipantsKeys(String excludeEntityUUID) {
		Set<String> participants = new LinkedHashSet<String>();
		participants.addAll(connected.keySet());
		participants.remove(excludeEntityUUID);
		
		return participants;
	}	

	public void addParticipant(Entity entity)
	{
		this.connected.put(entity.getUuid(), entity);
//		entity.addRelation(this);
		if (null != entity)
			addConnectedTail(entity);
        
		setModified(true);
	}
	
	/**
	 * add stub only
	 * 
	 * @param uuid
	 */
	public void addParticipant(String uuid)
	{
		if (null == connected.get(uuid))
		{
			connected.put(uuid, null);
	        setModified(true);
		}
		return;
	}
	
	public void removeParticipants()
	{
		connected.clear();
	}
	
	public void removeParticipant(String uuid)
	{
		ERBase entity = connected.remove(uuid);
		
		if (entity instanceof Entity)
		{
			((Entity) entity).removeRelation(this.getUuid());
			setModified(true);
		}
		return;
	}
	
	/**
	 * second level call
	 * 
	 * @param uuid
	 */
	public void removeParticipantTail(String uuid)
	{
		if(null != connected.remove(uuid))
			setModified(true);
		
		return;
	}

	public void remove()
	{
		for (ERBase rp : this.connected.values())
		{
			if (null != rp)
				((Entity) rp).removeRelation(this.getUuid());
		}
		connected.clear();
        setModified(true);
	}

	@Override
	public String toString() {
		return "{" + getName() + " - " + connected
				+ "}";
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relation other = (Relation) obj;
		if (connected == null) {
			if (other.connected != null)
				return false;
		} else if (!connected.equals(other.connected))
			return false;
		return true;
	}

	public boolean equalsData(Relation obj) {
		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;
		Relation other = (Relation) obj;
		for (String otherEid : other.getParticipantsKeys())
		{
			if (!connected.containsKey(otherEid))
				return false;
		}
		for (String currentRP : getParticipantsKeys())
		{
			if (!other.getParticipantsKeys().contains(currentRP))
				return false;
		}
		

		return true;
	}
	
	public Relation cloneBase()
	{
		return (Relation) super.cloneBase();
	}
	
	public Relation copyBase()
	{
		return (Relation) super.copyBase();
	}

//	public Relation cloneWithConnectedKeys()
//	{
//		return (Relation) super.cloneWithConnectedKeys();
//	}
}
