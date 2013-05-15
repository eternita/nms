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
package org.neuro4j.web.console.vlh;

import java.util.ArrayList;
import java.util.List;

import net.mlw.vlh.DefaultListBackedValueList;
import net.mlw.vlh.ValueList;
import net.mlw.vlh.ValueListInfo;
import net.mlw.vlh.adapter.AbstractValueListAdapter;

public class MultiColsResolved4SolrVLHAdapter extends AbstractValueListAdapter {


	private EntryResolver resolver;

	public MultiColsResolved4SolrVLHAdapter(EntryResolver resolver) {
		setAdapterType(DO_SORT);
		this.resolver = resolver;
	}
	
	public ValueList getValueList(ColumnEntries[] ids, String language, ValueListInfo info, int totalRows, int columns) 
	{
		if (info.getSortingColumn() == null) {
			info.setPrimarySortColumn(getDefaultSortColumn());
			info.setPrimarySortDirection(getDefaultSortDirectionInteger());
		}

		if (info.getPagingNumberPer() == Integer.MAX_VALUE) {
			info.setPagingNumberPer(getDefaultNumberPerPage());
		}
		int numberPerPage = info.getPagingNumberPer();

		if (numberPerPage == Integer.MAX_VALUE) {
			numberPerPage = getDefaultNumberPerPage();
			info.setPagingNumberPer(numberPerPage);
		}

//		int totalRows = ids.length;
//		int displayTotalRows = 0;
		int numberOfEntries = totalRows/columns;
		if (columns*numberOfEntries < totalRows)
			numberOfEntries++;
		info.setTotalNumberOfEntries(numberOfEntries);
//		displayTotalRows = totalRows;
		info.setTotalDisplayedNumberOfEntries(totalRows);

		int startPosition = 0;

		if (numberPerPage == 0) {
			numberPerPage = getDefaultNumberPerPage();
		}

		int pageNumber = info.getPagingPage();
		if (pageNumber > 1) {
			if ((pageNumber - 1) * numberPerPage > totalRows) {
				pageNumber = ((totalRows - 1) / numberPerPage) + 1;
				info.setPagingPage(pageNumber);
			}
		}

		if (pageNumber > 1) {
			startPosition = ((pageNumber - 1) * numberPerPage);
		} else {
			startPosition = 0;
		}
		int endPosition = (startPosition + numberPerPage >= totalRows) ? totalRows
				: (startPosition + numberPerPage);

		List list = new ArrayList(numberPerPage);
		// put objects that belong to current page to list
		for (int i = 0; i < ids.length; i++) {
			
			ColumnEntries ep = ids[i];
			ep.setResolver(resolver, language);
			list.add(ep);	
		}
		return new DefaultListBackedValueList(list, info);
	}
	
	/**
	 * return all coins
	 */
	public ValueList getValueList(String name, ValueListInfo info) {

		throw new RuntimeException("method is not implemented");
	}

//	/**
//	 * @return the locale
//	 */
//	public Locale getLocale() {
//		return locale;
//	}
//
//	/**
//	 * @param locale the locale to set
//	 */
//	public void setLocale(Locale locale) {
//		this.locale = locale;
//	}

}