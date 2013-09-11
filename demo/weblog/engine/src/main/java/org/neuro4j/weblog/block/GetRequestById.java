package org.neuro4j.weblog.block;


import static org.neuro4j.weblog.block.GetRequestById.IN_REQUESTID;
import static org.neuro4j.weblog.block.GetRequestById.OUT_REQUEST;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.CustomBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.ParameterDefinition;
import org.neuro4j.logic.swf.ParameterDefinitionList;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

@ParameterDefinitionList(input={
                                	@ParameterDefinition(name=IN_REQUESTID, isOptional=false, type= "java.lang.String")},
                         output={
                         	        @ParameterDefinition(name=OUT_REQUEST, isOptional=false, type= "org.neuro4j.core.Entity")})	
public class GetRequestById extends CustomBlock {
    
	static final String CURRENT_STORAGE = "CURRENT_STORAGE";

	static final String IN_REQUESTID = "requestId";
      
    static final String OUT_REQUEST = "request"; 
    
    
    @Override
    public int execute(LogicContext ctx) throws FlowExecutionException {
		
    	String requestId = (String) ctx.get(IN_REQUESTID);
        
		Storage currentStorage = (Storage) ctx.get(CURRENT_STORAGE); 
		Network net = null;
		try {
			net = currentStorage.query("select e(id=?)", new String[]{requestId});
			
			if (null == net || net.getSize() == 0)
				return NEXT;
			
			Entity request = (Entity) net.getById(requestId);
			if (null == request)
				return NEXT;
			
			ctx.put(OUT_REQUEST, request); 
			
		} catch (NQLException e) {
			e.printStackTrace();
			return ERROR;
		} catch (StorageException e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return NEXT;
	}
	
	@Override
	protected void init() throws FlowInitializationException{
		super.init();
	}
	

}
