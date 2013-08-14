package org.neuro4j.storage;

import java.util.logging.Logger;

public class StorageFactory {
	
	private final transient static Logger logger = Logger.getLogger(StorageFactory.class.getName());
		
	public static Storage getStorage(String name) throws StorageNotFoundException
	{
		try {
			Class clazz = Class.forName(name);
			Object fObj = clazz.newInstance();
			if (fObj instanceof Storage)
				return (Storage) fObj;
				
		} catch (ClassNotFoundException e) {
			logger.severe(e.getMessage());
		} catch (InstantiationException e) {
			logger.severe(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.severe(e.getMessage());
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
		throw new StorageNotFoundException("Storage " + name + " not found");
	}
}
