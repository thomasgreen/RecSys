package _Default;

import java.util.List;

import artistsPOJO.Attr_;


public abstract class TopItem<T> {
	//T represents an Item: either Artist or Track
	public abstract List<T> getItem();

	public abstract Attr_ getAttr();
}
