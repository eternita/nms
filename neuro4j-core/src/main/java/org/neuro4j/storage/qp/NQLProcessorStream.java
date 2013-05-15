package org.neuro4j.storage.qp;

import java.util.Set;

import org.neuro4j.core.Path;
import org.neuro4j.storage.qp.ERType;


public interface NQLProcessorStream {

	
	// different behavior for queries like: 
	// select e[e_type='request'] / r[name='session-request'] / e[e_type='session'] / r[name='session-request'] / e[e_type='request']
	// TODO: allow to set it in query
	public static boolean ALLOW_REENTRANCE = false; 
	
	// max amount of IDs read from parent/previous decorator
	public static final int INPUT_CHUNK_SIZE_LIMIT = 100;  
	
	
	
	public boolean hasNext();
	
	/**
	 * 
	 * @return
	 */
	public String next();
	
	/**
	 * return decorators depth level
	 * 
	 * @return
	 */
	public int getDepthLevel();
	
	public ERType getERQueryType();
	
	public Set<Path> getCurrentMatchedPaths();

}
