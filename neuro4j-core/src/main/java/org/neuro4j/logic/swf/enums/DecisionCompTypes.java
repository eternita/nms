package org.neuro4j.logic.swf.enums;

public enum DecisionCompTypes {
	
	constant(0, "constant value"), context(1, "value from context");
	
	private static String[] opNames = new String[]{constant.getDisplayName(), context.getDisplayName()};
	
    public int value;
    private String display;

    private DecisionCompTypes(int value, String display) {
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
    
    public static DecisionCompTypes getByName(String name)
    {
    	for (DecisionCompTypes d: values())
    	{
    		if (d.name().equals(name))
    		{
    			return d;
    		}
    	}
    	return null;
    }
};
