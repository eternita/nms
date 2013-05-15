package org.neuro4j;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorException;
import org.neuro4j.logic.LogicProcessorFactory;
import org.neuro4j.logic.LogicProcessorNotFoundException;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.StorageFactory;
import org.neuro4j.storage.StorageNotFoundException;
import org.neuro4j.storage.StorageException;
import org.neuro4j.utils.KVUtils;


public class NeuroManager  {

	private final static Logger logger = Logger.getLogger(NeuroManager.class.getName());
	
	private Map<String, NeuroStorage> storageMap = new HashMap<String, NeuroStorage>();
	
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
	public NeuroStorage getNeuroStorage(String configFileName)
	{
		Properties props = KVUtils.loadProperties(configFileName);
		String storageImpl = props.getProperty("n4j.manager.storage");
		
		return getNeuroStorage(storageImpl, props);
	}
	
	public NeuroStorage getNeuroStorage(File configFile)
	{
		Properties props = KVUtils.loadProperties(configFile);
		String storageImpl = props.getProperty("n4j.manager.storage");
		
		return getNeuroStorage(storageImpl, props);
	}
	
	/**
	 * Creates NeuroStorage using implementation class name and properties
	 * 
	 * @param storageImpl
	 * @param properties
	 * @return
	 */
	public NeuroStorage getNeuroStorage(String storageImpl, Properties properties)
	{
		NeuroStorage storage = storageMap.get(storageImpl);
		if (null == storage)
		{
			try {
				storage = StorageFactory.getNeuroStorage(storageImpl);
			} catch (StorageNotFoundException e) {
				logger.severe("NeuroManager instantiation failed - can't instantiate NeuroStorage. " + e);
				throw new RuntimeException( "NeuroManager instantiation failed - can't instantiate NeuroStorage. " + e.getMessage());
			}
			try {
				storage.init(properties);
				storageMap.put(storageImpl, storage);
			} catch (StorageException e) {
				logger.severe("Can't instantiate storage " + storageImpl + " " + e);
			}
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
		Properties props = KVUtils.loadProperties(configFileName);
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
