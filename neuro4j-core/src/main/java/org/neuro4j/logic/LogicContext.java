package org.neuro4j.logic;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


import org.apache.commons.beanutils.PropertyUtils;
import org.neuro4j.core.log.Logger;
import org.neuro4j.logic.swf.SWFConstants;

public class LogicContext {
	
	
	private HashMap<String, Object> parameters = new HashMap<String, Object>();
	private HashMap<String, Iterator> loopIterator = null;
	private Stack<String> packages = new Stack<String>();
	

	
	public LogicContext() {
		super();
		loopIterator = new HashMap<String, Iterator>();
	}


	public Map getRuntimeInfo()
	{
		return null; // flows stack - for debug, ...
	}


	public void put(String key, Object value) {
		parameters.put(key, value);
	}
	
	public Object get(String key)
	{
		if (key == null)
		{
			return null;
		}
		key = key.trim();
		
		if(key.startsWith(SWFConstants.QUOTES_SYMBOL) && key.endsWith(SWFConstants.QUOTES_SYMBOL))
		{
			return key.substring(1, key.length()-1);
		}
		if (key.contains("."))
		{
			int pointIndex = key.indexOf(".");
			String firstObj = key.substring(0 , pointIndex);
			Object obj = parameters.get(firstObj);
			if (obj != null)
			{
				String utilKey =  key.substring(pointIndex + 1);
				try {
					obj = PropertyUtils.getProperty(obj, utilKey);
					return obj;
				} catch (IllegalAccessException e) {
					Logger.error(this, e.getMessage(), e);
				} catch (InvocationTargetException e) {
					Logger.error(this, e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					Logger.error(this, e.getMessage(), e);
				}
			} else {
				return null;
			}
		}

		return parameters.get(key);
	}

	public Set<String> keySet()
	{
		return parameters.keySet();
	}

	public Object remove(String key)
	{
		return parameters.remove(key);
	}

	  public void putLoopIterator(String aKey, Iterator aValue)
	  {
	    this.loopIterator.put(aKey, aValue);
	  }
	
	  public Iterator getLoopIterator(String aKey)
	  {
		  Iterator result = this.loopIterator.get(aKey);
	
	    return result;
	  }
	
	  public void removeLoopIterator(String aKey)
	  {
	    this.loopIterator.remove(aKey);
	  }
	  
	  public void setNextRelation(String relationUUId)
	  {
		  put(SWFConstants.AC_NEXT_NODE_UUID, relationUUId);
	  }
	  
	  public void pushPackage(String flowPackage)
	  {
		packages.push(flowPackage);
	  }
	  
	  public String popPackage()
	  {
		  return packages.pop();
	  }

	  public String getCurrentPackage()
	  {
		  return packages.peek();
	  }
	  
	@Override
	public String toString() {
		// sort asc
		List<String> ks = new ArrayList<String>(parameters.keySet());
		Collections.sort(ks);
		Iterator<String> localIterator = ks.iterator();
		if (!(localIterator.hasNext())) {
			return "{}";
		}
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append('{');
		while (true) {
			Object localObject1 = localIterator.next();
			Object localObject2 = parameters.get(localObject1);
			localStringBuilder.append((localObject1 == this) ? "(this Map)" : localObject1);
			localStringBuilder.append('=');
			localStringBuilder.append((localObject2 == this) ? "(this Map)" : localObject2);
			if (!(localIterator.hasNext())) {
				localStringBuilder.append('}');
				break;
			}
			localStringBuilder.append(", ");
		}
		return localStringBuilder.toString();
	}

}
