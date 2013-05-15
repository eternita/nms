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
import java.util.Date;

/**
 * @author Matthew L. Wilson
 * @version $Revision: 1.3 $ $Date: 2004/08/10 20:41:38 $
 */
public class FileInfo
{
	/** The decorated File Object **/
   private File file;

   /** Default constructor
    * 
    * @param file to decorate.
    */
   public FileInfo(File file)
   {
      this.file = file;
   }

   /** @see java.io.File#lastModified()
    */
   public Date getLastModified()
   {
      return new Date(file.lastModified());
   }

   /** @see java.io.File#getName()
    */
   public String getName()
   {
      return file.getName();
   }

   /** Gets the extention of the file.
    * 
    * @return The extension if one exists.
    */
   public String getExtention()
   {
      return (getName().lastIndexOf('.') != -1) ? getName().substring(getName().lastIndexOf('.'), getName().length()) : "";
   }

   /**  @see java.io.File#getLength()
    */
   public long getLength()
   {
      return file.length();
   }

   /**  @see java.io.File#isDirectory()
    */
   public boolean getDirectory()
   {
      return file.isDirectory();
   }

   /**  @see java.io.File#canRead()
    */
   public boolean getReadable()
   {
      return file.canRead();
   }

   /** @see java.io.File#canWrite() */
   public boolean getWritable()
   {
      return file.canWrite();
   }

   /** @see java.io.File#getAbsolutePath() */
   public String getAbsolutePath()
   {
      return file.getAbsolutePath();
   }

   /** @see java.lang.Object#toString()
    */
   public String toString()
   {
      return file.getName() + " " + super.toString();
   }

}
