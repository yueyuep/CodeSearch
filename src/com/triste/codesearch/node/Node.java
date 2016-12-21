package com.triste.codesearch.node;

public abstract class Node {
	public enum Type{
		Project,Class,Method,Invoked_Method
	}
	
	public Type type;
	Long index;

	abstract public Long index();
	
	abstract public void setParseState(boolean b);
}
