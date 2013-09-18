package org.neuro4j.storage.qp;

import org.neuro4j.core.Connected;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.qp.QueryProcessorFilter;

public class DummyQueryProcessorFilter implements QueryProcessorFilter {

	public void filter(Connected er, Storage storage) {
		// Dummy - do nothing
		
		System.out.println("Running DummyQueryProcessorFilter for " + er);

	}

}
