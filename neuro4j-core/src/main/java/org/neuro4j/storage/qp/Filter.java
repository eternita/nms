package org.neuro4j.storage.qp;

import org.neuro4j.storage.qp.ERType;


public class Filter {
	public ERType erType;
	public String propertyName;
	public String propertyValue;
	public int filterAmount;
	
	@Override
	public String toString() {
		return erType + "[" + propertyName + "=" + propertyValue + "] " + filterAmount;
	}
	
	
}
