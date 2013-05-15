package org.neuro4j.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class N4JConfig {
		
	private static Properties config = null;
	
	
	static {
		config = loadProperties("neuro4j.properties");
	}
	
	private N4JConfig() { }
	
	

	private static Properties loadProperties(String fName)
	{
		Properties config = new Properties();
		InputStream is = null;
		try 
		{
			is = N4JConfig.class.getClassLoader().getResourceAsStream(fName);
			config.load(is);
		} catch (Exception e) {
			throw new RuntimeException("can't read  " + fName, e);
		} finally {
			if (null != is)
			{
				try {
					is.close();
				} catch (IOException e) { }
			}
		}
		return config;
	}
	
    public static String getProperty(String key)
    {
        return config.getProperty(key);
    }	
     
    public static Properties getProperties()
    {
        return (Properties) config.clone();
    }	
     
    public static Set<String> getPropertyKeys()
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
