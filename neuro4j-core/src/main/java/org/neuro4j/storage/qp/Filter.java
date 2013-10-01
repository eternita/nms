package org.neuro4j.storage.qp;



public class Filter {
//	public ERType erType;
	public String propertyName;
	public String propertyValue;
	public int filterAmount;
	
	@Override
	public String toString() {
		return "[" + propertyName + "=" + propertyValue + "] " + filterAmount;
	}
	
	
}
