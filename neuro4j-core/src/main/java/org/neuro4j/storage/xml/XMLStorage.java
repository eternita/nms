package org.neuro4j.storage.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.neuro4j.core.Network;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.inmemory.InMemoryStorage;
import org.neuro4j.storage.inmemory.qp.NQLProcessorInMemory2;
import org.neuro4j.storage.qp.NQLParser;
import org.neuro4j.storage.qp.NQLProcessor;
import org.neuro4j.storage.qp.ParseException;
import org.neuro4j.utils.KVUtils;
import org.neuro4j.xml.ConvertationException;
import org.neuro4j.xml.NetworkConverter;

public class XMLStorage extends InMemoryStorage {

	private String filePath;

	@Override
	public void init(Properties properties) throws StorageException {

		super.init(properties);		
		
		filePath = KVUtils.getStringProperty(properties, XMLStorageConfig.XML_FILE_PATH);

		filePath = checkForRelativeFilePath(filePath);
		
		this.instance = loadNetworkFromFile(filePath);
		if (null == instance)
			throw new StorageException("Can't load network from file " + filePath);
		
		return;
	}
		
    @Override
	public boolean save(Network network) throws StorageException {

		super.save(network);
		
		try {
			saveNetworkToFile(filePath, this.instance);
		} catch (FileNotFoundException e) {
			throw new StorageException("Can't save network to file " + filePath, e);
		}
		
		return true;
	}

	@Override
	public Network query(String q) throws NQLException {
		long start = System.currentTimeMillis();
    	NQLProcessor nqlProcessor = new NQLProcessorInMemory2(instance, this); 
		
		NQLParser eqp = new NQLParser(q, nqlProcessor);

		Network outNet = null;
		
		try {
			outNet = eqp.parse();
			
			outNet.cleanup(); // here is some ids as result of computation
			
			
//			long end = System.currentTimeMillis();
//	    	logger.finest("QTime " + (end - start) + " ms. q = " + URLDecoder.decode(q) );
//	    	if ((end - start) > 3000)
//	    		logger.warning("Slow Query, QTime " + (end - start) + " ms. q = " + URLDecoder.decode(q) );
	    	
		} catch (ParseException e) {
			throw new NQLException("Wrong NQL: " + q + "; \n " + e.getMessage(), e);
		} catch (StorageException e) {
			throw new NQLException("Error during execution NQL: " + q + "; \n " + e.getMessage(), e);
		}

		return outNet;		
	}

	private Network loadNetworkFromFile(String fileName)
	{
		Network net = null;
		InputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			if (null != fis)
				net = NetworkConverter.xml2network(fis); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConvertationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return net;
	}	
	
	private void saveNetworkToFile(String fileName, Network net) throws FileNotFoundException
	{
		
		
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName);
			if (null != fos)
				NetworkConverter.network2xmlstream(net, fos);
		} finally {
			try {
				if (null != fos)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return;
	}	
	

	@Override
	public void ping() throws StorageException {
		File f = new File(this.filePath);
		if(!f.exists())
			throw new StorageException("File " + this.filePath + " does not exist");
	}		
}
