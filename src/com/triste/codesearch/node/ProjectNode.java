package com.triste.codesearch.node;

public class ProjectNode extends Node{
	
	public String projectName="";
	public String path=" ";
	public boolean isParse=false;
	
	public ProjectNode() {
		// TODO 自动生成的构造函数存根
	}
	
	public ProjectNode(String name,String path){
		this.path=path;
		this.projectName=name;
		isParse=false;
		type=Type.Project;
		this.index=getIndex();
	}

	public Long index() {
		// TODO 自动生成的方法存根
		return index;
	}
	
	private Long getIndex(){
		return (long)(path+projectName).hashCode();
	}
	
	public String toString(){
		return path+"---"+projectName+" "+isParse;
	}

	public void setParseState(boolean s) {
		// TODO 自动生成的方法存根
		isParse=s;
		
	}

}
