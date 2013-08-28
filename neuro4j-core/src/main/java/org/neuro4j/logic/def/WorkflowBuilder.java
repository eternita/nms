package org.neuro4j.logic.def;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.core.rel.DirectionRelation;

public class WorkflowBuilder {

	public final static String NEXT_RELATION_NAME = "Next";
	public final static String ERROR_RELATION_NAME = "Error";
	
	public static void addSequence(Network net, LogicBlock... blocks)
	{
		if (blocks.length < 2)
		{
			net.add(blocks[0].getLogicBlockAdapter());
			return;
		}
		
		for (int i=0; i<blocks.length-1; i++)
			addSequence(net, blocks[i], blocks[i+1], NEXT_RELATION_NAME);

		return;
	}


	/**
	 * 
	 * @param net
	 * @param b1
	 * @param b2
	 * @param relation {Next | Error}
	 */
	public static void addSequence(Network net, LogicBlock b1, LogicBlock b2, String relation)
	{
		net.add(b1.getLogicBlockAdapter());
		net.add(b2.getLogicBlockAdapter());
		
		ERBase r = new DirectionRelation(
				relation, 
				b1.getLogicBlockAdapter(), 
				b2.getLogicBlockAdapter());

		net.add(r);		

		return;
	}
	
}
