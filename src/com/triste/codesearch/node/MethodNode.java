package com.triste.codesearch.node;

public class MethodNode extends Node {
	
	public String methodName="";
	String stringOfClass="";
	public String returnType="";
	public String param="";
	public boolean isParse=false;
	public String classInfo="";
	
	public MethodNode() {
		// TODO �Զ����ɵĹ��캯�����
	}
	
	public MethodNode(String name,ClassNode cn,String returnType,String param){
		this.methodName=name;
		this.stringOfClass=cn.getIndexString();
		this.isParse=false;
		this.returnType=returnType;
		this.param=param;
		this.type=Type.Method;
		this.index=getIndex();
		this.classInfo=cn.getClassInfo();
	}
	


	public Long index() {
		// TODO �Զ����ɵķ������
		return index;
	}
	
	private Long getIndex(){
		String s=stringOfClass+methodName;
		return (long)s.hashCode();
	}
	
	public String toString(){
		String s=stringOfClass+"---"+returnType+"---"+methodName+"("+param+")"+" "+isParse;
		return s;
	}


	public void setParseState(boolean s) {
		// TODO �Զ����ɵķ������
		this.isParse=s;
		
	}
	

}
