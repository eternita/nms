package org.neuro4j.storage.inmemory.qp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.core.Path;
import org.neuro4j.storage.qp.ERType;
import org.neuro4j.storage.qp.Filter;
import org.neuro4j.storage.qp.NQLProcessorStream;

public class InMemoryNQLProcessorStreamQuery extends InMemoryNQLProcessorStreamBase {

	private Network pipeNet;
	
	private Set<Connected> currentERNetwork;
	
	private int inChunkCounter = 0;

//	private ERType queryType;

	private Iterator<Connected> iter;
	
	private Map<String, Set<String>> useOnlyAttrMap = new HashMap<String, Set<String>>();
	
	private Map<String, Set<String>> ignoreAttrMap = new HashMap<String, Set<String>>();
	
	private Connected next = null;

	/**
	 * output network size limit
	 * allows to stop processing if the limit is reached
	 * 
	 */
	private long outputNetworkLimit = -1;
	
	/**
	 * for serving filter clause e.g. filter(r[name='coin-belong-to-coin-definition'] 5, r[name='user-contributed-to-coin-definition'] 3)
	 * 
	 * <filter, match-count>
	 */
	protected Map<Filter, Integer> filterMap = new HashMap<Filter, Integer>();

	
	public InMemoryNQLProcessorStreamQuery(
			Set<Connected> currentERNetwork,
			Network pipeNet,
			Set<Filter> filterSet,
			ERType queryType, 
			Set<Path> currentMatchedPaths,
			NQLProcessorStream inputStream, 
			boolean optional, 
			long outputNetworkLimit) 
	{
		super(currentMatchedPaths, inputStream, optional);
		this.outputNetworkLimit = outputNetworkLimit;
		this.currentERNetwork = currentERNetwork;
		this.pipeNet = pipeNet;
		
		for (Filter filter : filterSet)
			filterMap.put(filter, 0);		
		
		if (null == inputStream)
		{
			// very first decorator
//			this.queryType = queryType;
		} else {
			// not first decorator
//			ERType previousQueryType = inputStream.getERQueryType();
//			switch (previousQueryType)
//			{
//			case entity:
//				this.queryType = ERType.relation;
//				break;
//			case relation:
//				this.queryType = ERType.entity;
//				break;
//			}
		}
	}
	
	public InMemoryNQLProcessorStreamQuery(
			Set<Connected> currentERNetwork,
			Network pipeNet,
			Set<Filter> filterSet,
			NQLProcessorStream inputStream, 
			boolean optional,
			Map<String, Set<String>> useOnlyAttrMap,
			Map<String, Set<String>> ignoreAttrMap,
			long outputNetworkLimit
			) 
	{ 
		this(currentERNetwork, pipeNet, filterSet, null, new HashSet<Path>(), inputStream, optional, outputNetworkLimit);
		this.useOnlyAttrMap = useOnlyAttrMap;
		this.ignoreAttrMap = ignoreAttrMap;
	}
	
//	public ERType getERQueryType()
//	{
//		return queryType;
//	}
	
	/**
	 * 
	 */
	public boolean hasNext() 
	{
		if (null != next) // next() was not called. probably hasNext() called > 1 time 
			return true;
		
		if (-1 < outputNetworkLimit // not ALL 
				&& outputNetworkLimit + 1 < getCurrentOutputNetSize()) // more then limit
			return false;
		
		boolean hasNext = false;

		if (null == inputStream)
		{
			// very first decorator - no parent ids
			if (null == iter)
		    	iter = currentERNetwork.iterator();

			hasNext = iter.hasNext();
			if (hasNext) 
				next = iter.next(); 
			return hasNext;
		}
		
		// not first decorator
		if (null == iter)
		{
			// parent input is empty - no query required
			if (!inputStream.hasNext())
			{
				return false;
			}

			updateIterator();

		} else {
			// iter exist - check if end is reached
			if (!iter.hasNext() && inputStream.hasNext())
			{
				// end is reached - check if parent decorator has more ids
				updateIterator();
			} // if (!iter.hasNext())
		} // if - else (null == iter) 
		
		// if current iterator is empty and upstream has more previous ids - do recursion
		if (!iter.hasNext() && inputStream.hasNext())
			return hasNext();

		// validate er stream with query filters. (skip er's which over filter amount)
		hasNext = checkQueryFilters();
		
		return hasNext;
	}
	
	
	/**
	 * validate er stream with query filters. (skip er's which over filter amount)
	 *  
	 * @return
	 */
	private boolean checkQueryFilters()
	{
		boolean hasNext = iter.hasNext();
		
		if (!hasNext) // nothing found in current iterator
		{
			// check if parent stream has more elements
			if (inputStream.hasNext())
			{
				// recreate current iterator with new data from parent stream
				updateIterator();
				// recursive check
	        	return checkQueryFilters();
	        	
			} else {
				// end of parent iterator
	    		next = null;         	
				return hasNext; // return false;
			}
		}
		
		boolean goThroughIterator = false;
		do
		{
    		goThroughIterator = false;
			next = iter.next(); 
			
	        Connected er = next;
			for (Filter f : filterMap.keySet())
			{
		        if (f.propertyValue.equals(er.getProperty(f.propertyName)) )
		        {
		        	// er match this filter
		        	int matchCount = filterMap.get(f);
		        	matchCount++;
		        	filterMap.put(f, matchCount);
		        	
		        	if (matchCount > f.filterAmount)
		        	{
		    			// skip current doc 
		            	next = null;
		            	
		            	if (iter.hasNext())
			        		goThroughIterator = true;
			        	else
			            	hasNext = checkQueryFilters(); // do recursive check - may need updateIterator() call

		        		break;
		        	}
		        }
			}
		} while (goThroughIterator);		
        return hasNext;
	}
	
