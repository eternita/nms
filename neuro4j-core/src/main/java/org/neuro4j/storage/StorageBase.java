package org.neuro4j.storage;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.utils.ClassloaderUtil;

public abstract class StorageBase implements NeuroStorage {

	protected Properties properties;

	public Properties getConfig() 
	{
		return properties;
	}
	
	public void init(Properties properties) throws StorageException {
		this.properties = properties;
		return;
	}

	/**
	 * 
	 * 
	 * @param directoryWithJarExtensions
	 * @throws StorageException
	 */
	protected void extendClassLoader(String directoryWithJarExtensions) throws StorageException
	{
		if (null == directoryWithJarExtensions || 0 == directoryWithJarExtensions.trim().length())
			return;
		
		File jarDir = new File(directoryWithJarExtensions);
		
		FilenameFilter jarFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".jar")) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		for (File file : jarDir.listFiles(jarFilter)) {
			if (!file.isDirectory()) {
				try {
					ClassloaderUtil.addSoftwareLibrary(file);
				} catch (Exception e1) {
					throw new StorageException("Can't load  " + directoryWithJarExtensions + " to application classloader", e1);
				}

			}
		}
		
		return;
	}
	
	public Entity getEntityByUUID(String entityUUID) throws StorageException
	{
		Entity e = null;
		Network net;
		try {
			net = query("select e[id=?]", new String[]{entityUUID});
			e = net.getEntityByUUID(entityUUID);
		} catch (NQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return e;
	}
	

	public Relation getRelationByUUID(String relationUUID) throws StorageException {
		Relation r = null;
		Network net;
		try {
			net = query("select r[id=?]", new String[]{relationUUID});
			r = net.getRelationByUUID(relationUUID);
		} catch (NQLException e) {
			e.printStackTrace();
		}
		return r;
	}	
	
	/**
	 * "select e[?] depth 1", new String[]{entityUUID}
	 * 
	 * 
	 */
	public Network query(String q, String[] parameters)  throws NQLException, StorageException {
		for (String param : parameters)
			q = q.replaceFirst("\\?", "'" + param + "'");
		
		return query(q);
	}	
	
}
