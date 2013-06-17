package org.neuro4j.nms.server;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.neuro4j.NeuroManager;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.utils.KVUtils;

public class NMSServerConfig {
	
	public static final String STORAGE_PREFIX = "org.neuro4j.nms.server.storage.";
	public static final String STORAGE_CONFIG_FILE = "storage.properties";
	
	private static File homeConfigDir; 
	
	static {
		Map<String, String> env = System.getenv();
		String neuro4jHome = System.getProperty("NEURO4J_HOME");
		if (null == neuro4jHome)
			neuro4jHome = env.get("NEURO4J_HOME");

		if (null == neuro4jHome)
			throw new RuntimeException("NEURO4J_HOME is not set");

		homeConfigDir = new File(neuro4jHome); 
	}
	
	private static Map<String, NeuroStorage> storageMap = null;
	
	private Properties config = null;
	
	private static NMSServerConfig instance;
	
	private NMSServerConfig() {
		config = loadProperties("nms-config.properties");
		storageMap = loadStorages();
	}
	
	public static NMSServerConfig getInstance()
	{
		if (null == instance)
			instance = new NMSServerConfig();
		
		return instance;
	}
	
	/**
	 * 
	 * load storages configured in conf file to Map
	 */
	private Map<String, NeuroStorage> loadStorages() {
		Map<String, NeuroStorage> storageMap = new HashMap<String, NeuroStorage>();
		for (Object coreObj : config.keySet())
		{
			String core = (String) coreObj;
			if (!core.startsWith(STORAGE_PREFIX))
				continue;
			
			String coreHomeDir = config.getProperty(core); // core dir (relative path)

			File coreHome = new File(homeConfigDir, coreHomeDir);
			coreHomeDir = coreHome.getAbsolutePath(); 
			
			NeuroStorage storage = NeuroManager.newInstance().getNeuroStorage(coreHomeDir, STORAGE_CONFIG_FILE);
			storageMap.put(core.substring(STORAGE_PREFIX.length()), storage);
		}
		return storageMap;
	}
	
	public NeuroStorage getStorage(String name)
	{
		return storageMap.get(name);
	}

	public Set<String> getStorageNames()
	{
		return storageMap.keySet();
	}

	// accessed from setting.jsp
	public static Properties loadProperties(String fName)
	{
		return KVUtils.loadProperties(new File(homeConfigDir, fName));
	}
	
    public String getProperty(String key)
    {
        return config.getProperty(key);
    }	
     
    public Properties getProperties()
    {
        return (Properties) config.clone();
    }	
     
    public Set<String> getPropertyKeys()
    {
    	Set<String> keys = new HashSet<String>();
		for (Object keyObj : config.keySet())
		{
			String key = (String) keyObj;
			keys.add(key);
		}    	
        return keys;
    }	
     
}
