package org.neuro4j.weblog.block;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.CustomBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.ParameterDefinition;
import org.neuro4j.logic.swf.ParameterDefinitionList;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

@ParameterDefinitionList(input={
							@ParameterDefinition(name="request", isOptional = false, type="org.neuro4j.core.ERBase")})
public class BindRequestToSession extends CustomBlock {

	final static String IN_REQUEST = "request";
	static final String CURRENT_STORAGE = "CURRENT_STORAGE";
	
	public int execute(LogicContext ctx) throws FlowExecutionException {
		
		Connected request = (Connected) ctx.get(IN_REQUEST);
		Storage currentStorage = (Storage) ctx.get(CURRENT_STORAGE); 
		Network net = null;
		try {
			String sessionIdStr = request.getProperty("session-id");
			if (null == sessionIdStr)
				return NEXT;
			
			net = currentStorage.query("select r(id=?)", new String[]{sessionIdStr});
			
			if (null == net || net.getSize() == 0)
			{
				net = currentStorage.query("INSERT (id=? name=? r_type=? host=? request-start-time=?)", 
							new String[]{sessionIdStr, sessionIdStr, "session", request.getProperty("host"), request.getProperty("request-start-time")});
			}
			
			Connected session = (Connected) net.getById(sessionIdStr);
			if (null == session)
				return NEXT;

			
			if (!request.isConnectedTo(session.getUuid()))
			{
				net.add(request);
				session.addConnected(request);
				currentStorage.save(net);
			}
			
		} catch (NQLException e) {
			e.printStackTrace();
			return ERROR;
		} catch (StorageException e) {
			e.printStackTrace();
			return ERROR;
		}
			
		return NEXT;
	}
	
	
}
