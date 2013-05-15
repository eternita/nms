package org.neuro4j.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassUtils {
	 
//	private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

	@SuppressWarnings("rawtypes")
	public static boolean implementsInterface(Class checked, Class interf){
	    for (Class c : checked.getInterfaces()) {
	        if (c.equals(interf)) {
	            return true;
	        }
	    }
	    
	    if (null != checked.getSuperclass()) //  && !checked.getSuperclass().equals(Object.class)
	    {
	    	return implementsInterface(checked.getSuperclass(), interf);
	    }
	    
	    return false;
	}	

	@SuppressWarnings("rawtypes")
	public static boolean instnceOf(Class checked, Class superClass){
	    
		if (checked.equals(superClass))
		{
            return true;
		}

		if (null != checked.getSuperclass() && checked.getSuperclass().equals(superClass))
		{
            return true;
		}
	    
	    if (null != checked.getSuperclass())
	    {
	    	return instnceOf(checked.getSuperclass(), superClass);
	    }
	    
	    return false;
	}	
	
	public static Object deepCloneBySerialization(Object src)
	{
		try {
			long startTime = System.currentTimeMillis();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(src);

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject(); 
//			logger.debug("Spend time : " + (System.currentTimeMillis() - startTime) + " ms");
			return obj;
		} catch (IOException e) {
			e.printStackTrace();
//			logger.error("", e);
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
//			logger.error("", e);
			return null;
		}
		
	}
	
	
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
	public static List<Class> getClasses(String packageName)
            throws ClassNotFoundException, IOException 
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException 
	{
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
        	String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
            	classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
            	Class _class;
				try {
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
				} catch (ExceptionInInitializerError e) {
					// happen, for example, in classes, which depend on 
					// Spring to inject some beans, and which fail, 
					// if dependency is not fulfilled
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6),
							false, Thread.currentThread().getContextClassLoader());
				}
				classes.add(_class);
            }
        }
        return classes;
    }
}
