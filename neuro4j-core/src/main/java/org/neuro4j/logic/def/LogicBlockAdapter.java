package org.neuro4j.logic.def;

import org.neuro4j.core.ERBase;

/**
 * 
 * Implements persistence for CustomBlock inside Network
 *
 */
public class LogicBlockAdapter extends ERBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void init(ERBase entity) 
	{
	
		setUuid(entity.getUuid());
		setName(entity.getName());
		setLastModifiedDate(entity.getLastModifiedDate());
		setVirtual(entity.isVirtual());
		
		for (String key : entity.getPropertyKeys())
			setProperty(key, entity.getProperty(key));
		
		for(ERBase r: entity.getConnected()){
			addConnected(r);
		}
	
	}

	public LogicBlockAdapter() {
		super();
	}

}
