package org.neuro4j.logic;

import java.util.logging.Logger;



public class LogicProcessorFactory {

	private final static Logger logger = Logger.getLogger(LogicProcessorFactory.class.getName());

	public static LogicProcessor getLogicProcessor(String name) throws LogicProcessorNotFoundException
	{
		try {
			Class clazz = Class.forName(name);
			Object fObj = clazz.newInstance();
			if (fObj instanceof LogicProcessor)
				return (LogicProcessor) fObj;
				
		} catch (ClassNotFoundException e) {
			logger.severe("Can't create LogicProcessor " + name + " " + e);
		} catch (InstantiationException e) {
			logger.severe("Can't create LogicProcessor " + name + " " + e);
		} catch (IllegalAccessException e) {
			logger.severe("Can't create LogicProcessor " + name + " " + e);
		}
		throw new LogicProcessorNotFoundException("LogicProcessor " + name + " not found");
	}

}