	/**
	 * 
	 */
	private void updateIterator()
	{
		Set<String> previousIds = new LinkedHashSet<String>();
		// create new query with further ids from parent decorator
		while (inputStream.hasNext())
		{
			previousIds.add(inputStream.next());
			inChunkCounter ++;
			if (inChunkCounter >= INPUT_CHUNK_SIZE_LIMIT)
			{
				inChunkCounter = 0;
				break;
			}
		}
					
		// if optional -> copy matched paths from parent 
		if (optional)
			for (Path p : inputStream.getCurrentMatchedPaths())
				currentMatchedPaths.add(p);

		
		iter = getERByPreviousIds(previousIds).iterator();
		
		return;
	}
	
	private boolean isMatch(Connected er, String key, Set<String> values)
	{
		String erValue = er.getProperty(key);
		if (null != erValue)
    		for (String value : values)
    			if (value.equals(erValue))
    				return true;
		
		return false;
	}

	private boolean isMatch(Connected er, Map<String, Set<String>> attrMap, boolean defaultt)
	{		
		boolean match = defaultt;	
    	for (String key : attrMap.keySet())
    	{
    		String qkey = key;
			match = false;
			qkey = key;
			if (isMatch(er, qkey, attrMap.get(key)))
				return true;

/*    		if (key.startsWith("r.") 
    				&& queryType == ERType.relation)
    		{
    			match = false;
    			qkey = key.substring("r.".length());
    			if (isMatch(er, qkey, attrMap.get(key)))
    				return true;
    			
    			
    		} else if (key.startsWith("e.") 
    				&& queryType == ERType.entity) 
    		{
    			match = false;
    			qkey = key.substring("e.".length());
    			if (isMatch(er, qkey, attrMap.get(key)))
    				return true;
    			
    			
    		} else if (!key.startsWith("e.") && !key.startsWith("r.")) {
    			match = false;
    			qkey = key;
    			if (isMatch(er, qkey, attrMap.get(key)))
    				return true;
    		}
*/    		
    	} // for (String key : useOnlyAttrMap.keySet())

    	return match;
	}

	
	private Set<Connected> getERByPreviousIds(Set<String> previousIds)
	{
		Set<Connected> matchedERs = new HashSet<Connected>();
		
		for (Connected er : currentERNetwork)
		{
			for (String previousId : previousIds)
			{
				if (er.isConnectedTo(previousId))
				{
					
					// check for useOnly
					if (!useOnlyAttrMap.isEmpty())
					{
						if (isMatch(er, this.useOnlyAttrMap, true))
						{
							matchedERs.add(er);
							break;
						}
					} else if (!ignoreAttrMap.isEmpty()) {
						// check for ignore match
						if (!isMatch(er, this.ignoreAttrMap, false))
						{
							matchedERs.add(er);
							break;
						}
					} else {
						// no use-only or ignore filters -> just add it 
						matchedERs.add(er);
						break;
					}
				} // if (er.isConnectedTo(previousId))
			}
		} // for (ERBase er : currentERNetwork.getERBases())
		
		return matchedERs;
	}	
	
	public String next() {
		
		if (null == next)
			return null;
		
/*		Don't check it - in next() call outputNetworkLimit is much more then in hasNext() 
		because it updated in updateIterator() 
		
		if (outputNetworkLimit < getCurrentOutputNetSize())
			return null;
*/		
		
		Connected er = next;
		
		next = null;
		
		if (null == inputStream)
		{
			// very first decorator
			// create Paths
			Path p = new Path(er.getUuid());
			currentMatchedPaths.add(p);
		} else {
			// not first decorator		
			updateMatchedPaths(er);
		}
		
		return er.getUuid();
	}	
	
	private void updateMatchedPaths(Connected newERBase)
	{
		Set<Path> newMatchedPaths = new HashSet<Path>();
		
		for (Path p : inputStream.getCurrentMatchedPaths())
		{
			
			// if current level is optional -> move through current paths
			if (optional)
				newMatchedPaths.add(p);
			
			String lastId = p.getLast();
			Connected lastER = pipeNet.getById(lastId); // last er in the path can be virtual 
			if (newERBase.isConnectedTo(lastId)
					|| (lastER.isVirtual() && lastER.isConnectedTo(newERBase.getUuid())))  
			{
				if (!ALLOW_REENTRANCE)
				{   // check for re-entrance
					if (p.contains(newERBase.getUuid()))
						continue;
				}

				p = p.clone();
				p.add(newERBase.getUuid());
				newMatchedPaths.add(p);
			}
		}
		
		for (Path p : newMatchedPaths)
			currentMatchedPaths.add(p);
			
		return;
	}

	
	public int getDepthLevel()
	{
		if (null == inputStream)
			return 0; // top level
		else
			return inputStream.getDepthLevel() + 1;
	}
	
}
