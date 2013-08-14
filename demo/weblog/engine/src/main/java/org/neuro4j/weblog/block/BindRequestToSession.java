package org.neuro4j.weblog.block;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.CustomBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.ParameterDefinition;
import org.neuro4j.logic.swf.ParameterDefinitionList;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

@ParameterDefinitionList(input={
							@ParameterDefinition(name="request", isOptional = false, type="org.neuro4j.core.Entity")})
public class BindRequestToSession extends CustomBlock {

	final static String IN_REQUEST = "request";
	static final String CURRENT_STORAGE = "CURRENT_STORAGE";
	
	public int execute(LogicContext ctx) throws FlowExecutionException {
		
		Entity request = (Entity) ctx.get(IN_REQUEST);
		Storage currentStorage = (Storage) ctx.get(CURRENT_STORAGE); 
		Network net = null;
		try {
			String sessionIdStr = request.getProperty("session-id");
			if (null == sessionIdStr)
				return NEXT;
			
			net = currentStorage.query("select r(id=?)", new String[]{sessionIdStr});
			
			if (null == net || net.getSize() == 0)
			{
				net = currentStorage.query("INSERT R(id=? name=? r_type=? host=? request-start-time=?)", 
							new String[]{sessionIdStr, sessionIdStr, "session", request.getProperty("host"), request.getProperty("request-start-time")});
			}
			
			Relation session = (Relation) net.getById(sessionIdStr);
			if (null == session)
				return NEXT;

			
			if (!request.isConnectedTo(session.getUuid()))
			{
				net.add(request);
				session.addParticipant(request);
				currentStorage.save(net);
			}
			
		} catch (NQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERROR;
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERROR;
		}
			
		return NEXT;
	}
	
	
}
