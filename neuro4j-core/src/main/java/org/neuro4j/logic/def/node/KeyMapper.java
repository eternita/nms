package org.neuro4j.logic.def.node;

import java.util.Set;

import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.SWEUtils;
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
					evaluateParameterValue(splittedValue[0], splittedValue[1], ctx);
				}

			}
		}
		return NEXT;
	}
	



}
