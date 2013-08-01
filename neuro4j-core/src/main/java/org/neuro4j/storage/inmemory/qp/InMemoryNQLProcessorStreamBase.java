package org.neuro4j.storage.inmemory.qp;

import java.util.HashSet;
import java.util.Set;

import org.neuro4j.core.Path;
import org.neuro4j.storage.qp.NQLProcessorStream;

public abstract class InMemoryNQLProcessorStreamBase implements NQLProcessorStream {

	protected NQLProcessorStream inputStream;
	
	protected Set<Path> currentMatchedPaths;

	protected boolean optional = false; // if current level is optional // e()/r()?/e()

	public InMemoryNQLProcessorStreamBase(
			Set<Path> currentMatchedPaths, 
			NQLProcessorStream inputStream,
			boolean optional) 
	{
		this.inputStream = inputStream;
		this.currentMatchedPaths = currentMatchedPaths;
		this.optional = optional;
	}
	
	public Set<Path> getCurrentMatchedPaths()
	{
		return currentMatchedPaths;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public int getCurrentOutputNetSize()
	{
		if (null == currentMatchedPaths)
			return -1;
		
		Set<String> netids = new HashSet<String>();
		for (Path p : currentMatchedPaths)
			netids.addAll(p.getItems());
		
		return netids.size();
	}
	
}
