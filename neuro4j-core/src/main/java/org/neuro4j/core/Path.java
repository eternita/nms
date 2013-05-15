package org.neuro4j.core;

import java.util.ArrayList;
import java.util.List;

public class Path {
	
	private List<String> items = new ArrayList<String>();

	public Path() {
		super();
	}

	public Path(String item) {
		this();
		add(item);
	}

	public Path(Path p) {
		this();
		for (String i : p.items)
			add(i);
	}

	public Path(Path p, String item) {
		this();
		for (String i : p.items)
			add(i);
		
		add(item);
	}

	public Path add(String item)
	{
		items.add(item);
		return this;
	}


	public List<String> getItems()
	{
		List<String> items = new ArrayList<String>();
		items.addAll(this.items);
		return items;
	}
	
	public int getSize()
	{
		return items.size();
	}

	@Override
	public String toString() {
		return "Path [items=" + items + "]";
	}

	public String getLast() {
		return items.get(items.size() - 1);
	}
	
	public String removeLast() {
		return items.remove(items.size() - 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	public boolean contains(String erid) {
		// TODO Auto-generated method stub
		return items.contains(erid);
	}
	
	public Path clone()
	{
		Path p = new Path(this);
		return p;
	}
	
}
