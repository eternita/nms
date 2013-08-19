package org.neuro4j.weblog.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.neuro4j.mgr.uuid.UUIDMgr;


/**
 * 
 * logs http requests
 *
 */
public class WeblogFilter implements Filter {

	WeblogClient client = new WeblogClient(loadProperties("weblog-client.properties"));

	private Map<String, String> configParams = new HashMap<String, String>();
			
	public void init(FilterConfig filterConfig) throws ServletException {
		
		Enumeration names = filterConfig.getInitParameterNames();
		while (names.hasMoreElements())
		{
			String name = (String) names.nextElement();
			String value = filterConfig.getInitParameter(name);
			configParams.put(name, value);
		}
	}
	
	public void destroy() {	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException 
	{
		long start = System.currentTimeMillis();
		
		String requestId = UUIDMgr.getInstance().createUUIDString();
		// requestId can be used to post other events during request processing 
		request.setAttribute("requestId", requestId);
		
		chain.doFilter(request, response);
		
		// post dynamic request only (no images, css, etc)
		if (null != response.getContentType() && -1 < response.getContentType().indexOf("text/html"))
		{
			long end = System.currentTimeMillis();
			long execTime = end - start;
			
			Map<String, String> params = new HashMap<String, String>();
			params.putAll(configParams); // put all config params
			
			params.put("name", "web request");
			params.put("exec-time", "" + execTime);
			params.put("session-id", ((HttpServletRequest) request).getSession().getId());
			params.put("request-url", getRequestURL((HttpServletRequest) request));
			

			client.post((HttpServletRequest) request, params, null);
		}
			
			
		return;
	}
	
	private String getRequestURL(HttpServletRequest request)
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
	/**
	 * Load properties from file
	 * 
	 * @param file
	 * @return
	 */
	private static Properties loadProperties(String fName)
	{
		Properties config = new Properties();

		InputStream is = null;
		try 
		{
			is = WeblogFilter.class.getClassLoader().getResourceAsStream(fName);
			config.load(is);
		} catch (Exception e) {
			throw new RuntimeException("can't read properties " + fName, e);
		} finally {
			if (null != is)
			{
				try {
					is.close();
				} catch (IOException e) { }
			}
		}
		return config;
	}	
	

}
