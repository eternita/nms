package org.neuro4j.storage;

import java.util.logging.Logger;

public class StorageFactory {
	
	private final transient static Logger logger = Logger.getLogger(StorageFactory.class.getName());
		
	public static NeuroStorage getNeuroStorage(String name) throws StorageNotFoundException
	{
		try {
			Class clazz = Class.forName(name);
			Object fObj = clazz.newInstance();
			if (fObj instanceof NeuroStorage)
				return (NeuroStorage) fObj;
				
		} catch (ClassNotFoundException e) {
			logger.severe(e.getMessage());
		} catch (InstantiationException e) {
			logger.severe(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.severe(e.getMessage());
		}
		throw new StorageNotFoundException("NeuroStorage " + name + " not found");
	}
}
