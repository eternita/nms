package org.neuro4j.logic.def.node;

import org.neuro4j.logic.swf.SWFConstants;

public class JumpBlock extends CallBlock {
	
	public JumpBlock(String name) {
		super();
		lba.setProperty(SWFConstants.N4J_CONSOLE_NODE_TYPE, "star"); 
	}

}
