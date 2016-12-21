package com.triste.codesearch.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;

import com.triste.codesearch.node.ClassNode;
import com.triste.codesearch.node.MethodNode;
import com.triste.codesearch.node.ProjectNode;
import com.triste.codesearch.node.ClassNode.ClassType;
import com.triste.codesearch.node.Node.Type;



public class Maps {	
	
	
	public static String currentProjectPath;
	public static String currentFilePath;

	

	
	String path="";
	String fileName="";
	
	
	public Map<Long,List<Long>> containClassMap;
	public Map<Long, List<Long>> containMethodMap;
	public Map<Long, List<Long>> beInvokedMap;
	public Map<Long, Long> extendsMap;
	public Map<Long, Long> implementsMap;
	public Map<Long, List<Long>> nestMap;

	
	//每put进一个新的node就要把含有该key的list创建出来
	public Map<Long, ProjectNode> projectNodeMap;
	public Map<Long, ClassNode> classNodeMap;
	public Map<Long, MethodNode> methodNodeMap;
	
	private ProjectNode lastRecentProjectNode;
	private ClassNode lastRecentClassNode;//判断嵌套类的
	private ClassNode lastRecentOfAllClassNode;//判断最近一个类 包括嵌套类非嵌套类 即当前方法是否在methodNode里
	private MethodNode lastRecentMethodNode;

	public Maps(){
		containClassMap=new HashMap<Long, List<Long>>();
		containMethodMap=new HashMap<Long, List<Long>>();
		beInvokedMap=new HashMap<Long, List<Long>>();
		extendsMap=new HashMap<Long, Long>();
		implementsMap=new HashMap<Long, Long>();
		nestMap=new HashMap<Long, List<Long>>();
		
		projectNodeMap=new HashMap<Long, ProjectNode>();
		classNodeMap=new HashMap<Long, ClassNode>();
		methodNodeMap=new HashMap<Long, MethodNode>();
	}
	
	Maps(String path,String filename){
		this.path=path;
		this.fileName=filename;
	}
	
	public ProjectNode getProjectNode(){
		return lastRecentProjectNode;
	}
	
	public void setClassNode(ClassNode node){
		lastRecentClassNode=node;
	}
	
	public ClassNode getClassNode(){
		return lastRecentClassNode;
	}
	
	public void setLastAllClassNode(ClassNode node){
		lastRecentOfAllClassNode=node;
	}
	
	public ClassNode getLastAllClassNode(){
		return lastRecentOfAllClassNode;
	}
	
	public void setMethodNode(MethodNode node){
		lastRecentMethodNode=node;
	}
	
	public MethodNode getMethodNode(){
		return lastRecentMethodNode;
	}
	
	public boolean containMethodNode(MethodNode node){
		return methodNodeMap.containsKey(node.index());
	}
	
	
	public void addProject(ProjectNode node){
		projectNodeMap.put(node.index(), node);
		
		lastRecentProjectNode=node;
		List<Long> list=new ArrayList<Long>();
		containClassMap.put(node.index(), list);
	}
	
	public void addNode(com.triste.codesearch.node.Node node1,com.triste.codesearch.node.Node node2){
		if(node1.type==Type.Project){
			addProject_Class((ProjectNode)node1, (ClassNode)node2);
		}
		else if(node1.type==Type.Class){
			if(node2.type==Type.Class)
				addClass_Class((ClassNode)node1, (ClassNode)node2);
			else if(node2.type==Type.Method)
				addClass_Method((ClassNode)node1, (MethodNode)node2);
		
		}
		else if(node1.type==Type.Method){
			addMethod_InvokedMethod((MethodNode)node1, (MethodNode)node2);
		}
	}
	
	public void addNestClassNode(ClassNode node1,ClassNode node2){
		/*if(!isExistNode(node2))
			classNodeMap.put(node2.index(), node2);*/
		List<Long> list=nestMap.get(node1.index());
		if(list==null){
			list=new ArrayList<Long>();
			list.add(node2.index());
			nestMap.put(node1.index(), list);
		}
		else
		list.add(node2.index());
		
		
	}
	
	
	public void addProject_Class(ProjectNode projectNode,ClassNode classNode){
		//if(!isExistNode(classNode)){
			classNodeMap.put(classNode.index(), classNode);
			List<Long> list=new ArrayList<Long>();
			containMethodMap.put(classNode.index(), list);
		//}
	
		List<Long> list1=containClassMap.get(projectNode.index());
		list1.add(classNode.index());
		//System.out.println(containClassMap);
		//containClassMap.put(projectNode.index(), list1);
		//System.out.println("add class into containClassMap: "+classNode.toString());
	}
	
	public void addClass_Class(ClassNode classNode1,ClassNode classNode2){
		if(!isExistNode(classNode2))
			classNodeMap.put(classNode2.index(), classNode2);
		
		if(classNode2.classType==ClassType.INTERFACE){
			
			if(classNode1.classType==ClassType.INTERFACE)
				extendsMap.put(classNode1.index(), classNode2.index());
			else {
				implementsMap.put(classNode1.index(), classNode2.index());
				//System.out.println("classNode2:"+classNode2.index());
			}
		}
		else if(classNode2.classType==ClassType.CLASS){
			extendsMap.put(classNode1.index(), classNode2.index());
		}
	}
	
