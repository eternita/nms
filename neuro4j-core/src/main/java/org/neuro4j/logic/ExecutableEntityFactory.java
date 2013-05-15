package org.neuro4j.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neuro4j.utils.ClassUtils;



@SuppressWarnings("rawtypes")
public class ExecutableEntityFactory {

	
	private static Map<String, Class> entities = new HashMap<String, Class>();
	private static Map<String, String> shortNames = new HashMap<String, String>();
	
	private static final String preloadPackagesStr = "org.neuro4j";// TODO: read from property ("n4j.action_processor.preload_packages");
	static {
		// preload Bricks
		preloadBlocks();
	}
	
	private static void preloadBlocks()
	{
		try {
			String preloadPackages[] = preloadPackagesStr.split(" "); 
			List<Class> allClasses = new ArrayList<Class>();
			for (String pkg : preloadPackages)
			{
				allClasses.addAll(ClassUtils.getClasses(pkg));
			}
			for (Class clazz : allClasses)
			{
				if (ClassUtils.implementsInterface(clazz, ExecutableEntity.class))
				{
					String longName = clazz.getName(); 
					entities.put(longName, clazz);
					
					String shortName = longName.substring(longName.lastIndexOf('.') + 1);
					shortNames.put(shortName, longName);
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return;
	}
	
	
	public static ExecutableEntity getActionEntity(String name) throws ExecutableEntityNotFoundException
	{
		try {
			Class clazz = null;
			if (-1 == name.indexOf('.'))
			{
				// short name
//				String fullName = shortNames.get(name);
//				if (null != fullName)
				clazz = entities.get(shortNames.get(name));
			} else {
				clazz = entities.get(name);
			}
			
			if (null == clazz)
			{
				clazz = Class.forName(name);
				if (null != clazz)
					entities.put(name, clazz);
			}
			Object bObj = clazz.newInstance();
			if (bObj instanceof ExecutableEntity)
				return (ExecutableEntity) bObj;
				
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new ExecutableEntityNotFoundException("Block " + name + " not found");
	}
	
}
