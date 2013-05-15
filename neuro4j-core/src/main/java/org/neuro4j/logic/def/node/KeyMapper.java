package org.neuro4j.logic.def.node;

import java.util.Set;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
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
						
					} else if(splittedValue[0] != null && splittedValue[0].startsWith(SWFConstants.QUOTES_SYMBOL)) {
						splittedValue[0] = splittedValue[0].replace(SWFConstants.QUOTES_SYMBOL, "");
						ctx.put(splittedValue[1], splittedValue[0]);
						
					} else {
						Object obj = ctx.get(splittedValue[0]);
						ctx.put(splittedValue[1], obj);
					}
				}

			}
		}
		return NEXT;
	}

	public void load(Entity entity) throws FlowInitializationException
	{
		super.load(entity);
		setLoaded(true);
	}

	

}
