package org.neuro4j.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neuro4j.mgr.uuid.UUIDMgr;
import org.neuro4j.utils.KVUtils;

/**
 * Key-Value Base implementation
 * 
 *
 */
public class KVBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;
	
	protected String uuid;

	protected Map<String, String> properties = new HashMap<String, String>();

	public KVBase() {
		this.uuid = UUIDMgr.getInstance().createUUIDString();
	}
	

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void removeProperty(String key) {
		properties.remove(key);
	}
	
	public void removeProperties() {
		properties.clear();
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}

	public Set<String> getPropertyKeys() {
		
		return KVUtils.filterOutKeys(properties.keySet(), Representation.REPRESENTATION);
	}
	
	public Set<String> getPropertyKeysWithRepresentations() {
		return properties.keySet();
	}
	
	public Map<String, String> getPropertyMap() {
		Map<String, String> m = new HashMap<String, String>();
		m.putAll(properties);
		return m;
	}
	
	public void setProperties(Map<String, String> properties)
	{
		this.properties.putAll(properties);
	}
	
	public void setProperty(String key, String value)
	{
		properties.put(key, value);
	}
	
	public void clear()
	{
		
		properties.clear();
		this.uuid = null;
		return;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		KVBase other = (KVBase) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	

	/**
	 * my.key.1 = a
	 * my.key.2 = b
	 * my.key.3 = c
	 * 
	 * @param key
	 * @return
	 */
	public List<String> getPropertyArray(String key) {
		List<String> arrayValues = new ArrayList<String>();
		for (String k : properties.keySet())
		{
			if (k.matches("^" + key + "\\.(\\d)*"))
				arrayValues.add(getProperty(k));
		}
			
		return arrayValues;
	}
	
	/**
	 * return
	 * 1 = a
	 * 2 = b
	 * 3 = c 
	 * 
	 * for key = my.key 
	 * 
	 * my.key.1 = a
	 * my.key.2 = b
	 * my.key.3 = c
	 * 
	 * @param key
	 * @return
	 */
	public Map<Integer, String> getPropertyArrayMap(String key) {
		Map<Integer, String> arrayMap = new HashMap<Integer, String>();
		for (String k : properties.keySet())
		{
			if (k.matches("^" + key + "\\.(\\d)*"))
			{
				String nStr = k.replaceAll("^" + key + "\\.", "");
				int n = Integer.parseInt(nStr);
				
				arrayMap.put(n, getProperty(k));
			}
		}
			
		return arrayMap;
	}
	
	
	/**
	 * 
	 * for key = my.key 
	 * 
	 * store data as flat array
	 * 
	 * my.key.0 = value
	 * 
	 * or
	 * 
	 * my.key.0 = something
	 * my.key.1 = value
	 * 
	 * @param key
	 * @param value
	 */
	public void setPropertyAsArray(String key, String value)
	{
		int maxIdx = getMaxPropertyArrayIdx(key, properties);
		setProperty(key + "." + (maxIdx + 1), value);
	}	
	
	
	/**
	 *return 3
	 * 
	 * for key = my.key 
	 * 
	 * my.key.1 = a
	 * my.key.2 = b
	 * my.key.3 = c
	 * 
	 *  
	 * @param key
	 * @return
	 */
	private int getMaxPropertyArrayIdx(String key, Map<String, String> properties)
	{
		int maxIdx = -1;
		for (String k : properties.keySet())
		{
			if (k.matches("^" + key + "\\.(\\d)*"))
			{
				String nStr = k.replaceAll("^" + key + "\\.", "");
				int n = Integer.parseInt(nStr);
				if (n > maxIdx)
					maxIdx = n;
			}
		}
		return maxIdx;
	}


	@Override
	public String toString() {
		return "KVBase [uuid=" + uuid + ", properties=" + properties + "]";
	}
	
	
}
