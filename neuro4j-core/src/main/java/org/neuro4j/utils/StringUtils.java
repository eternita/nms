package org.neuro4j.utils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean match(String str, String regexp) {
		if (null == str)
			return false;

		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(str);
		return m.matches();
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
	  
}
