package org.neuro4j.logic.swf.enums;

public enum NetworkVisibility {
      Public, Private;
      
      private static String[] opNames = new String[]{Public.name(), Private.name()};
      
      public static NetworkVisibility getDefault()
      {
    	  return Public;
      }
      
      public static String[] types(){
      	return opNames;
      }
      
}
