package org.neuro4j.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Utility class that can add jar files to the classpath dynamically.
 *
 */
public class ClassloaderUtil {

	/**
	 * add jar file to current classloader
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void addSoftwareLibrary(File file) throws Exception {
	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    // do not use system classloader - it doesn't see current classes
//	    method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
	    method.invoke(ClassloaderUtil.class.getClassLoader(), new Object[]{file.toURI().toURL()});
	    
	}
}
