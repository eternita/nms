package org.neuro4j.storage.qp;

import org.neuro4j.core.ERBase;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.qp.QueryProcessorFilter;

public class DummyQueryProcessorFilter implements QueryProcessorFilter {

	public void filter(ERBase er, NeuroStorage storage) {
		// Dummy - do nothing
		
		System.out.println("Running DummyQueryProcessorFilter for " + er);

	}

}
