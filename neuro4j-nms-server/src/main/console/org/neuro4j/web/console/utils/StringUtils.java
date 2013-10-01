package org.neuro4j.web.console.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StringUtils {
    
  
  public static byte[] getBytes(String hexString)
  {
	  byte[] bts = new byte[hexString.length() / 2];
	  for (int i = 0; i < bts.length; i++) {
		  bts[i] = (byte) Integer.parseInt(hexString.substring(2*i, 2*i+2), 16);
	  }
	  return bts;
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
  
    public static String getNumberShortStr(long number)
    {       
        String numStr = "" + number;
        if (number < 1000)
        {
            return numStr;
        } else if (number > 999 && number < 1000000) {
            numStr = numStr.substring(0, numStr.length() - 3) + "K";
        } else if (number > 99999 && number < 1000000000) {
            numStr = numStr.substring(0, numStr.length() - 6) + "M";
        }

        return numStr;
    }

    public static boolean isStringEquals(String a, String b)
	{
		if (null == a) a = "";
		if (null == b) b = "";
		
		if (a.equals(b))
			return true;
		
		return false;
	}

    /**
     * inSet is not parametrized because parameter can be String or Denomination
     */
	public static String set2string(Set inSet, String separator)
	{
		if (null == inSet || inSet.size() == 0)
			return "";
		if (inSet.size() == 1)
			return inSet.iterator().next().toString();
		
		String s = "";
		Iterator iter = inSet.iterator();
		boolean firstIter = true;
		while (iter.hasNext())
		{
			if (firstIter)
				firstIter = false;
			else 
				s += separator;
			s += iter.next();
		}
		
		return s;
	}

  
  /**
   * Roman Empire (27BC–476) -> Roman Empire 
   * @param s
   * @return
   */
  public static String excludeBrasses(String s)
  {
      if (null == s)
          return null;
      
      int idxStart = s.indexOf('('); 
      int idxEnd = s.indexOf(')'); 
      if (idxStart > -1 && idxEnd > -1 && idxStart < idxEnd)
      {
          String s1 = s.substring(0, idxStart) + s.substring(idxEnd + 1);
          return s1.trim();
      }
      
      return s;
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
  
  /**
   * remove HTML tags
   * <code> <p>test</p> -> test </code>
   * 
   * 
   * @param inStr
   * @return
   */
  public static String html2txt(String inStr)
  {
      if (null == inStr)
          return inStr;
      
      String outStr = inStr;
      
      int idx1 = 0;
      int idx2 = 0;
      do {
          idx1 = outStr.indexOf("<");
          idx2 = outStr.indexOf(">", idx1);
          if (idx1 >= 0 && idx2 > 0)
          {
              outStr = outStr.substring(0, idx1) + outStr.substring(idx2 + 1);
          }
      } while (idx1 >= 0 && idx2 > 0);
      
      return outStr.replaceAll("&amp;nbsp;", " ").replaceAll("&nbsp;", " ");
  }
  
  /**
   * Sort headers in order [id, name, connected, other properties]
   * 
   * @param headersSet
   * @return
   */
  public static List<String> orderSQLHeaders(Set<String> headersSet)
  {
	  List<String> headers = new ArrayList<String>();
	  headers.addAll(headersSet);
	  
	  Collections.sort(headers, new Comparator<String>() {
		    public int compare(String o1, String o2) {

		    	if ("id".equalsIgnoreCase(o1) && !"id".equalsIgnoreCase(o2))
		        	return -1;
		        else if ("id".equalsIgnoreCase(o2) && !"id".equalsIgnoreCase(o1)) 
		        	return 1;
		        else if ("id".equalsIgnoreCase(o1) && "id".equalsIgnoreCase(o2)) 
		        	return 0;
		        
		    	if ("name".equalsIgnoreCase(o1) && !"name".equalsIgnoreCase(o2))
		        	return -1;
		        else if ("name".equalsIgnoreCase(o2) && !"name".equalsIgnoreCase(o1)) 
		        	return 1;
		        else if ("name".equalsIgnoreCase(o1) && "name".equalsIgnoreCase(o2)) 
		        	return 0;

		    	if ("connected".equalsIgnoreCase(o1) && !"connected".equalsIgnoreCase(o2))
		        	return -1;
		        else if ("connected".equalsIgnoreCase(o2) && !"connected".equalsIgnoreCase(o1)) 
		        	return 1;
		        else if ("connected".equalsIgnoreCase(o1) && "connected".equalsIgnoreCase(o2)) 
		        	return 0;
		    	
			    else 
		        {
				    	return o1.compareTo(o2);
		        }
		        
		        
		    }
		});
	  
	  return headers;
  }

}