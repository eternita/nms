/**
 * Copyright (c) 2003 held jointly by the individual authors.            
 *                                                                          
 * This library is free software; you can redistribute it and/or modify it    
 * under the terms of the GNU Lesser General Public License as published      
 * by the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.                                            
 *                                                                            
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; with out even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU Lesser General Public License for more details.                                                  
 *                                                                           
 * You should have received a copy of the GNU Lesser General Public License   
 * along with this library;  if not, write to the Free Software Foundation,   
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.              
 *                                                                            
 * > http://www.gnu.org/copyleft/lesser.html                                  
 * > http://www.opensource.org/licenses/lgpl-license.php
 */
package net.mlw.vlh.web.tag;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import net.mlw.vlh.ValueListInfo;
import net.mlw.vlh.web.util.DisplayHelper;
import net.mlw.vlh.web.util.JspUtils;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * Create focus status table. If info.isFocusEnabled() is false, skip all body
 * content. <b>Warning </b> info.doFosus is diferent that info.isFocuseEnabled.
 * 
 * @see net.mlw.vlh.web.ValueListInfo#isFocusEnabled
 * @see net.mlw.vlh.web.ValueListInfo#doFocus
 * @author Andrej Zachar
 * @version $Revision: 1.8 $ $Date: 2005/11/23 15:02:16 $
 */
public class FocusStatusTag extends ConfigurableTag
{

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      ValueListSpaceTag rootTag = (ValueListSpaceTag) JspUtils.getParent(this, ValueListSpaceTag.class);

      Locale local = rootTag.getConfig().getLocaleResolver().resolveLocale((HttpServletRequest) pageContext.getRequest());
      MessageSource message = rootTag.getConfig().getMessageSource();

      ValueListInfo info = rootTag.getValueList().getValueListInfo();
      if (info.isFocusEnabled())
      {
         StringBuffer sb = new StringBuffer();

         sb.append("<table ").append(getCellAttributes().getCellAttributesAsString()).append(">");
         sb.append("\n <tr>");
         sb.append(generateFocusStatus(info, message, local, rootTag));
         sb.append("\n </tr>");
         sb.append("\n <tr>");
         JspUtils.write(pageContext, sb.toString());
         pageContext.setAttribute("focusStatus" + rootTag.getTableInfo().getId(), new Integer(info.getFocusStatus()));
         return EVAL_BODY_INCLUDE;
      }
      return SKIP_BODY;

   }

   /**
    *
    * @param info
    * @param message
    * @param local
    * @param rootTag
    * @return
    * @throws NoSuchMessageException
    * @throws JspException
    */
   private StringBuffer generateFocusStatus(ValueListInfo info, MessageSource message, Locale local, ValueListSpaceTag rootTag)
         throws NoSuchMessageException, JspException
   {

      StringBuffer sb = new StringBuffer("");

      if ((!info.isDoFocus()) || (info.getFocusStatus() == ValueListInfo.FOCUS_FOUND))
      {
         return sb;
      }

      DisplayHelper displayHelper = rootTag.getConfig().getDisplayHelper();

      sb.append("\n  <td>");
      if (info.getFocusStatus() == ValueListInfo.FOCUS_NOT_FOUND)
      {
         sb.append(displayHelper.help(pageContext, message.getMessage("focusStatus.notFound", new String[]
         { info.getFocusValue() }, local)));
      }
      else
      {
         if (info.getFocusStatus() == ValueListInfo.FOCUS_TOO_MANY_ITEMS)
         {
            sb.append(displayHelper.help(pageContext, message.getMessage("focusStatus.tooManyItems", null, local)));
         }
      }
      sb.append("</td>");
      String delim = displayHelper.help(pageContext, message.getMessage("paging.delim", null, "", local));
      if (delim.length() > 0)
      {
         sb.append(delim);
      }
      return sb;
   }

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doEndTag() throws JspException
   {
      ValueListSpaceTag rootTag = (ValueListSpaceTag) JspUtils.getParent(this, ValueListSpaceTag.class);
      ValueListInfo info = rootTag.getValueList().getValueListInfo();
      if (info.isFocusEnabled())
      {
         JspUtils.write(pageContext, "\n </tr>\n</table>");
      }

      release();

      return EVAL_PAGE;
   }
}