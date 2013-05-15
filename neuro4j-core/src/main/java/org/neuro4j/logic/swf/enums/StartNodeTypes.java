package org.neuro4j.logic.swf.enums;

public enum StartNodeTypes {
	
	PUBLIC(0, "Public"), PRIVATE(1, "Private");
	
	private static String[] opNames = new String[]{PUBLIC.getDisplayName(), PRIVATE.getDisplayName()};
	
    public int value;
    private String display;

    private StartNodeTypes(int value, String display) {
            this.value = value;
            this.display = display;
    }
     
    public String getDisplayName()
    {
    	return display;
    }
    
    public static String[] types(){
    	return opNames;
    }
    
    public static StartNodeTypes getByDisplayName(String displayName)
    {
    	for (StartNodeTypes d: values())
    	{
    		if (d.display.equals(displayName))
    		{
    			return d;
    		}
    	}
    	return null;
    }
    
    public static StartNodeTypes getByName(String name)
    {
    	for (StartNodeTypes d: values())
    	{
    		if (d.name().equals(name))
    		{
    			return d;
    		}
    	}
    	return null;
    }
    
    public  static StartNodeTypes getDefaultType()
    {
    	return PUBLIC;
    }
};
