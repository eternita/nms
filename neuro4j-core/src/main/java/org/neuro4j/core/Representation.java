package org.neuro4j.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.utils.IOUtils;
import org.neuro4j.utils.KVUtils;


/**
 * Is used to represent raw data (e.g. byte arrays of images, video, ...)
 * Those data is stored outside Network (e.g. file system, remote server, etc, ...)
 * 
 *
 */
public class Representation extends KVBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;

	public static final String REPRESENTATION = "representation";
	public static final String REPRESENTATION_PREFIX = REPRESENTATION + ".";
	
	private static final String UUID_KEY = "uuid";
	private static final String DATA_TYPE_KEY = "type";
	
	public static Set<Representation> properties2representations(Map<String, String> properties)
	{
		Set<Representation> reps = new LinkedHashSet<Representation>();
		
		Map<Integer, Map<String, String>> arrayMap = KVUtils.filterArrayMap(properties, REPRESENTATION);		
		
		for (int n : arrayMap.keySet())
		{
			Map<String, String> repProperties = arrayMap.get(n);
			
			Representation rep = new Representation(repProperties);
			reps.add(rep);
		}
		
		return reps;
	}
	
	public static Map<String, String> representation2properties(Representation representation)
	{
		Map<String, String> properties = new HashMap<String, String>();
		properties.putAll(representation.getPropertyMap());
		return properties;
	}
	
	private Representation(Map<String, String> repProperties) {
		this.uuid = repProperties.get(UUID_KEY);
		setProperties(repProperties);
	}
	public Representation() {
		super();
	}
	
	@Override
	public void setUuid(String uuid) {
		super.setUuid(uuid);
		setProperty(UUID_KEY, getUuid());
	}
		
	public void setData(Storage storage, InputStream data) throws StorageException {
		if (null != data)
			setProperty(DATA_TYPE_KEY, data.getClass().getCanonicalName());
		
		setDataImpl(storage, data);
	}
	
	public void setData(Storage storage, byte[] data) throws StorageException {
		
		if (null == data)
			throw new StorageException("Can't save data for representation " + getUuid() + ". Input data is NULL");
		
		setProperty(DATA_TYPE_KEY, data.getClass().getCanonicalName());
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		setDataImpl(storage, bais);
		return;
	}
	
	public void setData(Storage storage, Network data) throws StorageException {
		if (null == data)
			throw new StorageException("Can't save data for representation " + getUuid() + ". Input network is NULL");
			
		setProperty(DATA_TYPE_KEY, data.getClass().getCanonicalName());
		
		String idStr = data.toIds();
		ByteArrayInputStream bais = new ByteArrayInputStream(idStr.getBytes());
		
		setDataImpl(storage, bais);
		return;
	}
	
	/**
	 * 
	 * @param storage
	 * @param data
	 * @throws StorageException
	 */
	private void setDataImpl(Storage storage, InputStream data) throws StorageException {
		if (null == storage)
			throw new StorageException("Can't save data for representation " + getUuid() + ". Storage is NULL");
		
		if (null == data)
			throw new StorageException("Can't save data for representation " + getUuid() + ". Input stream is NULL");

		setProperty(UUID_KEY, getUuid());
		
		OutputStream output = storage.getRepresentationOutputStream(getUuid());
		if (null == output)
			throw new StorageException("Can't save data for representation " + getUuid() + ". Output stream is NULL");
		
		try {
			long size = IOUtils.copyLarge(data, output);
			setProperty("size", "" + size);
		} catch (IOException e) {
			throw new StorageException("Can't save data for representation " + getUuid(), e);
		} catch (NullPointerException e) {
			throw new StorageException("Can't save data for representation " + getUuid(), e);
		}
		return;
	}
	
	public Network getDataAsNetwork(Storage storage) throws StorageException {
		String dataClass = getProperty(DATA_TYPE_KEY);
		if (!"org.neuro4j.core.Network".equals(dataClass))
			throw new StorageException("Representation's (" + getUuid() + ") type is not org.neuro4j.core.Network. It's '" + dataClass + "'");
		
		InputStream input = storage.getRepresentationInputStream(getUuid());
		if (null == input)
			throw new StorageException("Can't read data from representation " + getUuid() + ". Input stream is NULL");
		
		byte[] ba;
		try {
			ba = IOUtils.toByteArray(input);
		} catch (IOException e) {
			throw new StorageException("Can't read data from representation " + getUuid(), e);
		} 
		String idStr = new String(ba);
		
		String[] ids = idStr.split(" ");
		
		Network net = getNetworkByIds(storage, ids);
		
		
		return net;
	}
	
	private Network getNetworkByIds(Storage storage, String[] ids)
	{
		Network outNet = new Network();
		StringBuffer qsb = new StringBuffer("select e("); // select e(id='F53DA3BEC6218003FA9A37EC23B8AAF8' OR id='F53DA3BEC6218003FA9A37EC23B8AAF8')

		boolean first = true;
		
		for (String eid : ids)
		{
			if (first)
				first = false;
			else
				qsb.append(" OR ");
				
			qsb.append("id='").append(eid).append("'");
		}
		qsb.append(")");
		
		Network eNet = null;
		try {
			eNet = storage.query(qsb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		outNet.add(eNet);
		
/*		qsb = new StringBuffer("select r("); // select r(id='F53DA3BEC6218003FA9A37EC23B8AAF8' OR id='F53DA3BEC6218003FA9A37EC23B8AAF8')

		first = true;
		
		for (String eid : ids)
		{
			if (first)
				first = false;
			else
				qsb.append(" OR ");
				
			qsb.append("id='").append(eid).append("'");
		}
		qsb.append(")");
		
		Network rNet = null;
		try {
			rNet = storage.query(qsb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		outNet.add(rNet);
*/		

		return outNet;
	}
	
	/**
	 * data type is byte[] 
	 * 
	 * @param storage
	 * @return
	 * @throws StorageException
	 */
	public byte[] getDataAsBytes(Storage storage) throws StorageException {
		
		String dataClass = getProperty(DATA_TYPE_KEY);
		if (!"byte[]".equals(dataClass))
			throw new StorageException("Representation's (" + getUuid() + ") type is not byte[]. It's '" + dataClass + "'");
		
		InputStream input = storage.getRepresentationInputStream(getUuid());
		if (null == input)
			throw new StorageException("Can't read data from representation " + getUuid() + ". Input stream is NULL");
		
		byte[] ba;
		try {
			ba = IOUtils.toByteArray(input);
		} catch (IOException e) {
			throw new StorageException("Can't read data from representation " + getUuid(), e);
		} 
		
		return ba;
	}
	
	public InputStream getData(Storage storage) throws StorageException {
		
		InputStream in = storage.getRepresentationInputStream(getUuid());
		return in;
	}
	
	@Override
	public String toString() {
		return "Representation [uuid=" + uuid + ", properties=" + properties + "]";
	}
}