	public void addClass_Method(ClassNode classNode,MethodNode methodNode){
		//if(!isExistNode(methodNode)){
			methodNodeMap.put(methodNode.index(), methodNode);
			List<Long>list =new ArrayList<Long>();
			beInvokedMap.put(methodNode.index(), list);
		//}
		
		List<Long>list1=containMethodMap.get(classNode.index());
		list1.add(methodNode.index());
	}
	
	public void addMethod_InvokedMethod(MethodNode methodNode,MethodNode invokedMethodNode){
		if(!isExistNode(invokedMethodNode)){
			methodNodeMap.put(invokedMethodNode.index(), invokedMethodNode);
			List<Long>list=beInvokedMap.get(methodNode.index());
			list.add(invokedMethodNode.index());
			
		}
		//如果有这个method 那么看看methodnode是否跟它已经建立关系
		else{
			List<Long>list=beInvokedMap.get(methodNode.index());
			if(!list.contains(invokedMethodNode.index()))
				list.add(invokedMethodNode.index());
		}
	}
	
	public boolean isExistNode(com.triste.codesearch.node.Node node){
		if(node.type==Type.Class){
			return classNodeMap.containsKey(((ClassNode)node).index());
		}
		else if(node.type==Type.Method||node.type==Type.Invoked_Method){
			return classNodeMap.containsKey(((MethodNode)node).index());
		}
		
		return false;
	}
	
	public void clear(){
		
	}
	
	public void displayMap(){
		displayProjectNodeMap();
		displayClassNodeMap();
		displayMethodNodeMap();
		displayContainClassMap();
		displayContainMethodMap();
		displayBeInvokedMap();
		displayImplementsMap();
		displayExtendsMap();
		displayNestMap();
		
	}
	
	public void displayProjectNodeMap(){
		System.out.println("----------------------ProjectNodeMap--------------------------");
		Iterator<Long> it=projectNodeMap.keySet().iterator();
		while(it.hasNext()){
			Long key=it.next();
			ProjectNode pj=projectNodeMap.get(key);
			System.out.println(pj.toString());
		}
	}
	
	
	public void displayClassNodeMap(){
		System.out.println("----------------------ClassNodeMap----------------------------");
		Iterator<Long> it=classNodeMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			ClassNode classNode=classNodeMap.get(key); 
		    System.out.println(classNode.toString());   
		} 
	}
	
	public void displayMethodNodeMap(){
		System.out.println("----------------------MethodNodeMap---------------------------");
		Iterator<Long> it=methodNodeMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			MethodNode methodNode=methodNodeMap.get(key); 
		    System.out.println(methodNode.toString());   
		} 
	}
	
	public void displayNestMap(){
		System.out.println("----------------------NestMap----------------------------");
		Iterator<Long> it=nestMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			List<Long> list=nestMap.get(key);
			for(int i=0;i<list.size();++i){
				Long key2=list.get(i);
				ClassNode classNode=classNodeMap.get(key); 
				ClassNode classNode2=classNodeMap.get(key2); 
			    System.out.println(classNode.toString()+" 【nest】 "+classNode2.toString());  
			} 
		} 
	}

	
	public void displayContainClassMap(){
		System.out.println("----------------------ContainClassMap----------------------------");
		Iterator<Long> it=projectNodeMap.keySet().iterator();
		while(it.hasNext()){
			Long key=it.next();
			ProjectNode pj=projectNodeMap.get(key);
			System.out.println(pj.toString());
			//System.out.println(lastRecentProjectNode.toString());
			List<Long> list=containClassMap.get(pj.index());
			for(int i=0;i<list.size();++i){
				Long k =list.get(i);
				System.out.println(classNodeMap.get(k).toString());
			}
		}
		//System.out.println(containClassMap);
	
	}
	
	public void displayContainMethodMap(){
		System.out.println("----------------------ContainMethodMap----------------------------");
		Iterator<Long> it=containMethodMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			List<Long> list=containMethodMap.get(key); 
		    System.out.println("【"+classNodeMap.get(key)+"】:"+key); 
		    for(int i=0;i<list.size();++i){
		    	Long k=list.get(i);
		    	System.out.println(methodNodeMap.get(k).toString());
		    }
		} 
	}
	
	public void displayBeInvokedMap(){
		System.out.println("----------------------BeInvokedMap----------------------------");
		Iterator<Long> it=beInvokedMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			List<Long> list=beInvokedMap.get(key); 
		    System.out.println("【"+methodNodeMap.get(key)+"】:"); 
		    for(int i=0;i<list.size();++i){
		    	Long k=list.get(i);
		    	System.out.println(methodNodeMap.get(k).toString());
		    }
		} 
	}
	
	public void displayImplementsMap(){
		System.out.println("----------------------ImplementsMap----------------------------");
		Iterator<Long> it=implementsMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			Long key2=implementsMap.get(key);
			//System.out.println("key2:"+key2);
			System.out.println(classNodeMap.get(key).toString()+" 【implements】 "+classNodeMap.get(key2));
		}
		//System.out.println(classNodeMap);
	}
	
	public void displayExtendsMap(){
		System.out.println("----------------------ExtendsMap----------------------------");
		Iterator<Long> it=extendsMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			Long key2=extendsMap.get(key);
			System.out.println(classNodeMap.get(key).toString()+" 【extends】 "+classNodeMap.get(key2));
		}
	}
	

	
}
