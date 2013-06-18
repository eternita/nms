package org.neuro4j.storage;

public interface StorageConfig {

	/**
	 * 
	 */
	public static final String STORAGE_IMPL_CLASS = "n4j.manager.storage";

	/**
	 * path to storage home directory
	 */
	public static final String STORAGE_HOME_DIR = "n4j.storage.home_dir";
	
	/**
	 * path (can be relative if storage home specified) to directory with *.jar extensions for a storage
	 */
	public static final String STORAGE_LIB_DIR = "n4j.storage.lib_dir";
	
	/**
	 * 
	 */
	public static final String STORAGE_READ_ONLY = "n4j.storage.read_only";
	
	/**
	 * 
	 * Max fully loaded connections (e.g. relations per relation group name)
	 * 
	 * E.g. Imagine user uploaded 1000 photos (have 1000 relations). 
	 * Queried network will have MAX_QUERIED_CONNECTIONS_LIMIT loaded relations for user object (others - just ids)
	 * 
	 * MAX_QUERIED_CONNECTIONS_LIMIT is used for single entity / relation 
	 * 
	 */
	public static final String MAX_QUERIED_CONNECTIONS_LIMIT = "n4j.storage.max_queried_connections_limit";
	
	
}
