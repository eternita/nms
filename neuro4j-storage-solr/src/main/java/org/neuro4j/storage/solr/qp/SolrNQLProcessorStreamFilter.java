package org.neuro4j.storage.solr.qp;

import java.util.Set;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Path;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.qp.ERType;
import org.neuro4j.storage.qp.NQLProcessorStream;
import org.neuro4j.storage.qp.QueryProcessorFilter;
import org.neuro4j.storage.solr.SolrIndexMgr;

public class SolrNQLProcessorStreamFilter extends SolrNQLProcessorStreamBase {
	
	private Storage baseStorage = null;
	private QueryProcessorFilter filter = null;

	public SolrNQLProcessorStreamFilter(QueryProcessorFilter filter, Storage baseStorage,
			SolrIndexMgr siMgr, Set<Path> currentMatchedPaths,
			NQLProcessorStream inputStream) 
	{
		super(siMgr, currentMatchedPaths, inputStream, false);
		this.baseStorage = baseStorage;
		this.filter = filter;
		
		// do not update match paths - put through
		this.currentMatchedPaths = inputStream.getCurrentMatchedPaths();
	}

	
	public boolean hasNext() {
		
		if (null == inputStream)
		{
			// very first decorator - can't be filter
			return false;
		}
		
		return inputStream.hasNext();
	}
	
//	public ERType getERQueryType()
//	{
//		return inputStream.getERQueryType();
//	}	
	
	public String next() {
		
		if (null == inputStream)
		{
			// very first decorator - can't be filter
			return null;
		}
		String id = inputStream.next();

		// resolve by id
        Connected er = siMgr.getById(id); 
        
        filter.filter(er, baseStorage);

		return er.getUuid();
	}

	
	public int getDepthLevel()
	{
		if (null == inputStream) // filter can't be top level
			return 0; // top level
		else
			return inputStream.getDepthLevel(); // filter does not increment depth level (used in matched path)
	}

	
}
