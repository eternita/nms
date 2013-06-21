package org.neuro4j.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	private static Logger logger = Logger.getLogger(StringUtils.class.getName());

	public static boolean match(String str, String regexp) {
		if (null == str)
			return false;

		boolean match = false;
		try
		{
			Pattern p = Pattern.compile(regexp);
			Matcher m = p.matcher(str);
			match = m.matches();
		} catch (Exception ex) {
			logger.warning(ex.getMessage());
		}
		
		return match;
	}
	
	public static String getShortStr(String str, int maxLenth)
	{		
		if (maxLenth < 5) 
			maxLenth = 5;
		
		if (null != str)
		{
			if (str.length() > maxLenth)
			{
				str = str.substring(0, maxLenth - 5) + " ...";
			}
		}
		return str;
	}	
	
	  /**
	   * Override java.util.Set's toString 
	   * 
	   * [Ruble, Rouble] -> Ruble Rouble 
	   * 
	   * @param inSet
	   * @return
	   */
	  public static String set2str(Set<String> inSet)
	  {
	      return set2str(inSet, " ");
	  }
	  
	  public static String set2str(Set<String> inSet, String separator)
	  {
	      if (null == inSet)
	          return null;
	          
	      StringBuffer sb = new StringBuffer();
	      for (String s : inSet)
	          sb.append(s).append(separator);
	          
	      return sb.toString();
	  }  
	  
//	  public static Set<String> str2set(String inStr, String separator)
//	  {
//		  Set<String> set = new HashSet<String>();
//	      if (null == inStr)
//	          return set;
//	          
//	      
//	      StringBuffer sb = new StringBuffer();
//	      for (String s : inSet)
//	          sb.append(s).append(separator);
//	          
//	      return sb.toString();
//	  }  
}
