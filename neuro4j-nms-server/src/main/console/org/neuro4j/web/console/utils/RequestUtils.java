package org.neuro4j.web.console.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class RequestUtils {

    private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);
    
	/**
	 * request parameters save as request attributes with the same names
	 * 
	 * @param request
	 * @param params
	 */
	public static void params2attributes(HttpServletRequest request, String...params)
    {
		for (String param : params)
		{
    		request.setAttribute(param, request.getParameter(param));
		}
		return;
    }
	/**
	 * remove attributes from the request
	 * 
	 * @param request
	 * @param attributes
	 */
    public static void removeAttributes(HttpServletRequest request, String...attributes)
    {
        for (String atr : attributes)
        {
            request.removeAttribute(atr);
        }
        return;
    }

    public static String getRequestParametersAsURL(HttpServletRequest request)
	{
		return getRequestParametersAsURL(request, "");
	}

	public static String getRequestParametersAsURL(HttpServletRequest request, String... excludedParamName)
	{
		StringBuffer sb = new StringBuffer();
		Map paramsMap = request.getParameterMap();
		Iterator keyIter = paramsMap.keySet().iterator(); 
		while (keyIter.hasNext())
		{
			String param = keyIter.next().toString();
			if (!isExcluded(param, excludedParamName))
				sb.append(param).append("=").append(request.getParameter(param)).append("&");
		}
		
		return sb.toString();
	}
	
	private static boolean isExcluded(String param, String... excludedParamName)
	{
		for (String excludedParam : excludedParamName)
		{
			if (param.equals(excludedParam))
				return true;
		}
		return false;
	}
	
    public static byte[] getFileFromRequest(HttpServletRequest request, String attributeName) 
    {

    	try
    	{
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartRequest.getFile(attributeName);
            if (null == multipartFile)
                return null;
            
            byte[] image = null;
            try {
                image = multipartFile.getBytes();
                if (image.length == 0)
                    image = null;
            } catch (IOException e1) {
                logger.error("", e1);
                e1.printStackTrace();
            }
            return image;
    	} catch (Exception ex) {
    		// is not multipart request
    		return null;
    	}
    }
    
    

	/**
	 * get parameter names by prefix (e.g. extract CIids from selected checkboxes)
	 * 
	 * 
	 * @param request
	 * @return
	 */
	public static String[] getParameterNamesByPrefix(HttpServletRequest request, String prefix, boolean removePrefix)
	{
		String[] names = new String[0];
		Set<String> namesSet = new HashSet<String>();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements())
		{
			String paramKey = paramNames.nextElement();
			if (paramKey.startsWith(prefix))
			{
				if (removePrefix)
					namesSet.add(paramKey.substring(prefix.length()));
				else
					namesSet.add(paramKey);
			}
		}
		return namesSet.toArray(names);
	}
	
	public static final String BROWSER_IE = "BROWSER_IE";
	public static final String BROWSER_CHROME = "BROWSER_CHROME";
	public static final String BROWSER_FIREFOX = "BROWSER_FIREFOX";
	public static final String BROWSER_OPERA = "BROWSER_OPERA";
	public static final String BROWSER_SAFARY = "BROWSER_SAFARY";
	public static final String BROWSER_OTHER = "BROWSER_OTHER";
	private static Method getRequestURLMethod = null;

	public static String getBrowserType(HttpServletRequest request)
	{
		String userAgent = request.getHeader("user-agent"); 
		if (null == userAgent)
			return BROWSER_OTHER;
		
		if (-1 < userAgent.indexOf("MSIE"))
			return BROWSER_IE;
		
		if (-1 < userAgent.indexOf("Chrome"))
			return BROWSER_CHROME;
		
		if (-1 < userAgent.indexOf("Opera"))
			return BROWSER_OPERA;
		
		if (-1 < userAgent.indexOf("Firefox"))
			return BROWSER_FIREFOX;
		
		if (-1 < userAgent.indexOf("Safary"))
			return BROWSER_SAFARY;
		
		return BROWSER_OTHER;
	}
	
	public static String getRequestURL(HttpServletRequest request)
	{
        String requestURL = null;
    	requestURL = request.getRequestURL().toString();

//        try 
//        {
//        	// read real request url (which like in browser) using reflection
//        	if (null == getRequestURLMethod)
//        		getRequestURLMethod = Class.forName("org.apache.catalina.connector.RequestFacade").getMethod("getRequestURL");
//
//        	Object internalRequest = PropertyUtils.getProperty(request, "request.request");
//        	if (null != getRequestURLMethod && null != internalRequest)
//        		requestURL = getRequestURLMethod.invoke(internalRequest).toString();
//        	
//        } catch (Exception ex) {
//        	logger.info("Can't get requestURL from " + request, ex);
//        }
//        
//        if (null == requestURL)
//        	requestURL = request.getRequestURL().toString();
        
        return requestURL;
	}
    
	public static String escapeJSString(String in)
	{
		if (null == in)
			return "";
		
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<in.length(); i++)
		{
			char c = in.charAt(i);
			if ('\'' == c
					|| '\"' == c)
			{
				sb.append('\\').append(c);
			} else if ('\n' == c
					|| '\r' == c){
				sb.append(' ');
			} else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
}
