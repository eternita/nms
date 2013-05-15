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
package net.mlw.vlh.web.tag.support;

/** Defines an tag that has attributes that can be set.
 *  Such as a tr tag has align and onclick attributes.
 * 
 * @author Matthew L. Wilson 
 * @version $Revision: 1.2 $ $Date: 2005/01/14 17:26:18 $
 */
public interface ParamAddable
{
	/** Sets the param on the Attributeable object.
	 * 
	 * @param name  The name of the param.
	 * @param value The value of the pram.
	 */
   public void addParam(String key, String value);
}
