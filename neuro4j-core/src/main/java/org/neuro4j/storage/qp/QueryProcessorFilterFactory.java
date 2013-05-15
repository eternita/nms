package org.neuro4j.storage.qp;

import java.util.logging.Logger;

import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.qp.QueryProcessorFilter;
import org.neuro4j.storage.qp.QueryProcessorFilterFactory;



public class QueryProcessorFilterFactory {

	private final static Logger logger = Logger.getLogger(QueryProcessorFilterFactory.class.getName());

	public static QueryProcessorFilter getQueryProcessorFilter(String name) throws StorageException
	{
		try {
			Class clazz = Class.forName(name);
			Object fObj = clazz.newInstance();
			if (fObj instanceof QueryProcessorFilter)
				return (QueryProcessorFilter) fObj;
				
		} catch (ClassNotFoundException e) {
			logger.severe("Can't create QueryProcessorFilter " + name + " " + e);
		} catch (InstantiationException e) {
			logger.severe("Can't create QueryProcessorFilter " + name + " " + e);
		} catch (IllegalAccessException e) {
			logger.severe("Can't create QueryProcessorFilter " + name + " " + e);
		}
		throw new StorageException("QueryProcessorFilter " + name + " not found");
	}

}
