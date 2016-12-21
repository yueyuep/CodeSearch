package com.triste.codesearch.pagerank;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

import com.triste.codesearch.database.Save;

public class Database {

	public enum Relationships implements RelationshipType{
		POINT,
		CONTAIN_CLASS,
		EXTENDS,IMPLEMENTS,NEST,
		CONTAIN_METHOD,INVOKE
	}
	
	private final static String DB_PATH= Save.DB_PATH;
	static public GraphDatabaseService database;
	
	public static Index<Node> homeIndex;
	public static Index<Node> projectIndex;
	public static Index<Node> classIndex;
	public static Index<Node> methodIndex;
	
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
	
	Label projectLabel=DynamicLabel.label("projectLabel");
	Label classLabel=DynamicLabel.label("classLabel");
	Label methodLabel=DynamicLabel.label("methodLabel");
	
	public static final String PACKAGE="package";
	
	public static final String PAGERANK="pageRank";
	
	public static Node home;
	
	
	
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
				/*home=database.createNode();
				homeIndex.add(home, "name", "home");*/
				System.out.println("该数据库为空！");
			}
			else{
				System.out.println("数据库可以进行计算pagerank值.");
			}
			
			tx.success();
		}

		catch(Exception e){
			e.printStackTrace();
		}
		//ASTMain.indexFlag=false;
		
	}
	
	public void shutdown(){
		database.shutdown();
	}
	
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
}
