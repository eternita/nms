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
package net.mlw.vlh.adapter.file;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.mlw.vlh.DefaultListBackedValueList;
import net.mlw.vlh.ValueList;
import net.mlw.vlh.ValueListInfo;
import net.mlw.vlh.adapter.AbstractValueListAdapter;

/**
 * This ValueListAdapter returns a ValueList of FileInfo(s).
 * 
 * net.mlw.vlh.adapter.file.FileSystemAdapter
 * 
 * @author Matthew L. Wilson
 * @version $Revision: 1.6 $ $Date: 2005/08/19 16:04:30 $
 */
public class FileSystemAdapter extends AbstractValueListAdapter
{
   /** The name of the filter defining the current directory. */
   public static final String FOLDER        = "folder";

   /** The default starting directory, if FOLDER was not in the filters. */
   private String             defaultFolder = ".";

   private String             dateFormat    = "MM/dd/yyyy";

   /**
    * Default constructor.
    */
   public FileSystemAdapter()
   {
      setAdapterType(DO_SORT);
   }

   /**
    * @see net.mlw.vlh.ValueListAdapter#getValueList(java.lang.String, net.mlw.vlh.ValueListInfo)
    */
   public ValueList getValueList(String name, ValueListInfo info)
   {
      SimpleDateFormat df = new SimpleDateFormat(dateFormat);

      Date startDate = null;
      Date endDate = null;

      try
      {
         String stringStartDate = (String) info.getFilters().get("startDate");
         if (stringStartDate != null && stringStartDate.length() > 0)
         {
            startDate = df.parse(stringStartDate);
         }
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }
      try
      {
         String stringEndDate = (String) info.getFilters().get("endDate");
         if (stringEndDate != null && stringEndDate.length() > 0)
         {
            endDate = df.parse(stringEndDate);
         }
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }

      if (info.getSortingColumn() == null)
      {
         info.setPrimarySortColumn(getDefaultSortColumn());
         info.setPrimarySortDirection(getDefaultSortDirectionInteger());
      }

      if (info.getPagingNumberPer() == Integer.MAX_VALUE)
      {
         info.setPagingNumberPer(getDefaultNumberPerPage());
      }

      String folder = ((info.getFilters().get(FOLDER) != null) ? (String) info.getFilters().get(FOLDER) : defaultFolder);

      File dir = new File(folder);
      File[] files = dir.listFiles();
//      List list = new ArrayList(files.length);
      
      
//      ---------------------------
//      private boolean doPaging = true;
      int numberPerPage = info.getPagingNumberPer();

      if (numberPerPage == Integer.MAX_VALUE)
      {
         numberPerPage = getDefaultNumberPerPage();
         info.setPagingNumberPer(numberPerPage);
      }
      List list = new ArrayList(numberPerPage);
      
      int totalRows = files.length;
      int startPosition = 0;
      if (true)
      {
         info.setTotalNumberOfEntries(totalRows);

         if (numberPerPage == 0)
         {
            numberPerPage = getDefaultNumberPerPage();
         }

         int pageNumber = info.getPagingPage();
         if (pageNumber > 1)
         {
            if ((pageNumber - 1) * numberPerPage > totalRows)
            {
               pageNumber = ((totalRows - 1) / numberPerPage) + 1;
               info.setPagingPage(pageNumber);
            }
         }

         if (pageNumber > 1)
         {
        	 startPosition = ((pageNumber - 1) * numberPerPage);
         }
         else
         {
        	 startPosition = 0;
         }
      }
      int endPosition = (startPosition + numberPerPage >= totalRows) ? totalRows : (startPosition + numberPerPage);
      
//      ---------------------------
      for (int i = startPosition; i < endPosition; i++)
      {
         FileInfo file = new FileInfo(files[i]);
         if (startDate == null || startDate.before(file.getLastModified()))
         {
            if (endDate == null || endDate.after(file.getLastModified()))
            {
               list.add(file);
            }
         }

      }
      info.setTotalNumberOfEntries(totalRows);
      return new DefaultListBackedValueList(list, info);
   }

   /**
    * @param defaultFolder
    *           The defaultFolder to set.
    */
   public void setDefaultFolder(String defaultFolder)
   {
      this.defaultFolder = defaultFolder;
   }

}