package org.neuro4j.web.console.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.Connected;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorFactory;
import org.neuro4j.logic.LogicProcessorNotFoundException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.nms.server.NMSServerConfig;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.web.console.utils.RequestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ActionController {

	private static String logicProcessorStr = "org.neuro4j.logic.def.DefaultLogicProcessor"; // TODO: load ...
	
	private static LogicProcessor logicProcessor = null;
	static {
		try {
			logicProcessor = LogicProcessorFactory.getLogicProcessor(logicProcessorStr);
		} catch (LogicProcessorNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@RequestMapping("/init-action")
	public String startAction(HttpServletRequest request) throws StorageException 
	{
		RequestUtils.params2attributes(request, "eid", "storage");
		String startNodeId = request.getParameter("eid");
		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == storage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			return "console/settings";
		}		

		Connected e = storage.getById(startNodeId);
		request.setAttribute("entity", e);
		
		return "console/a/init";
	}

	@RequestMapping("/run-action")
	public String runAction(HttpServletRequest request) throws StorageException 
	{
		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == storage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			return "console/settings";
		}
		
		String startNodeId = request.getParameter("eid");
		String ctxParametersStr = request.getParameter("ctx_params");
		
		Map<String, String> params = parseCtxParameters(ctxParametersStr);
			
		LogicContext logicContext = new LogicContext();
		logicContext.put(SWFConstants.AC_NEURO_STORAGE, storage);
		logicContext.put(SWFConstants.AC_LOGIC_PROCESSOR, logicProcessor);
		logicContext.put(SWFConstants.AC_REQUEST, request);

		// request params to context
		for (String key : params.keySet())
			logicContext.put(key, params.get(key));
		
		Connected e = storage.getById(startNodeId);
		try {
			logicProcessor.action(e, null, storage, logicContext);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		request.setAttribute("LOGIC_CONTEXT", logicContext);

		return "console/a/results";
	}
	
	private Map<String, String> parseCtxParameters(String paramsStr)
	{
		Map<String, String> paramsMap = new HashMap<String, String>();
		String[] params = paramsStr.split("\n");
		
		for (String param : params)
		{
			String[] s = param.split(":");
			if (s.length > 1)
			{
				String key = s[0];
				String value = s[1]; 
				paramsMap.put(key.trim(), value.trim());
			}
		}		
		
		return paramsMap;
	}

}