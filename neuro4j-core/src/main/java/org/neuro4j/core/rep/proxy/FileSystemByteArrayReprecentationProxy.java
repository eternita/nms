package org.neuro4j.core.rep.proxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.neuro4j.core.Representation;
import org.neuro4j.core.rep.RepresentationProxy;
import org.neuro4j.core.rep.RepresentationProxyException;
import org.neuro4j.utils.KVUtils;


public class FileSystemByteArrayReprecentationProxy implements RepresentationProxy {

	/**
	 * default storage directory
	 */
	private static final String DEFAULT_STORAGE_DIR = "representation_storage";
	
	/**
	 * storage directory key (to override DEFAULT_STORAGE_DIR)
	 */
	private static final String STORAGE_DIR_KEY = "representation.proxy.base_dir";
	
	public void init() throws RepresentationProxyException {
	}

	public void put(Object data, Representation representation)
			throws RepresentationProxyException {
		String representationId = representation.getUuid();
		Map<String, String> properties = representation.getPropertyMap();
		
		String storageDirName = getStorageBaseDirectory(properties);
		byte[] ba = (byte[]) data;
		saveFile(storageDirName, representationId, ba);
		
	}

	public Object get(Representation representation)
			throws RepresentationProxyException {
		String representationId = representation.getUuid();
		Map<String, String> properties = representation.getPropertyMap();
		
		String storageDirName = getStorageBaseDirectory(properties);
		return readFile(storageDirName, representationId);
	}
	
	private String getStorageBaseDirectory(Map<String, String> properties)
	{
		String storageDirName = KVUtils.getStringProperty(properties, STORAGE_DIR_KEY, DEFAULT_STORAGE_DIR);
		return storageDirName;
	}

	private static void saveFile(String dirPath, String id, byte[] image) throws RepresentationProxyException
	{
		File dir = new File(dirPath);
		if (!dir.isDirectory())
		{
			dir.mkdirs();
		}
		
		FileOutputStream fos = null;
		try 
		{
			File f = new File(dirPath + "/" + id);
			fos = new FileOutputStream(f);
			fos.write(image);
			fos.flush();
		} catch (Exception e) {
			throw new RepresentationProxyException("Can't save file" + id + " to " + dirPath, e);
		} finally {
			if (null != fos)
			{
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

	
	private static byte[] readFile(String dir, String id) throws RepresentationProxyException
	{
		File file = new File(dir, id);
		if (!file.exists() || !file.isFile())
		{
			throw new RepresentationProxyException("Can't read file" + id + " in " + dir);
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			int bytesRead = 0;
            int bufferSize = 4000;
	         byte[] byteBuffer = new byte[bufferSize];				
	         while ((bytesRead = is.read(byteBuffer)) != -1) {
	             baos.write(byteBuffer, 0, bytesRead);
	         }
		} catch (Exception e) {
			throw new RepresentationProxyException("Can't read file" + id + " in " + dir, e);
//			e.printStackTrace();
		} finally {
			if (null != is)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return baos.toByteArray();
	}	
}
