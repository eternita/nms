package org.neuro4j.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class KVUtils {

	
	private final static Logger logger = Logger.getLogger(KVUtils.class.getName());

	public static String getStringProperty(Properties properties, String key)
	{
		String valueStr = properties.getProperty(key);		
		return valueStr;
	}
	
	public static String getStringProperty(Properties properties, String key, String defaultValue)
	{
		String valueStr = properties.getProperty(key);		
		if (null == valueStr)
			valueStr = defaultValue;
		
		return valueStr;
	}
	
	public static String getStringProperty(Map<String, String> properties, String key, String defaultValue)
	{
		String valueStr = properties.get(key);		
		if (null == valueStr)
			valueStr = defaultValue;
		
		return valueStr;
	}
	
	/**
	 * read property value
	 * 
	 * @param properties
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getIntProperty(Properties properties, String key, int defaultValue)
	{
		int value = defaultValue;
		String valueStr = properties.getProperty(key);
		if (null != valueStr)
		{
			try 
			{
				value = Integer.parseInt(valueStr);
			} catch (Exception ex) {
				logger.severe(key + " should be integer but it's " + value + ". Default value is used: " + defaultValue + " " + ex);
				value = defaultValue;
			}
		}
		
		return value;
	}
	
	/**
	 * read property value
	 * 
	 * @param properties
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBooleanProperty(Properties properties, String key, boolean defaultValue)
	{
		boolean value = defaultValue;
		String valueStr = properties.getProperty(key);
		if (null != valueStr)
		{
			try 
			{
				value = Boolean.parseBoolean(valueStr);
			} catch (Exception ex) {
				logger.severe(key + " should be boolean but it's " + value + ". Default value is used: " + defaultValue + " " + ex);
				value = defaultValue;
			}
		}
		
		return value;
	}
	
   /**
	* representation.1.uuid=auuid
	* representation.1.proxy.impl=bimpl
	* representation.1.data.class=cdata
	* 
	* representation.2.uuid=aa
	* representation.2.proxy.impl=bb
	* representation.2.data.class=cc
	* 
	* 1 uuid=aa
	* 1 proxy.impl=bb
	* 1 data.class=cc
	* 
	* 2 uuid=aa
	* 2 proxy.impl=bb
	* 2 data.class=cc
	* 
	* 
	* @param filter
	* @return
	*/
	public static Map<Integer, Map<String, String>> filterArrayMap(Map<String, String> inputMap, String filter) {
		Map<Integer, Map<String, String>> arrayMap = new HashMap<Integer, Map<String, String>>();
		for (String k : inputMap.keySet())
		{
			if (k.matches("^" + filter + "\\.(\\d)*(.)*"))
			{
				String afterStr = k.replaceAll("^" + filter + "\\.", "");
				int fistDotIdx = afterStr.indexOf('.');
				
				String numberStr = afterStr.substring(0, fistDotIdx);
				String newKey = afterStr.substring(fistDotIdx + 1);
				
				int n = Integer.parseInt(numberStr);
				
				Map<String, String> innerMap = arrayMap.get(n);
				if (null == innerMap)
				{
					innerMap = new HashMap<String, String>();
					arrayMap.put(n, innerMap);
				}
				
				innerMap.put(newKey, inputMap.get(k));
			}
		}
			
		return arrayMap;
	}	
	
	public static Set<String> filterOutKeys(Set<String> input, String filter) {
		Set<String> output = new LinkedHashSet<String>();
		for (String key : input)
		{
			if (!key.matches("^" + filter + "\\.(\\d)*(.)*"))
				output.add(key);
		}
		
		return output;
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
	public static int getMaxPropertyArrayIdx(String key, Map<String, String> properties)
	{
		int maxIdx = -1;
		for (String k : properties.keySet())
		{
			if (k.matches("^" + key + "\\.(\\d)*(.)*"))
			{
				String nStr = k.replaceAll("^" + key + "\\.", "");
				int fistDotIdx = nStr.indexOf('.');
				if (-1 < fistDotIdx)
					nStr = nStr.substring(0, fistDotIdx);

				
				int n = Integer.parseInt(nStr);
				if (n > maxIdx)
					maxIdx = n;
			}
		}
		return maxIdx;
	}	
	
	public static int getRepresentationArrayIdxByUUIDValue(String prefix, String uuidKey, String uuidValue, Map<String, String> properties)
	{
		int idx = -1;
		for (String k : properties.keySet())
		{
			if (k.matches("^" + prefix + "\\.(\\d)*(.)*"))
			{
				if (uuidValue.equals(properties.get(k)))
				{
					String afterStr = k.replaceAll("^" + prefix + "\\.", "");
					int fistDotIdx = afterStr.indexOf('.');
					if (-1 < fistDotIdx)
					{
						String prKey = afterStr.substring(fistDotIdx + 1);
						if (uuidKey.equals(prKey))
						{
							String nStr = afterStr.substring(0, fistDotIdx);
							idx = Integer.parseInt(nStr);
							return idx;
						}
					}
				}
			}
		}
		return idx;
	}	
	
	public static Properties loadProperties(InputStream is)
	{
		Properties config = new Properties();
		
		if (null == is)
			return config;
			
		try 
		{
			config.load(is);
		} catch (Exception e) {
			logger.severe("can't read app_config properties from stream " + e);
			throw new RuntimeException("can't read app_config properties from stream ", e);
		}
		return config;
	}
	
	/**
	 * Load properties from file
	 * 
	 * @param file
	 * @return
	 */
	public static Properties loadProperties(File file)
	{
		Properties config = new Properties();
		
		if (!file.exists() || !file.isFile() )
			return config;
			
		InputStream is = null;
		try 
		{
			is = new FileInputStream(file);
			config.load(is);
		} catch (Exception e) {
			logger.severe("can't read app_config properties " + file + " " + e);
			throw new RuntimeException("can't read app_config properties " + file, e);
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
	
	/**
	 * Load properties from codebase using .class.getClassLoader().getResourceAsStream(...);
	 * 
	 * @param fName
	 * @return
	 */
	public static Properties loadPropertiesFromCodebase(String fName)
	{
		Properties config = new Properties();

		InputStream is = null;
		try 
		{
			is = KVUtils.class.getClassLoader().getResourceAsStream(fName);
			config.load(is);
		} catch (Exception e) {
			logger.severe("can't read app_config properties " + fName + " " + e);
			throw new RuntimeException("can't read app_config properties " + fName, e);
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
}
