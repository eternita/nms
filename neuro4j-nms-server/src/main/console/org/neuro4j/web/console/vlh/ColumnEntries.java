package org.neuro4j.web.console.vlh;

public class ColumnEntries {
	private String id1;
	private String id2;
	private String id3;
	private String id4;
	private Object obj1;
	private Object obj2;
	private Object obj3;
	private Object obj4;
	
	private EntryResolver resolver;
	private String language;
	
	public void setResolver(EntryResolver resolver, String language) {
		this.resolver = resolver;
		this.language = language;
	}

	public ColumnEntries(String id1, String id2) {
		super();
		this.id1 = id1;
		this.id2 = id2;
	}
	
	public ColumnEntries(String id1, String id2, String id3) {
		super();
		this.id1 = id1;
		this.id2 = id2;
		this.id3 = id3;
	}
	
	public ColumnEntries(String id1, String id2, String id3, String id4) {
		super();
		this.id1 = id1;
		this.id2 = id2;
		this.id3 = id3;
		this.id4 = id4;
	}
	
	public Object getA1() {
		if (null != obj1)
			return obj1;
		
		if (null != id1 && null != resolver && null != language)
		{
			obj1 = resolver.resolve(id1, language);
			return obj1;
		}
		return null;
	}

	public Object getA2() {
		if (null != obj2)
			return obj2;

		if (null != id2 && null != resolver && null != language)
		{
			obj2 = resolver.resolve(id2, language);
			return obj2;
		}
		return null;
	}	

	public Object getA3() {
		if (null != obj3)
			return obj3;

		if (null != id3 && null != resolver && null != language)
		{
			obj3 = resolver.resolve(id3, language);
			return obj3;
		}
		return null;
	}	

	public Object getA4() {
		if (null != obj4)
			return obj4;

		if (null != id4 && null != resolver && null != language)
		{
			obj4 = resolver.resolve(id4, language);
			return obj4;
		}
		return null;
	}	

	
	public int getObjectCount()
	{
		if (null != id4)
			return 4;
		if (null != id3)
			return 3;
		if (null != id2)
			return 2;
		return 1;
	}
	
}
