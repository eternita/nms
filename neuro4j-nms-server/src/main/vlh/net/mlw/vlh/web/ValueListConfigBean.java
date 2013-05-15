package net.mlw.vlh.web;

import java.util.HashMap;
import java.util.Map;

import net.mlw.vlh.web.tag.support.CellInterceptor;
import net.mlw.vlh.web.tag.support.CsvDisplayProvider;
import net.mlw.vlh.web.tag.support.DefaultLinkEncoder;
import net.mlw.vlh.web.tag.support.DisplayProvider;
import net.mlw.vlh.web.tag.support.DivHtmlDisplayProvider;
import net.mlw.vlh.web.tag.support.ExcelDisplayProvider;
import net.mlw.vlh.web.tag.support.GroupingCellInterceptor;
import net.mlw.vlh.web.tag.support.HtmlDisplayProvider;
import net.mlw.vlh.web.tag.support.LinkEncoder;
import net.mlw.vlh.web.util.DisplayHelper;
import net.mlw.vlh.web.util.PassThroughDisplayHelper;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * 
 * net.mlw.vlh.web.ValueListConfigBean
 * 
 * @author Matthew Wilson, Andrej Zachar
 */
public class ValueListConfigBean
{

	public static final String DEFAULT_NAME = "languages/icollector";

	public static final CellInterceptor DEFAULT_CELL_INTERCEPTOR = new GroupingCellInterceptor();
	
	public static final int DEFAULT_STYLE_COUNT = 2;

	public static final String DEFAULT_STYLE_PREFIX = "vhl_tr";

	public static final DisplayProvider DEFAULT_DISPLAY_PROVIDER = new HtmlDisplayProvider();

	public static final LocaleResolver DEFAULT_LOCALE_RESOLVER = new SessionLocaleResolver();

	public static final DisplayHelper DEFAULT_DISPLAY_HELPER = new PassThroughDisplayHelper();

	public static final LinkEncoder DEFAULT_LINK_ENCODER = new DefaultLinkEncoder();

	public static final ResourceBundleMessageSource DEFAULT_MESSAGE_SOURCE = new ResourceBundleMessageSource();

	public static final Map DEFAULT_DISPLAY_PROVIDERS = new HashMap();
	static
	{
		DEFAULT_MESSAGE_SOURCE.setBasename("languages/vlh");

		DEFAULT_DISPLAY_PROVIDERS.put("html", new HtmlDisplayProvider());
		DEFAULT_DISPLAY_PROVIDERS.put("div", new DivHtmlDisplayProvider());
		DEFAULT_DISPLAY_PROVIDERS.put("csv", new CsvDisplayProvider());
		DEFAULT_DISPLAY_PROVIDERS.put("excel", new ExcelDisplayProvider());

	}

	private CellInterceptor cellInterceptor = DEFAULT_CELL_INTERCEPTOR;
	
	private String nullToken = "-";

	private int styleCount = DEFAULT_STYLE_COUNT;

	private String stylePrefix = DEFAULT_STYLE_PREFIX;

	private MessageSource messageSource = DEFAULT_MESSAGE_SOURCE;

	private DisplayHelper displayHelper = DEFAULT_DISPLAY_HELPER;

	private LocaleResolver localeResolver = DEFAULT_LOCALE_RESOLVER;

	private Map displayProviders;

	private LinkEncoder linkEncoder = DEFAULT_LINK_ENCODER;

	private String imageRoot = "/images";
	/**
	 * @return Returns the displayProviders.
	 */
	public DisplayProvider getDisplayProvider(String name)
	{
		if (displayProviders == null)
		{
			displayProviders =  DEFAULT_DISPLAY_PROVIDERS;
		}

		DisplayProvider display = (DisplayProvider) displayProviders.get(name);
		if (display == null)
		{
			display = (DisplayProvider) DEFAULT_DISPLAY_PROVIDERS.get(name);
		}
		if (display == null)
		{
			display = DEFAULT_DISPLAY_PROVIDER;
		}

		return display;
	}

	/**
	 * @param displayProviders
	 *            The displayProviders to set.
	 */
	public void setDisplayProviders(Map displayProviders)
	{
		this.displayProviders = displayProviders;
	}

	/**
	 * @return Returns the displayHelper.
	 */
	public DisplayHelper getDisplayHelper()
	{
		return displayHelper;
	}

	/**
	 * @param displayHelper
	 *            The displayHelper to set.
	 */
	public void setDisplayHelper(DisplayHelper displayHelper)
	{
		this.displayHelper = displayHelper;
	}

	/**
	 * @return Returns the nullToken.
	 */
	public String getNullToken()
	{
		return nullToken;
	}

	/**
	 * @param nullToken
	 *            The nullToken to set.
	 */
	public void setNullToken(String nullToken)
	{
		this.nullToken = nullToken;
	}

	/**
	 * @return Returns the styleCount.
	 */
	public int getStyleCount()
	{
		return styleCount;
	}

	/**
	 * @param styleCount
	 *            The styleCount to set.
	 */
	public void setStyleCount(int styleCount)
	{
		this.styleCount = styleCount;
	}

	/**
	 * @return Returns the stylePrefix.
	 */
	public String getStylePrefix()
	{
		return stylePrefix;
	}

	/**
	 * @param stylePrefix
	 *            The stylePrefix to set.
	 */
	public void setStylePrefix(String stylePrefix)
	{
		this.stylePrefix = stylePrefix;
	}

	/**
	 * @return Returns the messageSource.
	 */
	public MessageSource getMessageSource()
	{
		if (messageSource == null)
		{
			ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//			messageSource.setBasename("classicLook");
			messageSource.setBasename("languages/vlh");
			this.messageSource = messageSource;
		}

		return messageSource;
	}

	/**
	 * @param messageSource
	 *            The messageSource to set.
	 */
	public void setMessageSource(MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	/**
	 * @return Returns the localeResolver.
	 */
	public LocaleResolver getLocaleResolver()
	{
		return localeResolver;
	}

	/**
	 * @param localeResolver
	 *            The localeResolver to set.
	 */
	public void setLocaleResolver(LocaleResolver localeResolver)
	{
		this.localeResolver = localeResolver;
	}

	/**
	 * @return Returns the linkEncoder.
	 */
	public LinkEncoder getLinkEncoder()
	{
		return linkEncoder;
	}

	/**
	 * @param linkEncoder The linkEncoder to set.
	 */
	public void setLinkEncoder(LinkEncoder linkEncoder)
	{
		this.linkEncoder = linkEncoder;
	}

    /**
     * @return String  The style for focused row.
     */
    public String getFocusedRowStyle() {        
        return getStylePrefix()+"FocusedRow";
    }
   /**
    * @return Returns the cellInterceptor.
    */
   public CellInterceptor getCellInterceptor()
   {
      return cellInterceptor;
   }
   /**
    * @param cellInterceptor The cellInterceptor to set.
    */
   public void setCellInterceptor(CellInterceptor cellInterceptor)
   {
      this.cellInterceptor = cellInterceptor;
   }
   /**
    * @return Returns the imageRoot.
    */
   public String getImageRoot()
   {
      return imageRoot;
   }
   /**
    * @param imageRoot The imageRoot to set.
    */
   public void setImageRoot(String imageRoot)
   {
      this.imageRoot = imageRoot;
   }
}