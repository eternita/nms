package org.neuro4j.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.neuro4j.core.rep.RepresentationProxyException;
import org.neuro4j.core.rep.RepresentationProxyFactory;
import org.neuro4j.utils.KVUtils;


/**
 * Is used to represent raw data (e.g. byte arrays of images, video, ...)
 * Those data is stored outside Network (e.g. file system, remote server, etc, ...)
 * 
 * any type of representation (VAK {visual, auditorial, kinesthetic, ...} ) E.g. images or sounds or 
 *
 */
public class Representation extends KVBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;

	public static final String REPRESENTATION = "representation";
	public static final String REPRESENTATION_PREFIX = REPRESENTATION + ".";
	
	private static final String UUID_KEY = "uuid";
	private static final String PROXY_IMPL_KEY = "proxy.impl";
	private static final String DATA_CLASS_KEY = "data.class";
	
	
	public static Set<Representation> properties2representations(Map<String, String> properties)
	{
		Set<Representation> reps = new LinkedHashSet<Representation>();
		
		Map<Integer, Map<String, String>> arrayMap = KVUtils.filterArrayMap(properties, REPRESENTATION);		
		
		for (int n : arrayMap.keySet())
		{
			Map<String, String> repProperties = arrayMap.get(n);
			
			Representation rep = new Representation(repProperties);
			reps.add(rep);
		}
		
		return reps;
	}
	
	public static Map<String, String> representation2properties(Representation representation)
	{
		Map<String, String> properties = new HashMap<String, String>();
		properties.putAll(representation.getPropertyMap());
		return properties;
	}
	
	private Representation(Map<String, String> repProperties) {
		this.uuid = repProperties.get(UUID_KEY);
		setProperties(repProperties);
	}

	public Representation(String proxyImpl) {
		super();
		setProperty(UUID_KEY, getUuid());
		setProperty(PROXY_IMPL_KEY, proxyImpl);
	}
	
	@Override
	public void setUuid(String uuid) {
		super.setUuid(uuid);
		setProperty(UUID_KEY, getUuid());
	}

	public void setData(Object data) throws RepresentationProxyException {
		if (null != data)
			setProperty(DATA_CLASS_KEY, data.getClass().getCanonicalName());
		RepresentationProxyFactory.put(getProperty(PROXY_IMPL_KEY), data, this);
	}
	
	
	public Object getData() throws RepresentationProxyException {
		return RepresentationProxyFactory.get(getProperty(PROXY_IMPL_KEY), this);
	}
	
	
	@Override
	public String toString() {
		return "Representation [uuid=" + uuid + ", properties=" + properties + "]";
	}
}
