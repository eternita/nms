package org.neuro4j.core.log;

import org.slf4j.LoggerFactory;

public class Logger {

	  public static void error(Object category, String msg, Object[] params)
	  {
	    if (params.length == 0)
	    {
	      getLogger(category).error(msg);
	    }
	    else
	    {
	      getLogger(category).error(msg, params);
	    }
	  }
	  public static void error(Object category, String msg)
	  {
	      getLogger(category).error(msg);
	  }
	  public static void error(Object category, String msg, Object param)
	  {
	      getLogger(category).error(msg, param);
	  }
	  public static void error(Object category, String msg, Object param1, Object param2)
	  {
	      getLogger(category).error(msg, param1, param2);
	  }
	  public static void error(Object category, String msg, Throwable ex)
	  {
	    getLogger(category).error(msg, ex);
	  }
	  
	  public static void info(Object category, String msg, Throwable ex)
	  {
	    getLogger(category).info(msg, ex);
	  }

	  public static void info(Object category, String msg, Object[] params)
	  {
	    if (params.length == 0)
	    {
	      getLogger(category).info(msg);
	    }
	    else
	    {
	      getLogger(category).info(msg, params);
	    }
	  }
	  public static void info(Object category, String msg, Object param1)
	  {
	      getLogger(category).info(msg, param1);
	  }
	  
	  public static void info(Object category, String msg, Object param1, Object param2)
	  {
	      getLogger(category).info(msg, param1, param2);
	  }

	  public static void debug(Object category, String msg, Object[] params)
	  {
	    if (params.length == 0)
	    {
	      getLogger(category).debug(msg);
	    }
	    else
	    {
	      getLogger(category).debug(msg, params);
	    }
	  }
	  
	  public static void debug(Object category, String msg, Object param1)
	  {
	      getLogger(category).debug(msg, param1);
	  }
	  
	  public static void debug(Object category, String msg, Object param1, Object param2)
	  {
	      getLogger(category).debug(msg, param1, param2);
	  }

	  public static void debug(Object category, String msg, Throwable ex)
	  {
	    getLogger(category).debug(msg, ex);
	  }
	  public static void warn(Object category, String msg, Object[] params)
	  {
	    if (params.length == 0)
	    {
	      getLogger(category).warn(msg);
	    }
	    else
	    {
	      getLogger(category).warn(msg, params);
	    }
	  }

	  public static void warn(Object category, String msg, Throwable ex)
	  {
	    getLogger(category).warn(msg, ex);
	  }
	  
	  private static org.slf4j.Logger getLogger(Object category)
	  {
	    if ((category instanceof Class))
	    {
	      return LoggerFactory.getLogger((Class)category);
	    }
	    if ((category instanceof String))
	    {
	      return LoggerFactory.getLogger((String)category);
	    }
	    if (category != null)
	    {
	      return LoggerFactory.getLogger(category.getClass());
	    }
	    
	    return LoggerFactory.getLogger("undefined");
	  }

}
