package com.triste.codesearch.database;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.kernel.impl.util.FileUtils;

import com.triste.codesearch.maps.Maps;
import com.triste.codesearch.node.ClassNode;
import com.triste.codesearch.node.MethodNode;
import com.triste.codesearch.node.ProjectNode;
import com.triste.codesearch.node.ClassNode.ClassType;
import com.triste.codesearch.node.Node.Type;
import com.triste.codesearch.parse.ASTMain;

public class Save {
	
	public enum Relationships implements RelationshipType{
		POINT,
		CONTAIN_CLASS,
		EXTENDS,IMPLEMENTS,NEST,
		CONTAIN_METHOD,INVOKE
	}
	
	private Map<Long,List<Long>> containClassMap;
	private Map<Long, List<Long>> containMethodMap;
	private Map<Long, List<Long>> beInvokedMap;
	private Map<Long, Long> extendsMap;
	private Map<Long, Long> implementsMap;
	private Map<Long, List<Long>> nestMap;
	//每put进一个新的node就要把含有该key的list创建出来
	private Map<Long, ProjectNode> projectNodeMap;
	private Map<Long, ClassNode> classNodeMap;
	private Map<Long, MethodNode> methodNodeMap;
	
	//public final static String DB_PATH="D:\\Software\\neo4j\\java";
	public final static String DB_PATH="D:\\Software\\neo4j\\Database";
	static public GraphDatabaseService database;
	
	static Index<Node> homeIndex;
	static Index<Node> projectIndex;
	static Index<Node> classIndex;
	static Index<Node> methodIndex;
	
	//database.index().forNodes(CLASS_NODE);
	public static final String PROJECT_NODE="projectNode";
	public static final String CLASS_NODE="classNode";
	public static final String METHOD_NODE="methodNode";
	
	//node.setProperty(CLASS_NAME, "class");
	public static final String PROJECT_NAME="projectName";
	public static final String CLASS_NAME="className";
	public static final String METHOD_NAME="methodName";
	
	//classIndex.add(node, CLASS_ID, i);
	public static final String PROJECT_ID="projectID";
	public static final String CLASS_ID="classID";
	public static final String METHOD_ID="methodID";
	
	public static final String PATH="path";
	public static final String INDEX="index";
	public static final String IS_PARSE="isParse";
	public static final String CLASS_TYPE="classType";
	public static final String RETURN_TYPE="returnType";
	public static final String PARAM="param";
	public static final String CLASS_INFO="classInfo";
	public static final String PACKAGE="package";
	
	
	
	Label projectLabel=DynamicLabel.label("projectLabel");
	Label classLabel=DynamicLabel.label("classLabel");
	Label methodLabel=DynamicLabel.label("methodLabel");
	
	public static Node home;

	public Save(){
		
	}
	
	public Save(Maps map){
		containClassMap=map.containClassMap;
		containMethodMap=map.containMethodMap;
		beInvokedMap=map.beInvokedMap;
		extendsMap=map.extendsMap;
		implementsMap=map.implementsMap;
		nestMap=map.nestMap;
		
		projectNodeMap=map.projectNodeMap;
		classNodeMap=map.classNodeMap;
		methodNodeMap=map.methodNodeMap;
		
		
	}
	
	public  void initDB(){
		database=(new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH));
		registerShutdownHook();
		
