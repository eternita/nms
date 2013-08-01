package org.neuro4j.storage.solr.qp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.solr.common.SolrDocument;
import org.neuro4j.core.Path;
import org.neuro4j.storage.qp.NQLProcessorStream;
import org.neuro4j.storage.solr.SolrIndexMgr;

public abstract class SolrNQLProcessorStreamBase implements NQLProcessorStream {

	protected SolrIndexMgr siMgr; 
	
	protected NQLProcessorStream inputStream;
	
	protected Iterator<SolrDocument> iter;
	
	protected Set<Path> currentMatchedPaths;

	protected boolean optional = false; // if current level is optional // e()/r()?/e()

	public SolrNQLProcessorStreamBase(
			SolrIndexMgr siMgr, 
			Set<Path> currentMatchedPaths, 
			NQLProcessorStream inputStream,
			boolean optional) 
	{
		this.siMgr = siMgr;
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
