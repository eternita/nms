package org.neuro4j.web.console;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.mlw.vlh.ValueList;
import net.mlw.vlh.ValueListInfo;
import net.mlw.vlh.web.ValueListRequestUtil;

import org.neuro4j.web.console.vlh.ColumnEntries;
import org.neuro4j.web.console.vlh.EntryResolver;
import org.neuro4j.web.console.vlh.MultiColsResolved4SolrVLHAdapter;
import org.neuro4j.web.console.vlh.MultiColsResolvedVLHAdapter;
import org.neuro4j.web.console.vlh.ObjectVLHAdapter;
import org.neuro4j.web.console.vlh.ResolvedVLHAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class GeneralHelper 
{

	private static final Logger logger = LoggerFactory.getLogger(GeneralHelper.class);

	
	/**
	 * is used for forwarding to the "PageNotFound"-page
	 */
	public static ModelAndView exitOnError(HttpServletRequest request, Controller controller)
	{
		return exitOnError(request, controller, null);		
	}

	/**
	 * is used for forwarding to the "PageNotFound"-page
	 */
	public static ModelAndView exitOnError(HttpServletRequest request, Controller controller, String message)
	{
		request.setAttribute("redirect_to_home_page", "redirect_to_home_page");
		
		logger.error("PageNotFound with message " + message + ", controller " + controller.getClass().toString() + ", request: " + request);
		if (null != message)
		{
			request.setAttribute("errorMsg", message);
		}
		return new ModelAndView("general/page_not_found");
	}

     

    public static void createVHLList(HttpServletRequest request, 
    		List objs, String language, String datasetName, String datasetId, int rowPerPage)
    {
		Locale locale = Locales.getSupportedLocale(request);
		ObjectVLHAdapter cla2 = new ObjectVLHAdapter();
		cla2.setLocale(locale);
		ValueListInfo vl_info = ValueListRequestUtil.buildValueListInfo(request, datasetId);
		vl_info.setPagingNumberPer(rowPerPage);
		ValueList vl = cla2.getValueList(objs, vl_info);
		request.setAttribute(datasetName, vl);
    	return;
    }
	
    public static void createVHLList(HttpServletRequest request, 
    		String[] ids, String language, EntryResolver resolver, String datasetName, String datasetId, int rowPerPage)
    {
    	ResolvedVLHAdapter cla2 = new ResolvedVLHAdapter(resolver);
		ValueListInfo ci_info = ValueListRequestUtil.buildValueListInfo(request, datasetId);
		ci_info.setPagingNumberPer(rowPerPage);
		ValueList ci_vl = cla2.getValueList(ids, language, ci_info);
			
		request.setAttribute(datasetName, ci_vl);
    	return;
    }
	
    public static void create4SolrVHLList(HttpServletRequest request, 
    		String[] ids, String language, EntryResolver resolver, String datasetName, String datasetId, int rowPerPage, int totalRows)
    {
    	ResolvedVLHAdapter cla2 = new ResolvedVLHAdapter(resolver);
		ValueListInfo ci_info = ValueListRequestUtil.buildValueListInfo(request, datasetId);
		ci_info.setPagingNumberPer(rowPerPage);
		ValueList ci_vl = cla2.getValueList4Solr(ids, language, ci_info, totalRows);
			
		request.setAttribute(datasetName, ci_vl);
    	return;
    }
	
    public static void createMultiColumn4SolrVHLList(HttpServletRequest request, 
    		ColumnEntries[] ids, String language, EntryResolver resolver, 
    		String datasetName, String datasetId, int rowPerPage, int totalRows, int columns)
    {
    	MultiColsResolved4SolrVLHAdapter cla2 = new MultiColsResolved4SolrVLHAdapter(resolver);
		ValueListInfo ci_info = ValueListRequestUtil.buildValueListInfo(request, datasetId);
		ci_info.setPagingNumberPer(rowPerPage);
		ValueList ci_vl = cla2.getValueList(ids, language, ci_info, totalRows, columns);
			
		request.setAttribute(datasetName, ci_vl);
    	return;
    }

    public static void createMultiColumnVHLList(HttpServletRequest request, 
    		ColumnEntries[] ids, String language, EntryResolver resolver, String datasetName, String datasetId, int rowPerPage)
    {
    	MultiColsResolvedVLHAdapter cla2 = new MultiColsResolvedVLHAdapter(resolver);
		ValueListInfo ci_info = ValueListRequestUtil.buildValueListInfo(request, datasetId);
		ci_info.setPagingNumberPer(rowPerPage);
		ValueList ci_vl = cla2.getValueList(ids, language, ci_info, rowPerPage);
			
		request.setAttribute(datasetName, ci_vl);
    	return;
    }
}