		try(Transaction tx=database.beginTx()){
			
			homeIndex=database.index().forNodes("homeNode");
			projectIndex=database.index().forNodes(PROJECT_NODE);
			classIndex=database.index().forNodes(CLASS_NODE);
			methodIndex=database.index().forNodes(METHOD_NODE);
			
			home=homeIndex.get("name", "home").getSingle();
			if(home==null){
				home=database.createNode();
				homeIndex.add(home, "name", "home");
				System.out.println("create home node success");
			}
			else{
				System.out.println("node home has existed");
			}
			
			tx.success();
		}
		/*try(Transaction tx=database.beginTx()){
			if(ASTMain.indexFlag){
				Schema schema = database.schema();
				IndexDefinition indexDefinition = schema.indexFor(projectLabel)
						.on(PROJECT_NAME).create();
			}
			//indexDefinition.
			tx.success();
		}
		try(Transaction tx=database.beginTx()){
			if(ASTMain.indexFlag){
			Schema schema = database.schema();
			IndexDefinition indexDefinition = schema.indexFor(classLabel)
					.on(CLASS_NAME).create();
			}
			tx.success();
		}
		try(Transaction tx=database.beginTx()){
			if(ASTMain.indexFlag){
			Schema schema = database.schema();
			IndexDefinition indexDefinition = schema.indexFor(methodLabel)
					.on(METHOD_NAME).create();
			}
			tx.success();
		}*/
		catch(Exception e){
			e.printStackTrace();
		}
		//ASTMain.indexFlag=false;
		
	}
	
	public void setMap(Maps map){
		containClassMap=map.containClassMap;
		containMethodMap=map.containMethodMap;
		beInvokedMap=map.beInvokedMap;
		extendsMap=map.extendsMap;
		implementsMap=map.implementsMap;
		nestMap=map.nestMap;
		
		projectNodeMap=map.projectNodeMap;
		classNodeMap=map.classNodeMap;
		methodNodeMap=map.methodNodeMap;
	}
	
	public void shutdown(){
		database.shutdown();
	}
	
	public void saveToDB(){
		saveProjectNodeMap();
		saveClassNodeMap();
		saveMethodNodeMap();
		saveContainClassMap();
		saveContainMethodMap();
		saveImplementsMap();
		saveExtendsMap();
		saveNestMap();
		saveBeInvokedMap();
		
		//database.shutdown();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////
	public void saveProjectNodeMap(){
		System.out.println("----------------------saving ProjectNodeMap--------------------------");
		Iterator<Long> it=projectNodeMap.keySet().iterator();
		while(it.hasNext()){
			Long key=it.next();
			ProjectNode pj=projectNodeMap.get(key);
			saveNode(pj);
		}
		
		System.out.println("*************************save ProjectNodeMap success*************************");
	}
	
	public void saveClassNodeMap(){
		System.out.println("----------------------saving ClassNodeMap----------------------------");
		Iterator<Long> it=classNodeMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			ClassNode classNode=classNodeMap.get(key); 
			saveNode(classNode);
		    //System.out.println(classNode.toString());   
		} 
		System.out.println("**************************save ClassNodeMap success*************************");
	}
	
	public void saveMethodNodeMap(){
		System.out.println("----------------------saving MethodNodeMap---------------------------");
		Iterator<Long> it=methodNodeMap.keySet().iterator();    
		while(it.hasNext()){  
			Long key=it.next();
			MethodNode methodNode=methodNodeMap.get(key); 
			saveNode(methodNode);
		    //System.out.println(methodNode.toString());   
		} 
		System.out.println("**************************save MethodNodeMap success***************************");
	}
	
	public void saveContainClassMap(){
		System.out.println("----------------------saving ContainClassMap----------------------------");
		Iterator<Long> it=projectNodeMap.keySet().iterator();
		Long key = null;
		try(Transaction tx=database.beginTx()){
			while(it.hasNext()){
				key=it.next();
				//ProjectNode pj=projectNodeMap.get(key);
				//System.out.println(pj.toString());
				//System.out.println(lastRecentProjectNode.toString());
				Node projectNode=projectIndex.get(PROJECT_ID, key).getSingle();
				List<Long> list=containClassMap.get(key);
				for(int i=0;i<list.size();++i){
					Long k =list.get(i);
					Node classNode=classIndex.get(CLASS_ID, k).getSingle();
					projectNode.createRelationshipTo(classNode, Relationships.CONTAIN_CLASS);
					//System.out.println(classNodeMap.get(k).toString());
				}
			}
			tx.success();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("projectNode:【"+classNodeMap.get(key)+"】:"+key); 
		}
		System.out.println("*************************save ContainClassMap success**********************");
	}
	
	public void saveContainMethodMap(){
		System.out.println("----------------------saving ContainMethodMap----------------------------");
		Long key = null;
		Iterator<Long> it=containMethodMap.keySet().iterator();  
		try(Transaction tx=database.beginTx()){
			while(it.hasNext()){  
				key=it.next();
				List<Long> list=containMethodMap.get(key); 
			    //System.out.println("【"+classNodeMap.get(key)+"】:"+key); 
				Node classNode=classIndex.get(CLASS_ID, key).getSingle();
			    for(int i=0;i<list.size();++i){
			    	Long k=list.get(i);
			    	Node methodNode=methodIndex.get(METHOD_ID, k).getSingle();
			    	classNode.createRelationshipTo(methodNode, Relationships.CONTAIN_METHOD);
			    	//System.out.println(methodNodeMap.get(k).toString());
			    }
			}
			tx.success();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("classNode:【"+classNodeMap.get(key)+"】:"+key); 
		}
		System.out.println("*************************save ContainMethodMap success************************");
	}
	
	//
	public void saveImplementsMap(){
		System.out.println("----------------------saving ImplementsMap----------------------------");
		Long key=null;
		Long key2=null;
		Iterator<Long> it=implementsMap.keySet().iterator();  
		try(Transaction tx=database.beginTx()){
			while(it.hasNext()){  
			    key=it.next();
				Node classNode1=classIndex.get(CLASS_ID, key).getSingle();
				key2=implementsMap.get(key);
				Node classNode2=classIndex.get(CLASS_ID, key2).getSingle();
				classNode1.createRelationshipTo(classNode2, Relationships.IMPLEMENTS);
				//System.out.println("key2:"+key2);
				//System.out.println(classNodeMap.get(key).toString()+" 【implements】 "+classNodeMap.get(key2));
			}
			tx.success();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(classNodeMap.get(key).toString()+" 【implements】 "+classNodeMap.get(key2));
		}
		System.out.println("**********************save ImplementsMap success***************************");
	}
	
	//子类 EXTENDS ---->父类
	public void saveExtendsMap(){
		System.out.println("----------------------saving ExtendssMap----------------------------");
		Long key=null;
		Long key2=null;
		Iterator<Long> it=extendsMap.keySet().iterator();  
		try(Transaction tx=database.beginTx()){
			while(it.hasNext()){  
				key=it.next();
				key2=extendsMap.get(key);
				Node classNode1=classIndex.get(CLASS_ID, key).getSingle();
				Node classNode2=classIndex.get(CLASS_ID, key2).getSingle();
				classNode1.createRelationshipTo(classNode2, Relationships.EXTENDS);
				//System.out.println(classNodeMap.get(key).toString()+" 【extends】 "+classNodeMap.get(key2));
			}
			tx.success();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(classNodeMap.get(key).toString()+" 【extends】 "+classNodeMap.get(key2));
		}
		System.out.println("**********************save ExtendssMap success***************************");
	}
	
	public void saveNestMap(){
		System.out.println("----------------------saving NestMap----------------------------");
		Long key=null;
		Long key2=null;
		Iterator<Long> it=nestMap.keySet().iterator();   
		try(Transaction tx=database.beginTx()){
			while(it.hasNext()){  
			    key=it.next();
				List<Long> list=nestMap.get(key);
				Node classNode1=classIndex.get(CLASS_ID, key).getSingle();
				for(int i=0;i<list.size();++i){
					key2=list.get(i);
					Node classNode2=classIndex.get(CLASS_ID, key2).getSingle();
					classNode1.createRelationshipTo(classNode2, Relationships.NEST);
					//ClassNode classNode=classNodeMap.get(key); 
					//ClassNode classNode2=classNodeMap.get(key2); 
				   // System.out.println(classNode.toString()+" 【nest】 "+classNode2.toString());  
				} 
			}
			tx.success();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(classNodeMap.get(key).toString()+" 【nest】 "+classNodeMap.get(key2).toString());  

		}
		System.out.println("**********************save NestMap success***********************");
	}
	
	//调用他的方法 INVOKE-----> util方法
	public void saveBeInvokedMap(){
		System.out.println("----------------------saving BeInvokedMap----------------------------");
		Long key=null;
		Long k=null;
		Iterator<Long> it=beInvokedMap.keySet().iterator(); 
		try(Transaction tx=database.beginTx()){
			while(it.hasNext()){  
				key=it.next();
				List<Long> list=beInvokedMap.get(key); 
				Node methodNode1=methodIndex.get(METHOD_ID, key).getSingle();
			   // System.out.println("【"+methodNodeMap.get(key)+"】:"); 
			    for(int i=0;i<list.size();++i){
			    	k=list.get(i);
			    	Node methodNode2=methodIndex.get(METHOD_ID, k).getSingle();
			    	methodNode2.createRelationshipTo(methodNode1, Relationships.INVOKE);
			    	//System.out.println(methodNodeMap.get(k).toString());
			    }
			}
			tx.success();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("【"+methodNodeMap.get(key)+"】:"+methodNodeMap.get(k).toString()); 
		}
		System.out.println("*******************save BeInvokedMap success*************************");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////工具函数
	
	public void saveNode(com.triste.codesearch.node.Node myNode){ 
		if(myNode.type==Type.Project)
			saveProjectNode((ProjectNode)myNode);
		else if(myNode.type==Type.Class)
			saveClassNode((ClassNode)myNode);
		else if(myNode.type==Type.Method)
			saveMethodNode((MethodNode)myNode);

	}
	
	public void saveProjectNode(ProjectNode projectNode){
		
		 try(Transaction tx=database.beginTx()){
			 Node node;
			 node=projectIndex.get(PROJECT_ID, projectNode.index()).getSingle();
			 if(node==null){
				node=database.createNode();
				node.addLabel(projectLabel);
				node.setProperty(PROJECT_NAME,projectNode.projectName);
				if(projectNode.path==(null))
					node.setProperty(PATH, "");
				else 
					node.setProperty(PATH, projectNode.path);
				node.setProperty(IS_PARSE, projectNode.isParse);
				
				projectIndex.add(node, PROJECT_ID, projectNode.index());
				home.createRelationshipTo(node,Relationships.POINT);
			 }
				
				tx.success();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(projectNode);
			}
			
	}
	
	public void saveClassNode(ClassNode classNode){
		Node node;
		try(Transaction tx=database.beginTx()){
			node=classIndex.get(CLASS_ID, classNode.index()).getSingle();
			if(node==null){
			    node=database.createNode();
				node.addLabel(classLabel);
				node.setProperty("type","class");
				node.setProperty(CLASS_NAME,classNode.className);
				node.setProperty(PACKAGE, classNode.packageName);
				if(classNode.path==(null))
					node.setProperty(PATH, "");
				else
					node.setProperty(PATH, classNode.path);
				node.setProperty(IS_PARSE, classNode.isParse);
				if(classNode.classType==ClassType.CLASS)
					node.setProperty(CLASS_TYPE,"CLASS");
				else if(classNode.classType==ClassType.INTERFACE)
					node.setProperty(CLASS_TYPE,"INTERFACE");
				if(classNode.classType==ClassType.ENUM)
					node.setProperty(CLASS_TYPE,"ENUM");
				
				classIndex.add(node, CLASS_ID, classNode.index());
			}
				
				tx.success();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(classNode);
			}
	}
	
	public void saveMethodNode(MethodNode methodNode){
		Node node;
		try(Transaction tx=database.beginTx()){

			node=methodIndex.get(METHOD_ID, methodNode.index()).getSingle();
			if(node==null){
			    node=database.createNode();
				node.addLabel(methodLabel);
				node.setProperty("type","method");
				node.setProperty(METHOD_NAME,methodNode.methodName);
				node.setProperty(RETURN_TYPE,methodNode.returnType);
				node.setProperty(IS_PARSE, methodNode.isParse);
				node.setProperty(PARAM, methodNode.param);
				node.setProperty(CLASS_INFO, methodNode.classInfo);
				
				methodIndex.add(node, METHOD_ID, methodNode.index());
			}
				tx.success();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(methodNode);
			}
	}
	
	
	/*public void addRelationship(node.Node node1,node.Node node2){
		
	}*/
	

	
	private static void registerShutdownHook() {
		/*这个方法的意思就是在jvm中增加一个关闭的钩子，当jvm关闭的时候，
		会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，
		当系统执行完这些钩子后，jvm才会关闭。
		所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作。 */
        Runtime.getRuntime()
                .addShutdownHook( new Thread()
                {
                    @Override
                    public void run()
                    {
                        database.shutdown();
                    }
                } );
    }
	
	private static void clearDB() {  
        try {  
        	//删除数据库
            FileUtils.deleteRecursively(new File(DB_PATH));  
        }  
        catch(IOException e) {  
            throw new RuntimeException(e);  
        }  
    }
	
	
	public static void main(String[] args) {
		Save save=new Save();
		save.initDB();
	}
	
	
	
	
	
	
	
}
