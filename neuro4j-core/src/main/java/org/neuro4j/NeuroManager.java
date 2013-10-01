package org.neuro4j;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorException;
import org.neuro4j.logic.LogicProcessorFactory;
import org.neuro4j.logic.LogicProcessorNotFoundException;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageConfig;
import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.StorageFactory;
import org.neuro4j.utils.KVUtils;


public class NeuroManager  {

	private final static Logger logger = Logger.getLogger(NeuroManager.class.getName());
	
	private Map<String, Storage> storageMap = new HashMap<String, Storage>();
	
	private Map<String, LogicProcessor> logicProcessorMap = new HashMap<String, LogicProcessor>();

	private NeuroManager() { }
	
	public static NeuroManager newInstance()
	{
		return new NeuroManager();
	}

	/**
	 * Creates NeuroStorage using config file
	 * 
	 * @param configFileName
	 * @return
	 */
	public Storage getStorage(String configFile) throws StorageException
	{
		Properties props = KVUtils.loadPropertiesFromCodebase(configFile);
		String storageImpl = props.getProperty(StorageConfig.STORAGE_IMPL_CLASS);
		
		return getStorage(storageImpl, props);
	}
	
	/**
	 * Creates NeuroStorage using config file
	 * 
	 * @param configFileName
	 * @return
	 */
	public Storage getStorage(File configFile) throws StorageException
	{
		Properties props = KVUtils.loadProperties(configFile);
		String storageImpl = props.getProperty(StorageConfig.STORAGE_IMPL_CLASS);
		
		return getStorage(storageImpl, props);
	}
	
	/**
	 * 
	 * @param storageHomeDirStr
	 * @param configFileStr
	 * @return
	 */
	public Storage getStorage(String storageHomeDirStr, String configFileStr) throws StorageException
	{
		File storageHomeDir = new File(storageHomeDirStr);
		
		File configFile = new File(storageHomeDir, configFileStr);
		
		Properties props = KVUtils.loadProperties(configFile);
		String storageImpl = props.getProperty(StorageConfig.STORAGE_IMPL_CLASS);
		props.put(StorageConfig.STORAGE_HOME_DIR, storageHomeDir.getAbsolutePath());
		
		return getStorage(storageImpl, props);
	}
	
	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	public Storage getStorage(InputStream inputStream) throws StorageException
	{
		Properties props = KVUtils.loadProperties(inputStream);
		String storageImpl = props.getProperty(StorageConfig.STORAGE_IMPL_CLASS);
		
		return getStorage(storageImpl, props);
	}
	
	/**
	 * Creates NeuroStorage using implementation class name and properties
	 * 
	 * @param storageImpl
	 * @param properties
	 * @return
	 */
	public Storage getStorage(String storageImpl, Properties properties) throws StorageException
	{
		Storage storage = storageMap.get(storageImpl);
		if (null == storage)
		{
			storage = StorageFactory.getStorage(storageImpl);
			storage.init(properties);
			
			// run ping query
			try {
				storage.query("SELECT (id='test-ping-during-init')");
			} catch (NQLException e) {
				throw new StorageException("Can't run ping query for storage " + storageImpl, e);
			}
			
			storageMap.put(storageImpl, storage);
		}
			
		
		return storage;
	}

	/**
	 * Creates LogicProcessor using config file
	 * 
	 * @param configFileName
	 * @return
	 */
	public LogicProcessor getLogicProcessor(String configFileName)
	{
		Properties props = KVUtils.loadPropertiesFromCodebase(configFileName);
		String logicProcessorImpl = props.getProperty("n4j.manager.logic-processor");
		
		return getLogicProcessor(logicProcessorImpl, props);
	}
	
	/**
	 * Creates LogicProcessor using implementation class name and properties
	 * 
	 * @param logicProcessorImpl
	 * @param properties
	 * @return
	 */
	public LogicProcessor getLogicProcessor(String logicProcessorImpl, Properties properties)
	{
		LogicProcessor logicProcessor = logicProcessorMap.get(logicProcessorImpl);
		if (null == logicProcessor)
		{
			try {
				logicProcessor = LogicProcessorFactory.getLogicProcessor(logicProcessorImpl);
			} catch (LogicProcessorNotFoundException e) {
				logger.severe( "NeuroManager instantiation failed - can't instantiate LogicProcessor. " + e);
				throw new RuntimeException( "NeuroManager instantiation failed - can't instantiate LogicProcessor. " + e.getMessage());
			}
			try {
				logicProcessor.init(properties);
				logicProcessorMap.put(logicProcessorImpl, logicProcessor);
			} catch (LogicProcessorException e) {
				logger.severe( "Can't instantiate logic processor  " + logicProcessorImpl + " " + e);
			}
		}
			
		return logicProcessor;
	}

}
