package org.neuro4j.logic.swf.enums;

public enum DecisionOperators {
	
	EQ_STR(0, "= (string)", false), NEQ_STR(1, "!= (string)", false), DEFINED(2,"defined", true), UNDEFINED(3, "undefined", true), EMPTY_STR(4, "empty string", true), EQ(5, "==", false), NEQ(6, "!=", false),  HAS_EL(7, "has elements", true), LESS(8, "<", false), GREATER(9, ">", false);
	
	private static String[] opNames = new String[]{EQ_STR.getDisplayName(), NEQ_STR.getDisplayName(), DEFINED.getDisplayName(), UNDEFINED.getDisplayName(), EMPTY_STR.getDisplayName(), EQ.getDisplayName(), NEQ.getDisplayName(), HAS_EL.getDisplayName(),LESS.getDisplayName(),GREATER.getDisplayName()};
	
    public int value;
    private String display;
    private boolean singleOperand = false;

    private DecisionOperators(int value, String display, boolean singleOperand) {
            this.value = value;
            this.display = display;
            this.singleOperand = singleOperand;
    }
     
    public String getDisplayName()
    {
    	return display;
    }
    
    public static String[] operators(){
    	return opNames;
    }
    
    public static DecisionOperators getByName(String name)
    {
    	for (DecisionOperators d: values())
    	{
    		if (d.name().equals(name))
    		{
    			return d;
    		}
    	}
    	return null;
    }

	public boolean isSingleOperand() {
		return singleOperand;
	}
    
    
};
