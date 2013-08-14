package org.neuro4j.storage.inmemory.qp;

import java.util.Set;

import org.neuro4j.core.Path;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.inmemory.qp.InMemoryNQLProcessorStreamBase;
import org.neuro4j.storage.qp.ERType;
import org.neuro4j.storage.qp.NQLProcessorStream;
import org.neuro4j.storage.qp.QueryProcessorFilter;
public class InMemoryNQLProcessorStreamFilter extends InMemoryNQLProcessorStreamBase {
	
	private Storage baseStorage = null;
	private QueryProcessorFilter filter = null;

	public InMemoryNQLProcessorStreamFilter(QueryProcessorFilter filter, Storage baseStorage,
//			SolrIndexMgr2 siMgr, 
			Set<Path> currentMatchedPaths,
			NQLProcessorStream inputStream) 
	{
		super(currentMatchedPaths, inputStream, false);
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
	
	public ERType getERQueryType()
	{
		return inputStream.getERQueryType();
	}	
	
	public String next() {
		
		if (null == inputStream)
		{
			// very first decorator - can't be filter
			return null;
		}
		String id = inputStream.next();

		// resolve by id
//        ERBase er = siMgr.getById(id); 
        
//        filter.filter(er, baseStorage);

//		return er.getUuid();
		return id;
	}

	
	public int getDepthLevel()
	{
		if (null == inputStream) // filter can't be top level
			return 0; // top level
		else
			return inputStream.getDepthLevel(); // filter does not increment depth level (used in matched path)
	}

	
}
