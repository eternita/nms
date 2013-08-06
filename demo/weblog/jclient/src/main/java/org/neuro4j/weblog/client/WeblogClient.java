package org.neuro4j.weblog.client;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.NeuroManager;
import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Representation;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.utils.StringUtils;

/**
 * 
 * Weblog Client
 *
 */
public class WeblogClient {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	{
		try {
			logger.addHandler(new ConsoleHandler());
			logger.addHandler(new FileHandler("weblogs.log"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final DateFormat YYYY_MM_dd_HH_mm_ss_SSS_DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	private String STORAGE_HOME_DIR = null; // "./data/demo-storage"; 
	
	// executed just after request posted to storage
	private String REQUEST_POST_QUERY = null; //"behave(flow='weblog.ProcessRequest-Start' requestId=?)"; 
	
	private NeuroStorage storage; 
	
	public WeblogClient(Properties properties)
	{
		STORAGE_HOME_DIR = properties.getProperty("org.neuro4j.weblogs.storage_home"); 
		
		REQUEST_POST_QUERY = properties.getProperty("org.neuro4j.weblogs.post_flow"); 
		
		storage = NeuroManager.newInstance().getNeuroStorage(STORAGE_HOME_DIR, "storage.properties");
	}

//	public void post(HttpServletRequest request)
//	{
//		post(request, null);
//		return;
//	}
	
	public void post(HttpServletRequest request, Map params, byte[] content)
	{
		Map map = request2map(request);
		if (null != params)
			map.putAll(params);
		
		Entity e = map2entity(params);
		post(e, content);
		return;
	}
	
//	public void post(Map params)
//	{
//		Entity e = map2entity(params);
//		post(e);
//		return;
//	}

//	public void post(Network net)
//	{
//		try {
//			storage.save(net);
//		} catch (StorageException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return;
//	}
	
	private void post(final Entity entity, final byte[] content)
	{
			final Network net = new Network();
			net.add(entity);
			


			
			final String requestId = entity.getUuid(); 
			
			Runnable r = new Runnable() {
				
				public void run() {
					try {
						if (null != content)
						{
							Representation representation = new Representation();
							try {
								representation.setData(storage, content);
								entity.addRepresentation(representation);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						storage.save(net);
						// TODO: !! wait until data committed to solr
						// Solr doesn't commit immideatly even for : solrServer.commit(true, true);
						// check if it solved in further versions of Solr
						Thread.currentThread().sleep(500); // wait until data committed to solr
						storage.query(REQUEST_POST_QUERY, new String[]{requestId});
					} catch (Exception e) {
						logger.severe("Post entity error: " + e.getMessage());
					}
				}
			};
			
			Thread t = new Thread(r);
			t.start();
			
			
		return;
	}
	
//	public Network query(String query) throws NQLException, StorageException
//	{
//		return neuroStorage.query(query);
//	}

	private static Entity map2entity(Map<String, String> map)
	{
		Entity e = new Entity();
		if (null == map)
			return e;
		
		
		for (String key : map.keySet())
		{
			if ("id".equalsIgnoreCase(key) || "uuid".equalsIgnoreCase(key))
				e.setProperty(key + "_", map.get(key));
			else 
				e.setProperty(key, map.get(key));
				
		}
		
		return e;
	}

	private static Map request2map(HttpServletRequest request)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("request-url", StringUtils.getShortStr(getRequestURL(request), 2000));

        if ("GET".equals(request.getMethod()))
        {
        	Enumeration paramNames = request.getParameterNames();
        	while (paramNames.hasMoreElements()){
        		String name = (String) paramNames.nextElement();
        		params.put(name, request.getParameter(name));
        	}
        }
        
        params.put("host", request.getRemoteHost());
        params.put("remote-address", request.getRemoteAddr());
        params.put("user-agent", StringUtils.getShortStr(request.getHeader("user-agent"), 2000));
        params.put("method", request.getMethod());
        params.put("referer", StringUtils.getShortStr(request.getHeader("referer"), 2000));
        params.put("session-id", request.getSession().getId());
        params.put("request-start-time", YYYY_MM_dd_HH_mm_ss_SSS_DF.format(new Date()));

		return params;
	}

	private static String getRequestURL(HttpServletRequest request)
	{
        String requestURL = request.getRequestURL().toString();
   
        if ("GET".equals(request.getMethod()))
        {
        	// add parameters for storing 
        	// POST method parameters are not stored because they can be huge (e.g. file upload)
        	StringBuffer sb = new StringBuffer(requestURL);
        	Enumeration paramNames = request.getParameterNames();
        	if (paramNames.hasMoreElements())
        	{
        		sb.append("?");
        	}
        	while (paramNames.hasMoreElements()){
        		String name = (String) paramNames.nextElement();
        		sb.append(name).append("=").append(request.getParameter(name)).append("&");            		
        	}
        	requestURL = sb.toString();
        }	
        return requestURL;
	}	


}
