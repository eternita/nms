package org.neuro4j.logic.swf.enums;

public enum DecisionOperators {
	
	EQ_STR(0, "= (string)"), NEQ_STR(1, "!= (string)"), DEFINED(2,"defined"), UNDEFINED(3, "undefined"), EMPTY_STR(4, "empty string"), EQ(5, "=="), NEQ(6, "!="),  HAS_EL(7, "has elements"), LESS(7, "<"), GREATER(8, ">");
	
	private static String[] opNames = new String[]{EQ_STR.getDisplayName(), NEQ_STR.getDisplayName(), DEFINED.getDisplayName(), UNDEFINED.getDisplayName(), EMPTY_STR.getDisplayName(), EQ.getDisplayName(), NEQ.getDisplayName(), HAS_EL.getDisplayName(),LESS.getDisplayName(),GREATER.getDisplayName()};
	
    public int value;
    private String display;

    private DecisionOperators(int value, String display) {
            this.value = value;
            this.display = display;
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
};
