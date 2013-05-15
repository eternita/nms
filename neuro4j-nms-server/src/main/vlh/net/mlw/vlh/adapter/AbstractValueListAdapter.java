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
package net.mlw.vlh.adapter;

import net.mlw.vlh.ValueListAdapter;
import net.mlw.vlh.ValueListInfo;

/**
 * Default abstract implementation of a ValueListAdapter.
 * 
 * @author mwilson
 */
public abstract class AbstractValueListAdapter implements ValueListAdapter
{
  /** @see net.mlw.vlh.ValueListAdapter#getAdapterType() */
  private int adapterType = DO_NOTHING;

  /** The default number of entries per page. * */
  private int defaultNumberPerPage = 50;

  /** The default column to sort by. * */
  private String defaultSortColumn;

  /** The default column to sort by. * */
  private Integer defaultSortDirection = ValueListInfo.ASCENDING;

  /**
   * @see net.mlw.vlh.ValueListAdapter#getAdapterType()
   */
  public int getAdapterType()
  {
    return adapterType;
  }

  /** Sets the adatper type.
   * 
   * @see net.mlw.vlh.ValueListAdapter#getAdapterType()
   */
  public void setAdapterType(int adapterType)
  {
    this.adapterType = adapterType;
  }

  /** Sets the default number of entries per page.
   * 
   * @param defaultNumberPerPage
   *          The default number of entries per page.
   */
  public void setDefaultNumberPerPage(int defaultNumberPerPage)
  {
    this.defaultNumberPerPage = defaultNumberPerPage;
  }

  /**  Gets the default number of entries per page.
   * 
   * @return Returns the defaultNumberPerPage.
   */
  public int getDefaultNumberPerPage()
  {
    return defaultNumberPerPage;
  }

  /** Sets the default sortColumn if none is present in the filters.
   * 
   * @param defaultSortColumn
   *          The defaultSortColumn to set.
   */
  public void setDefaultSortColumn(String defaultSortColumn)
  {
    this.defaultSortColumn = defaultSortColumn;
  }

  /**
   * @return Returns the defaultSortColumn.
   */
  public String getDefaultSortColumn()
  {
    return defaultSortColumn;
  }

  /**
   * Sets the default sort directon is none is supplied in the Map. Valid
   * values: asc|desc
   * 
   * @param defaultSortDirection
   *          The defaultSortDirection to set.
   */
  public void setDefaultSortDirection(String defaultSortDirection)
  {
    this.defaultSortDirection = "desc".equals(defaultSortDirection) ? ValueListInfo.DESCENDING : ValueListInfo.ASCENDING;
  }

  /** Gets the sort direction defined as the default.
   * 
   * @return Returns the defaultSortDirection.
   */
  public Integer getDefaultSortDirectionInteger()
  {
    return defaultSortDirection;
  }
}