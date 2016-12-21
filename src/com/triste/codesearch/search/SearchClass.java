package com.triste.codesearch.search;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

import com.triste.codesearch.node.ClassNode;
import com.triste.codesearch.pagerank.Database;


public class SearchClass extends Database{
	
	public SearchClass(){
		this.initDB();
	}
	
	public String search(long id){
		
		ArrayList<Node> list = searchByID(id);
		JSONObject obj = new JSONObject();
		ArrayList<String> objList = new ArrayList<String>();
		for(int i=0;i<list.size();++i){
			objList.add(getOneClassString(list.get(i)));
		}
		JSONArray arr = new JSONArray((Collection)objList);
/*		try {
			obj.put("list", arr);
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
		
		return arr.toString();
		
	}
	
	public String search(String className){
		
		ArrayList<Node> list = searchByName(className);
		JSONObject obj = new JSONObject();
		ArrayList<String> objList = new ArrayList<String>();
		for(int i=0;i<list.size();++i){
			objList.add(getOneClassString(list.get(i)));
		}
		JSONArray arr = new JSONArray((Collection)objList);
/*		try {
			obj.put("list", arr);
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
		
		return arr.toString();
		
	}
	
	
	public String getOneClassString(Node node){
		try {
			JSONObject obj = new JSONObject();
			try (Transaction tx = database.beginTx()){	
				ClassNode cn = new ClassNode((String)node.getProperty(CLASS_NAME),(String)node.getProperty(PACKAGE));
				
				obj.put("id", cn.index());
				
				obj.put("type", "class");
				double d = -1;
				if(node.hasProperty(PAGERANK))
					d = (double)node.getProperty(PAGERANK);
				obj.put("pagerank", d);
				

				obj.put("package_name",node.getProperty(PACKAGE));
				obj.put("class_name", (String)node.getProperty(CLASS_NAME));
				obj.put("class_type",(String)node.getProperty(CLASS_TYPE));
				obj.put("parse_state", (boolean)node.getProperty(IS_PARSE));
				obj.put("file_location",(String) node.getProperty(PATH));
				tx.success();
		}
			
			obj.put("implement", getListInfo(searchImplementClass(node)));
			obj.put("extend", getListInfo(searchExtendClass(node)));
			obj.put("implemented", getListInfo(searchImplementedClass(node)));
			obj.put("extended", getListInfo(searchExtendedClass(node)));
			
			return obj.toString();
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		
		return "";
	}
	
	public void setPackage(){
		Label label = DynamicLabel.label("classLabel");
		try (Transaction tx = Database.database.beginTx()) {
			for (Node node : Database.database.findNodesByLabelAndProperty(
					label, "type", "class")) {
				
				
				String s="";
				for(Relationship re:node.getRelationships(Relationships.CONTAIN_METHOD,Direction.OUTGOING)){
					s = (String)re.getEndNode().getProperty(CLASS_INFO);
					break;
				}
				
				String p = "";
				if(!s.equals("")){
					String []a = s.split("/");
					p = a[0];
				}
				
				node.setProperty(PACKAGE, p);
				
			}
			tx.success();
		}
	}
	
	public String getListInfo(ArrayList<Node> list){
		
		try {
			try (Transaction tx = database.beginTx()){
				if(list==null)
					return "";
				ArrayList<JSONObject> objList = new ArrayList<JSONObject>();
				for(int i=0;i<list.size();++i){
					JSONObject obj = new JSONObject();
					
					Node n = list.get(i);
					ClassNode cn = new ClassNode((String)n.getProperty(CLASS_NAME),(String)n.getProperty(PACKAGE));
					
					obj.put("id",cn.index());
					obj.put("name", (String)n.getProperty(CLASS_NAME));
					obj.put("class_type", (String)n.getProperty(CLASS_TYPE));
					obj.put("path", (String)n.getProperty(PATH));
					objList.add(obj);
				}
				
				JSONArray arr = new JSONArray((Collection)objList);
				
				tx.success();
				return arr.toString();
			}
			
			
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return  "";
	}
	

	public ArrayList<Node> searchByName(String className) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		Label label = DynamicLabel.label("classLabel");
		try (Transaction tx = database.beginTx()) {
			for (Node node : database.findNodesByLabelAndProperty(label,
					CLASS_NAME, className)) {
				resultNodes.add(node);
			}
			tx.success();

		}
		sortH2L(resultNodes);
		return resultNodes;

	}
	
	public ArrayList<Node> searchByID(long id) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		Label label = DynamicLabel.label("classLabel");
		try (Transaction tx = database.beginTx()) {
			Node node = classIndex.get(CLASS_ID, id).getSingle();
			if(node!=null){
				resultNodes.add(node);
				//System.out.println("isnt null");
			}
				
			tx.success();
		}
		return resultNodes;

	}
	
	
	public void sortH2L(ArrayList<Node> list){//pagerank从大到小排
		for(int i=0;i<list.size()-1;++i){
			for(int j=0;j<list.size()-1-i;++j){
				if(!pagerankA_bigthan_B(list.get(i), list.get(i+1))){
					Node n = list.get(i);
					list.set(i, list.get(i+1));
					list.set(i+1, n);
				}
					
			}
		}
	}
	
	public boolean pagerankA_bigthan_B(Node a,Node b){
		try (Transaction tx = database.beginTx()) {
			if((double)a.getProperty(PAGERANK)>(double)b.getProperty(PAGERANK)){
				tx.success();
				return true;
			}
			tx.success();
			return false;
			
		}
		
	}

	// 验证函数是否被解析
	public boolean validateParseState(Node node) {
		try (Transaction tx = database.beginTx()) {
			if (node.getProperty(IS_PARSE).equals(true)) {
				tx.success();
				return true;
			} else {
				tx.success();
				return false;
			}
		}

	}

	//find the father classes
	public ArrayList<Node> searchExtendClass(Node classNode) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		// wait to be check null or ""

		try {
			try (Transaction tx = database.beginTx()) {
				classNode.getProperty("className");
				// TraversalDescription td=
				// database.traversalDescription().relationships(RelTypes.USE,
				// Direction.INCOMING);
				// for(Path path: td.traverse(classNode)){
				// resultNodes.add(path.startNode());
				// }
				for (Relationship iter : classNode.getRelationships(
						Relationships.EXTENDS, Direction.OUTGOING)) {
					resultNodes.add(iter.getEndNode());
				}
				tx.success();
			}
			
			sortH2L(resultNodes);
			return resultNodes;
			
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}
	
	
	//find the father classes
	public ArrayList<Node> searchImplementClass(Node classNode) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		// wait to be check null or ""

		try {
			try (Transaction tx = database.beginTx()) {
				classNode.getProperty("className");
				// TraversalDescription td=
				// database.traversalDescription().relationships(RelTypes.USE,
				// Direction.INCOMING);
				// for(Path path: td.traverse(classNode)){
				// resultNodes.add(path.startNode());
				// }
				for (Relationship iter : classNode.getRelationships(
						Relationships.IMPLEMENTS, Direction.OUTGOING)) {
					resultNodes.add(iter.getEndNode());
				}
				tx.success();
			}
			
			sortH2L(resultNodes);
			
			return resultNodes;
			
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}

	//find the son classes
	public ArrayList<Node> searchExtendedClass(Node classNode) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		try {
			try (Transaction tx = database.beginTx()) {
				classNode.getProperty("className");
				// TraversalDescription td=
				// database.traversalDescription().relationships(RelTypes.INVOKE,
				// Direction.OUTGOING);
				// for(Path path: td.traverse(classNode)){
				// resultNodes.add(path.endNode());
				// }
				for (Relationship iter : classNode.getRelationships(
						Relationships.EXTENDS, Direction.INCOMING)) {
					resultNodes.add(iter.getStartNode());
				}
				tx.success();
			}
			
			sortH2L(resultNodes);
			
			return resultNodes;
			
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}
	
	//find the son classes
	public ArrayList<Node> searchImplementedClass(Node classNode) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		try {
			try (Transaction tx = database.beginTx()) {
				classNode.getProperty("className");
				// TraversalDescription td=
				// database.traversalDescription().relationships(RelTypes.INVOKE,
				// Direction.OUTGOING);
				// for(Path path: td.traverse(classNode)){
				// resultNodes.add(path.endNode());
				// }
				for (Relationship iter : classNode.getRelationships(
						Relationships.IMPLEMENTS, Direction.INCOMING)) {
					resultNodes.add(iter.getStartNode());
				}
				tx.success();
			}
			
			sortH2L(resultNodes);
			return resultNodes;
			
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}

/*	public String getFileLocation(Node classNode) {
		try {
			try (Transaction tx = database.beginTx()) {
				classNode.getProperty("className");
				Node proNode = classNode.getSingleRelationship(
						RelTypes.CONTAIN, Direction.INCOMING).getStartNode();
				String packageName = (String) classNode
						.getProperty("packageName");
				packageName = packageName.replace('.', '\\');
				System.out.println(packageName);
				String fileLocation = (String) proNode
						.getProperty("fileLocation");
				String classLocation = fileLocation + '\\' + "src" + '\\'
						+ packageName + '\\'
						+ (String) classNode.getProperty("className") + ".java";
				return classLocation;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}*/

/*	// waiting to be complete to find the sourceCode
	public void getExtendSource(Node classNode) {
		String output = new String();
		try (Transaction tx = database.beginTx()) {
			Traverser extendsTraverser = getExtendSourceTraverser(classNode);
			for (Path friendPath : extendsTraverser) {
				output += "At depth " + friendPath.length() + " => "
						+ friendPath.endNode().getProperty("className") + "\n";
			}
			System.out.println(output);
			tx.success();
		}
	}

	private Traverser getExtendSourceTraverser(final Node classNode) {
		TraversalDescription td = database.traversalDescription().depthFirst()
				.relationships(RelTypes.EXTEND, Direction.OUTGOING);

		return td.traverse(classNode);

	}

	public ArrayList<Node> getImplementSourceTraverser(Node classNode) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		try {
			try (Transaction tx = database.beginTx()) {
				classNode.getProperty("className");
				TraversalDescription td = database.traversalDescription()
						.relationships(RelTypes.IMPLEMENT, Direction.OUTGOING);
				for (Path path : td.traverse(classNode)) {
					resultNodes.add(path.endNode());
				}
				tx.success();
				return resultNodes;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}*/
	
}
