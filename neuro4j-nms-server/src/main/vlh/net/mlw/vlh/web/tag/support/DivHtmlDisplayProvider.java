/**
 * Copyright (c) 2003 held jointly by the individual authors.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as active by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *  > http://www.gnu.org/copyleft/lesser.html >
 * http://www.opensource.org/licenses/lgpl-license.php
 */
package net.mlw.vlh.web.tag.support;

import net.mlw.vlh.ValueListInfo;


/**
 * @author Matthew L. Wilson, Andrej Zachar
 * @version $Revision: 1.20 $ $Date: 2005/11/23 14:37:15 $
 */
public class DivHtmlDisplayProvider extends HtmlDisplayProvider
{
	  public String getHeaderRowPostProcess()
	   {
	      return "";
	   }

	   /** 
	    * Get the HTML that comes before the column text. 
	    *  
	    * @return The HTML that comes before the column text. 
	    */
	   public String getHeaderRowPreProcess()
	   {
	      return "";
	   }

	   public String getMimeType()
	   {
	      return null;
	   }

	   public String getRowPostProcess()
	   {
	      return "";
	   }

	   public String getRowPreProcess(Attributes attributes)
	   {
	      return "";
	   }
	   public String getNestedHeaderPreProcess(ColumnInfo columnInfo, ValueListInfo info)
	   {
	      return "";
	   }

	   public String getNestedHeaderPostProcess()
	   {
	      return "";
	   }

}