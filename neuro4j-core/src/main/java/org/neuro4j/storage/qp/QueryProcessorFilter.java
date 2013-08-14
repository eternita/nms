package org.neuro4j.storage.qp;

import org.neuro4j.core.ERBase;
import org.neuro4j.storage.Storage;

// Query Processor Filter
public interface QueryProcessorFilter {

	public void filter(ERBase er, Storage storage);
	
}
