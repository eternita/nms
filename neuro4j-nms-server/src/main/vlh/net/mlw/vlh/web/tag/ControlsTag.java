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

import net.mlw.vlh.ValueList;
import net.mlw.vlh.web.ValueListConfigBean;
import net.mlw.vlh.web.tag.support.ColumnInfo;
import net.mlw.vlh.web.util.JspUtils;

/**
 * This tag creates a column in a row with "listeners for actions".
 * 
 * @todo Document this tag.
 * 
 * @author  Andrej Zachar
 * @version $Revision: 1.6 $ $Date: 2005/11/23 15:02:16 $
 * @deprecated use vlh:column with omitted parameters 'property'
 */
public class ControlsTag extends BaseColumnTag
{

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {

      if (bodyContent != null)
      {
         bodyContent.clearBody();
      }

      //If the valuelist is empty, skip the tag...

      ValueListSpaceTag rootTag = (ValueListSpaceTag) JspUtils.getParent(this, ValueListSpaceTag.class);

      ValueList valueList = rootTag.getValueList();
      if (valueList == null || rootTag.getValueList().getList() == null || rootTag.getValueList().getList().size() == 0)
      {
         return SKIP_BODY;
      }
      else
      {
         return super.doStartTag();
      }
   }

   /**
    * @see javax.servlet.jsp.tagext.Tag#doEndTag()
    */
   public int doEndTag() throws JspException
   {
      ValueListSpaceTag rootTag = (ValueListSpaceTag) JspUtils.getParent(this, ValueListSpaceTag.class);
      ValueListConfigBean config = rootTag.getConfig();
      DefaultRowTag rowTag = (DefaultRowTag) JspUtils.getParent(this, DefaultRowTag.class);
      appendClassCellAttribute(rowTag.getRowStyleClass());
      Locale locale = config.getLocaleResolver().resolveLocale((HttpServletRequest) pageContext.getRequest());

      if (rowTag.getCurrentRowNumber() == 0)
      {
         String titleKey = getTitleKey();
         String label = (titleKey == null) ? getTitle() : config.getMessageSource().getMessage(titleKey, null, titleKey, locale);

         ColumnInfo columnInfo = new ColumnInfo(config.getDisplayHelper().help(pageContext, label), null, null, getAttributes());
         
         // toolTip or toolTipKey is set => get the string and put it into the ColumnInfo
         String toolTipKey = getToolTipKey();
         columnInfo.setToolTip((toolTipKey == null) ? getToolTip() : config.getMessageSource().getMessage(toolTipKey, null, toolTipKey,
               locale));

         rowTag.addColumnInfo(columnInfo);
      }

      StringBuffer sb = new StringBuffer(rowTag.getDisplayProvider().getCellPreProcess(getCellAttributes()));

      //AAA doesIncludeBodyContent need it here? or it is needed in controls? i think no!, or add some new features to display providers?

      if (bodyContent != null && bodyContent.getString() != null && bodyContent.getString().trim().length() > 0)
      {
         sb.append(bodyContent.getString().trim());
         bodyContent.clearBody();
      }
      sb.append(rowTag.getDisplayProvider().getCellPostProcess());
      JspUtils.write(pageContext, sb.toString());

      release();

      return EVAL_PAGE;
   }
}