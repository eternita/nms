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
package net.mlw.vlh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Wrapper for a <CODE>List</CODE> and a <CODE>ValueListInfo</CODE> object.
 *  This is the Default implementation of ValueList.
 *  
 * @author Matthew L. Wilson, Andrej Zachar
 * @version $Revision: 1.10 $ $Date: 2005/08/19 15:46:25 $
 */
public class DefaultListBackedValueList implements ValueList
{
	private List list = null;
	private ValueListInfo info = null;
	private Iterator iter = null;

	/** Creates a new instance of DefaultValueList
	 */
	public DefaultListBackedValueList()
	{
		this(new ArrayList(), new ValueListInfo());
	}

	/** Default constructor.
	 * Here is always the same the list.size and info.getTotalNumberOfEntries.
	 * @param list The List to be sorted.
	 */
	public DefaultListBackedValueList(List list)
	{
        this.list = list;
		this.info = new ValueListInfo();
        if (list != null)
        {
            info.setTotalNumberOfEntries(list.size());            
        }
        
	}

	/** Default constructor.
	 * Warning! list.size and info.getTotalNumberOfEntries could be different!
	 * @param list The List to be sorted.
	 * @param info The sorting/paging/filtering info.
	 */
	public DefaultListBackedValueList(List list, ValueListInfo info)
	{
		this.list = list;
		this.info = info;
	}

	/** @see com.mlw.vlh.ValueList.getList()
	 */
	public List getList()
	{
		return list;
	}

	/** @see com.mlw.vlh.ValueList.getListInfo()
	 */
	public ValueListInfo getValueListInfo()
	{
		return info;
	}

	/** @see com.mlw.vlh.ValueList.hasNext()
	 */
	public boolean hasNext()
	{
		if (iter == null && list != null)
		{
			this.iter = list.iterator();
		}

		return (iter == null) ? false : iter.hasNext();
	}

	/** @see java.util.Iterator#next()
	 */
	public Object next() throws NoSuchElementException
	{
		if (iter == null)
		{
			if (list != null)
			{
				this.iter = list.iterator();
			}
			else
			{
				throw new NoSuchElementException();
			}
		}

		return iter.next();
	}

	/** @see java.util.Iterator#remove()
	 */
	public void remove()
	{
	}
	
	/** Resets the Iterator in this ValueList.
	 */
	public void reset()
	{
	   iter = null;
	}
}
