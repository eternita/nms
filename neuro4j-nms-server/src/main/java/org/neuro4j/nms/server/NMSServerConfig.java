package org.neuro4j.nms.server;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.neuro4j.NeuroManager;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.utils.KVUtils;

public class NMSServerConfig {
	
	private static final String NMS_CONFIG_FILE = "nms-config.properties";
	
	private static final String STORAGE_CONFIG_FILE = "storage.properties";
	
	// used in settings.jsp
	public static final String STORAGE_PREFIX = "org.neuro4j.nms.server.storage.";
	
	private static File nmsHomeDir; 
	
	static {
		Map<String, String> env = System.getenv();
		String nmsHome = System.getProperty("NMS_HOME");
		if (null == nmsHome)
			nmsHome = env.get("NMS_HOME");

		if (null == nmsHome)
			throw new RuntimeException("NMS_HOME is not set");

		nmsHomeDir = new File(nmsHome); 
	}
	
	private static Map<String, Storage> storageMap = null;
	
	private Properties config = null;
	
	private static NMSServerConfig instance;
	
	private NMSServerConfig() {
		config = loadProperties(NMS_CONFIG_FILE);
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
	private Map<String, Storage> loadStorages() {
		Map<String, Storage> storageMap = new HashMap<String, Storage>();
		for (Object storageObj : config.keySet())
		{
			String storageStr = (String) storageObj;
			if (!storageStr.startsWith(STORAGE_PREFIX))
				continue;
			
			String storageHomeDir = config.getProperty(storageStr); // storage dir (relative path)

			File storageHome = new File(nmsHomeDir, storageHomeDir);
			storageHomeDir = storageHome.getAbsolutePath(); // storage dir (absolute path)
			
			Storage storage;
			try {
				storage = NeuroManager.newInstance().getStorage(storageHomeDir, STORAGE_CONFIG_FILE);
				storageMap.put(storageStr.substring(STORAGE_PREFIX.length()), storage);
			} catch (StorageException e) {
				e.printStackTrace();
			}
		}
		return storageMap;
	}
	
	public Storage getStorage(String name)
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
		return KVUtils.loadProperties(new File(nmsHomeDir, fName));
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
