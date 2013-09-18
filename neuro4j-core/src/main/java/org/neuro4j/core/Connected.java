package org.neuro4j.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neuro4j.mgr.uuid.UUIDMgr;
import org.neuro4j.utils.KVUtils;


public class Connected extends KVBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;
	
	private String name;
	protected Date lastModifiedDate;
	private boolean modified = false;
	private boolean virtual = false;

	protected Map<String, Connected> connected = new HashMap<String, Connected>();
	
	public Connected() {
		super();
		this.name = getUuid();
		setModified(true);
	}
	
	
	public Connected(String name) {
		this();
		this.name = name;
		setModified(true);
	}
	
	public Connected(String name, Connected... connected) {
		this(name);
		Map<String, Connected> connectedMap = new HashMap<String, Connected>();
		for (Connected rp : connected)
		{
			connectedMap.put(rp.getUuid(), rp);
			rp.addConnected(this);
		}
		this.connected = connectedMap;
        setModified(true);
	}
	
	public String getName() {
		return name;
	}
	
	public String getProperty(String key) {
		if ("name".equalsIgnoreCase(key))
			return getName();
		
		if ("id".equalsIgnoreCase(key))
			return getUuid();

		if ("uuid".equalsIgnoreCase(key))
			return getUuid();

		return properties.get(key);
	}
	
	public void setName(String name) {
		
		if (this.name.equals(name))
			return;
			
		this.name = name;
		setModified(true);
		return;
	}	
	
	public boolean isCompleteLoaded()
	{
		for (String key : connected.keySet())
			if (null == connected.get(key))
				return false;
		
		return true;
	}

	/**
	 * second derivative call/link update. Eg: something -> r.addParticipant(e) -> e.addConnectedTail(r) 
	 * 
	 * @param erBase
	 */
	protected void addConnectedTail(Connected erBase)
	{
		erBase.connected.put(this.getUuid(), this);
	}

	public void addConnected(Connected erBase)
	{
		this.connected.put(erBase.getUuid(), erBase);
		
		if (null != erBase)
			addConnectedTail(erBase);
        
		setModified(true);
	}
	
	/**
	 * add stub only
	 * 
	 * @param id
	 */
	public void addConnected(String id)
	{
		if (null == connected.get(id))
		{
			connected.put(id, null);
			setModified(true);
		}
	}
	
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}	
	
	public void removeProperty(String key) {
		super.removeProperty(key);
		setModified(true);
	}
	
	public void removeProperties() {
		super.removeProperties();
		setModified(true);
	}
	
	
	public void setProperty(String key, String value)
	{
		if ("name".equalsIgnoreCase(key))
			setName(value);
		
		if ("id".equalsIgnoreCase(key))
			setUuid(value);

		if ("uuid".equalsIgnoreCase(key))
			setUuid(value);
		
		super.setProperty(key, value);
		setModified(true);
	}
	
	public void clear()
	{
		super.clear();
		this.name = null;
		this.lastModifiedDate = null;
		return;
	}
	
	public Set<String> getConnectedKeys() {
		return connected.keySet();
	}
	
	public boolean isConnectedTo(String id) {
		return connected.containsKey(id);
	}
	
	/**
	 * 
	 * @param excludeId
	 * @return
	 */
	public Set<String> getConnectedKeys(String excludeId) {
		Set<String> connectedKeys = new LinkedHashSet<String>();
		for (String connectedId : this.connected.keySet())
		{
			if (!connectedId.equals(excludeId))
				connectedKeys.add(connectedId);
		}
		return connectedKeys;

	}
	
	/**
	 * May not return all connections!!
	 * Returns loaded connections only
	 * 
	 * @return
	 */
	public Set<Connected> getConnected(String propertyName, String propertyValue) {
		Set<Connected> filter = new LinkedHashSet<Connected>();
		for (Connected er : this.connected.values())
		{
			if (null != er) // can be null - if not loaded
			{
				String value = null;
				if ("name".equalsIgnoreCase(propertyName))
					value = er.getName();
				else if ("uuid".equalsIgnoreCase(propertyName))
					value = er.getUuid();
				else 
					value = er.getProperty(propertyName);
				
				if (null != value && value.equals(propertyValue))
					filter.add(er);

			}
		}
		return filter;
	}

	/**
	 * May not return all connections!!
	 * Returns loaded connections only
	 * 
	 * @return
	 */
	public Set<Connected> getConnected() {
		Set<Connected> filter = new LinkedHashSet<Connected>();
		for (Connected er : this.connected.values())
		{
			if (null != er) // can be null - if not loaded
				filter.add(er);
		}
		return filter;
	}
	
	public Set<Connected> getAllConnected() {
		
		if (this.connected.keySet().size() != this.connected.values().size())
			throw new RuntimeException("ERBase " + uuid + " is not complete loaded)");
		
		return getConnected();
	}
	
	/**
	 * 
	 * @param ids IDs to filter out
	 * @return
	 */
	public Set<Connected> getAllConnectedFiltered(String... ids) {
		
		Set<Connected> cset = getAllConnected();
		
		// filter out excluded
		if (null != ids)
		{
			for (String id : ids)
			{
				Connected exclude = this.connected.get(id);
				if (null != exclude)
					cset.remove(exclude);
			}
		}
		return cset;
	}
	
	public Connected getConnected(String id) {
		return this.connected.get(id);
	}
	
	public Set<Representation> getRepresentations()
	{
		return Representation.properties2representations(this.properties);
	}
	
	public void addRepresentation(Representation representation)
	{
		if (null == representation)
			return;
		
		Map<String, String> representationProperties = Representation.representation2properties(representation);
		
		int arrayIdx = KVUtils.getMaxPropertyArrayIdx(Representation.REPRESENTATION, this.properties);
		arrayIdx ++;
		
		for (String key : representationProperties.keySet())
		{
			this.properties.put(Representation.REPRESENTATION_PREFIX + arrayIdx + "." + key, representationProperties.get(key));
		}
		setModified(true);
		return;
	}
	
	public void removeRepresentation(Representation reprepsentation)
	{
		if (null == reprepsentation)
			return;

		Map<String, String> representationProperties = Representation.representation2properties(reprepsentation);
		int arrayIdx = KVUtils.getRepresentationArrayIdxByUUIDValue(Representation.REPRESENTATION, "uuid", reprepsentation.getUuid(), this.properties);		
		for (String key : representationProperties.keySet())
		{
			this.properties.remove(Representation.REPRESENTATION_PREFIX + arrayIdx + "." + key);
		}
		setModified(true);
		return;
	}
	
	/**
	 * copy object but create new UUID 
	 * 
	 * @return
	 */
	protected Connected copyBase()
	{
		Connected clone = cloneBase();
		
		clone.setUuid(UUIDMgr.getInstance().createUUIDString());
		
		return clone;
		
	}
	
	public Connected cloneBase()
	{
		Connected clone = null;
		try {
			clone = (Connected) this.getClass().newInstance();
			clone.setUuid(this.getUuid());
			clone.setName(this.getName());
			clone.setLastModifiedDate(this.getLastModifiedDate());
			clone.setVirtual(this.isVirtual());
			
			for (String key : this.properties.keySet())
				clone.setProperty(key, this.getProperty(key));

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return clone;
	}		
	
	public Connected cloneWithConnectedKeys()
	{
		Connected clone = cloneBase();
		for (String cid : this.getConnectedKeys())
			clone.connected.put(cid, null);

		return clone;
	}


	public boolean isVirtual() {
		return virtual;
	}


	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}	

	public void removeConnected()
	{
		String[] cids = connected.keySet().toArray(new String[connected.keySet().size()]);
		for(String cid : cids)
			removeConnected(cid);
		
		return;
	}

	public void removeConnected(String uuid)
	{
		Connected con = connected.remove(uuid);
		
		if (null != con)
		{
			con.removeConnectedTail(this.getUuid());
			setModified(true);
		}
		return;
	}	
	
	void removeConnectedTail(String uuid)
	{
		if(null != connected.remove(uuid))
			setModified(true);
		
	}
	
	public Map<String, List<Connected>> groupConnectedByName()
	{
		Map<String, List<Connected>> groupMap = new HashMap<String, List<Connected>>();
		for (Connected r : connected.values())
		{
			if (null == r) // if not loaded - skip it
				continue;

			String rName = r.getName();
			List<Connected> rList = groupMap.get(rName);
			if (null == rList)
			{
				rList = new ArrayList<Connected>();
				groupMap.put(rName, rList);
			}

			rList.add(r);
		} // for (Relation r : relations)

		return groupMap;
	}	
}
