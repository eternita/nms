package org.neuro4j.core.rel;

import org.neuro4j.core.Constants;
import org.neuro4j.core.ERBase;



public class DirectionRelation extends ERBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;

	public final static String FROM_KEY = "StartUUID"; // start
	public final static String TO_KEY = "EndUUID"; // end
	
	private ERBase start = null;
	private ERBase end = null;
	
	public DirectionRelation() {
		super("Don't use - it's just for cloning.");
		// TODO read representations or throw Exception
	}

	public DirectionRelation(String relation, ERBase b1, ERBase b2) {
		super(relation, b1, b2);
		setProperty(FROM_KEY, b1.getUuid());
		setProperty(TO_KEY, b2.getUuid());
		start = b1;
		end = b2;
		
	}

	public ERBase getStart()
	{
		return start;
	}

	public ERBase getEnd()
	{
		return end;
	}
}
