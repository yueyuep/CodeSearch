package com.triste.codesearch.node;

public class ClassNode extends Node{
	
	public enum ClassType{
		CLASS,ENUM,INTERFACE
	}
	
	public String className="";
	public String packageName="";
	public String path=" ";
	public ClassType classType=ClassType.CLASS;
	public boolean isParse=false;
	
	public ClassNode(){
		
	}
	
	public ClassNode(String n,String pn){
		this.className=n;
		this.packageName=pn;
		this.isParse=false;
		this.type=Type.Class;
		this.index=getIndex();
	}
	
	public ClassNode(String n,String pn,String p){
		this.className=n;
		this.packageName=pn;
		this.path=p;
		this.isParse=false;
		this.type=Type.Class;
		this.index=getIndex();
	}
	
	
	public Long index() {
		// TODO �Զ����ɵķ������
		return index;
	}
	
	private Long getIndex(){
		return (long)(packageName+className).hashCode();
	}
	
	public String getIndexString(){
		return packageName+className;
	}
	
	public String getClassInfo(){
		return packageName+"/"+className;
	}

	
	public void setParseState(boolean s) {
		// TODO �Զ����ɵķ������
		this.isParse=s;
		
	}
	
	
	public void setClassType(ClassType t){
		this.classType=t;
	}
	
	public String toString(){
		return path+"---"+packageName+"---"+className+" "+isParse;
	}

}
