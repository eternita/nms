package org.neuro4j.core.rel;

import org.neuro4j.core.Constants;
import org.neuro4j.core.Connected;



public class DirectionRelation extends Connected {

	/**
	 * 
	 */
	private static final long serialVersionUID = Constants.SERIALIZATION_VERSION_UID;

	public final static String FROM_KEY = "StartUUID"; // start
	public final static String TO_KEY = "EndUUID"; // end
	
	private Connected start = null;
	private Connected end = null;
	
	public DirectionRelation() {
		super("Don't use - it's just for cloning.");
		// TODO read representations or throw Exception
	}

	public DirectionRelation(String relation, Connected b1, Connected b2) {
		super(relation, b1, b2);
		setProperty(FROM_KEY, b1.getUuid());
		setProperty(TO_KEY, b2.getUuid());
		start = b1;
		end = b2;
		
	}

	public Connected getStart()
	{
		return start;
	}

	public Connected getEnd()
	{
		return end;
	}
}
