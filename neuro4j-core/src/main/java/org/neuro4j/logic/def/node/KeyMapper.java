package org.neuro4j.logic.def.node;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.apache.commons.beanutils.ConstructorUtils;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.SWEUtils;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.logic.swf.SWFParametersConstants;

public class KeyMapper extends LogicBlock {

	
	public int execute(LogicContext ctx)
			throws FlowExecutionException {

		Set<String> parameterKeys = lba.getPropertyKeys();
		
		for (String key: parameterKeys)
		{
			if (key.startsWith(SWFParametersConstants.MAPPER_NODE_KEY_PREFIX))
			{
				String mappedValue = lba.getProperty(key);
				if (mappedValue != null)
				{
					String[] splittedValue = SWEUtils.getMappedParameters(mappedValue);
					if (SWFConstants.NULL_VALUE.equalsIgnoreCase(splittedValue[0]))
					{
						ctx.put(splittedValue[1], null);
					// ex. "(java.util.HashSet)" will create new instance of java.util.HashSet. can be just in SOURCE
					} else if(splittedValue[0] != null && splittedValue[0].startsWith(SWFConstants.NEW_CLASS_SYMBOL_START) && splittedValue[0].endsWith(SWFConstants.NEW_CLASS_SYMBOL_END)) {
						
						splittedValue[0] = splittedValue[0].replace(SWFConstants.QUOTES_SYMBOL, "").replace("(", "").replace(")", "");
						
						Object obj = createNewInstance(splittedValue[0]);
						
						ctx.put(splittedValue[1], obj);
						
					} else if(splittedValue[0] != null && splittedValue[0].startsWith(SWFConstants.QUOTES_SYMBOL)) {
						
						splittedValue[0] = splittedValue[0].replace(SWFConstants.QUOTES_SYMBOL, "");
						ctx.put(splittedValue[1], splittedValue[0]);
						
					}else {
						Object obj = ctx.get(splittedValue[0]);
						ctx.put(splittedValue[1], obj);
					}
				}

			}
		}
		return NEXT;
	}
	
	private Object createNewInstance(String clazzName) {
		Class<?> beanClass = null;
		Object beanInstance = null;
		try {
			beanClass = Class.forName(clazzName);
			beanInstance = ConstructorUtils.invokeConstructor(beanClass, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return beanInstance;

	}


}
