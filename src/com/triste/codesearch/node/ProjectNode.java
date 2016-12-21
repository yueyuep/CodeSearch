package com.triste.codesearch.node;

public class ProjectNode extends Node{
	
	public String projectName="";
	public String path=" ";
	public boolean isParse=false;
	
	public ProjectNode() {
		// TODO �Զ����ɵĹ��캯�����
	}
	
	public ProjectNode(String name,String path){
		this.path=path;
		this.projectName=name;
		isParse=false;
		type=Type.Project;
		this.index=getIndex();
	}

	public Long index() {
		// TODO �Զ����ɵķ������
		return index;
	}
	
	private Long getIndex(){
		return (long)(path+projectName).hashCode();
	}
	
	public String toString(){
		return path+"---"+projectName+" "+isParse;
	}

	public void setParseState(boolean s) {
		// TODO �Զ����ɵķ������
		isParse=s;
		
	}

}
