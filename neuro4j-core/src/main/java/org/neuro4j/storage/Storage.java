package org.neuro4j.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;


public interface Storage {
	
	public Properties getConfig();
	
	public void init(Properties properties) throws StorageException;
	
	public void ping() throws StorageException;

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
	
	public Connected getById(String entityUUID) throws StorageException;	

//	public Relation getRelationByUUID(String relationUUID) throws StorageException;
	
	public Network query(String q) throws NQLException, StorageException;

	public Network query(String q, String[] parameters) throws NQLException, StorageException;
}
