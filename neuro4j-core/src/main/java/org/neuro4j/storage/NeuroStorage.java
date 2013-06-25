package org.neuro4j.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;


public interface NeuroStorage {
	
	public Properties getConfig();
	
	public void init(Properties properties) throws StorageException;

	public boolean save(Network network) throws StorageException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws StorageException
	 */
	public InputStream getRepresentationInputStream(String id) throws StorageException;
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * @throws StorageException
	 */
	public OutputStream getRepresentationOutputStream(String id) throws StorageException;
	
	public Entity getEntityByUUID(String entityUUID) throws StorageException;	

	public Relation getRelationByUUID(String relationUUID) throws StorageException;
	
	public Network query(String q) throws NQLException, StorageException;

	public Network query(String q, String[] parameters) throws NQLException, StorageException;
}
