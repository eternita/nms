package org.neuro4j.logic.def.node;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicException;
import org.neuro4j.logic.def.LogicBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.logic.swf.SWFParametersConstants;

public class LoopBlock  extends LogicBlock {
	
	private static final String NEXT_EXIT_RELATION = SWFConstants.NEXT_RELATION_NAME;
	private static final String DO_EXIT_RELATION = "LOOP_EXIT";

	private String iteratorKey = null;
	private String elementKey = null;
	
	private String doExit = null;
	private String loopExit = null;
	
	public LoopBlock() {
		super();
	}
	
	public int execute(LogicContext fctx)
			throws FlowExecutionException {
		Object object = null;
		Iterator iteratorObject = fctx.getLoopIterator(this.iteratorKey);
		if (iteratorObject == null) {
			object = fctx.get(iteratorKey);
			if (object == null) {
				cleanOnExit(fctx);
				return NEXT;
			}

			if ((object instanceof Iterable)) {
				iteratorObject = ((Iterable) object).iterator();
			} else if ((object instanceof Collection)) {
				iteratorObject = ((Collection) object).iterator();
			} else if (object.getClass().isArray()) {
				iteratorObject = Arrays.asList((Object[]) object).iterator();
			} else {
				cleanOnExit(fctx);
				return NEXT;
			}

			fctx.putLoopIterator(this.iteratorKey, iteratorObject);
		}

		if (iteratorObject.hasNext()) {
			Object value = iteratorObject.next();
			fctx.put(this.elementKey, value);
			fctx.setNextRelation(doExit);
		} else {
			cleanOnExit(fctx);
		}
		return NEXT;
	}
	
	private void cleanOnExit(LogicContext fctx)
	{
		fctx.setNextRelation(loopExit);
		fctx.removeLoopIterator(this.iteratorKey);
	}

	public void load(Entity entity) throws FlowInitializationException
	{
		super.load(entity);
		iteratorKey = getNotEmptyProperty(SWFParametersConstants.LOOP_NODE_ITERATOR);
		elementKey = getNotEmptyProperty(SWFParametersConstants.LOOP_NODE_ELEMENT);
		
		 List<String> nextRelations = getOutgoingRelationsUUID(DO_EXIT_RELATION);
		 if (nextRelations != null && nextRelations.size() >= 1)
		 {
			 doExit = nextRelations.get(0);
		 }
		 
		 List<String> falseRelations = getOutgoingRelationsUUID(NEXT_EXIT_RELATION);
		 if (falseRelations != null && falseRelations.size() >= 1)
		 {
			 loopExit = falseRelations.get(0);
		 }
		 
		 return;
	}

	@Override
	public void validate(LogicContext ctx) throws FlowExecutionException {
		super.validate(ctx);
		
		if (elementKey == null || iteratorKey == null || doExit == null || loopExit == null)
		{
			throw new FlowExecutionException("LoopBlock: Wrong configuration");
		}

	}	
	

}
