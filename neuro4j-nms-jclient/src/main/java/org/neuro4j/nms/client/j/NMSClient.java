package org.neuro4j.nms.client.j;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.neuro4j.core.Network;
import org.neuro4j.storage.StorageBase;
import org.neuro4j.xml.NetworkConverter;

/**
 * 
 * Network Management System API Client
 *
 */
public class NMSClient extends StorageBase {
	
	private static final Log logger = LogFactory.getLog(NMSClient.class);
	private String serverBaseURL; // http://localhost:8080/n4j-nms                        

	
	private boolean queryWireXML = false; // true - wire is XML, false - wire is binary 
	private boolean updateWireXML = true; // true - wire is XML, false - wire is binary 

	
	/**
	 * URLs
	 * query - /api/query
	 * update - /api/update/xml
	 *        - /api/update/bin
	 * 
	 */
	public NMSClient()
	{
		super();
	}

	public void init(Properties properties) {
		this.serverBaseURL = properties.getProperty("org.neuro4j.nms.client.server_url");
		
		try {
			queryWireXML = Boolean.parseBoolean(properties.getProperty("org.neuro4j.nms.client.query_wire_xml").trim());
		} catch (Exception e) {
			logger.error("Can't parse org.neuro4j.nms.client.query_wire_xml. Use default [false]");
		}

//		try {
//			updateWireXML = Boolean.parseBoolean(properties.getProperty("org.neuro4j.nms.client.update_wire_xml").trim());
//		} catch (Exception e) {
//			logger.error("Can't parse org.neuro4j.nms.client.update_wire_xml. Use default [true]");
//		}
		
		return;
	}


	public Network query(String q)
	{
		logger.info("Query " + q);
		HttpClient httpClient = new DefaultHttpClient();

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("cmd","query"));
		params.add(new BasicNameValuePair("q",q));
		if (!queryWireXML)
			params.add(new BasicNameValuePair("output","bin"));
		HttpGet httpGet = new HttpGet(serverBaseURL + "/api/query?" + URLEncodedUtils.format(params, "utf-8"));
		try 
		{
			 HttpResponse response = httpClient.execute(httpGet);
			if (queryWireXML)
			{
				HttpEntity entity = response.getEntity();
				
			    InputStream instream = entity.getContent();
			    Network n = null;
			    try {
				    n = NetworkConverter.xml2network(instream);
			    } finally {
			        instream.close();
			    }
			    return n;
			} else {
				
				// query response in binary mode
				HttpEntity entity = response.getEntity();
			    InputStream instream = entity.getContent();
			    ObjectInputStream ois = new ObjectInputStream(instream);
			    try {
			    	Network n = (Network) ois.readObject();
			    	return n;
			    } finally {
			        ois.close();
			        instream.close();
			    }
			    
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return null;
	}


	public boolean save(Network network) {
		if (updateWireXML)
			return updateXML(network);
		else 
			return updateBin(network);
	}

	private boolean updateBin(Network network) 
	{
		HttpClient httpClient = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost(serverBaseURL + "/api/update/bin");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try 
		{
			ObjectOutputStream output = new ObjectOutputStream(baos);
			output.writeObject(network);
			output.flush();
			output.close();

			byte[] ba = baos.toByteArray();

			InputStreamEntity isre = new InputStreamEntity(new ByteArrayInputStream(ba), ba.length);
			httpPost.setEntity(isre);
			httpPost.setHeader("Content-Type", "application/x-java-serialized-object");

		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
		}

		// Execute the method.
		try {
			 HttpResponse response = httpClient.execute(httpPost);
			
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		httpClient.getConnectionManager().shutdown();
		return false;
	}
	
	private boolean updateXML(Network network) {
		HttpClient httpClient = new DefaultHttpClient();
	      // Execute the method.
		try {
			String netXml = NetworkConverter.network2xml(network);

			HttpPost method = new HttpPost(serverBaseURL + "/api/update/xml");

			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("cmd", "save"));
			formparams.add(new BasicNameValuePair("network", netXml));
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			method.setEntity(entity);

			HttpResponse response = httpClient.execute(method);
			 
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			httpClient.getConnectionManager().shutdown();			
		}		
	    return false;
	}

	
}
