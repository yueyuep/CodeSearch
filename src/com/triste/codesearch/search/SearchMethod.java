package com.triste.codesearch.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.triste.codesearch.pagerank.Database;

public class SearchMethod extends Database{
	
	public SearchMethod(){
		this.initDB();
	}
	
/*	public void shutdown(){
		this.shutdown();
	}*/
	
	public String search(long id){
		ArrayList<Node> list = searchByID(id);
		
		JSONObject obj = new JSONObject();
		ArrayList<String> objList = new ArrayList<String>();
		for(int i=0;i<list.size();++i){
			objList.add(getOneMethodString(list.get(i)));
		}
		JSONArray arr = new JSONArray((Collection)objList);
		/*try {
			obj.put("list", arr);
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
		
		return arr.toString();
	}
	
	public String search(String name,String param){
		ArrayList<Node> list = searchByName(name, param);
		
		JSONObject obj = new JSONObject();
		ArrayList<String> objList = new ArrayList<String>();
		for(int i=0;i<list.size();++i){
			objList.add(getOneMethodString(list.get(i)));
		}
		JSONArray arr = new JSONArray((Collection)objList);
		/*try {
			obj.put("list", arr);
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
		
		return arr.toString();
	}
	
	
	public  String getOneMethodString(Node node){
		
		
			JSONObject obj = new JSONObject();
			try {
				try (Transaction tx = database.beginTx()){
					
					String pc = ((String)node.getProperty(CLASS_INFO)).replace("/", "");
					String mn = (String)(node.getProperty(METHOD_NAME));
					System.out.println(pc);
					
					obj.put("id", (long)(pc+mn).hashCode());
					obj.put("method_name", mn);
					String packageName = "";
					String className = "";
					String s =(String)node.getProperty(CLASS_INFO);
					if(!s.equals("")){
						String []a = s.split("/");
						//System.out.println(a[0]);
						packageName = a[0];
						className = a[1];
					}
					
					obj.put("package_name", packageName);
					obj.put("class", className);
					
					double d = -1;
					if(node.hasProperty(PAGERANK))
						d = (double)node.getProperty(PAGERANK);
					obj.put("pagerank", d);
					obj.put("type", "method");
					obj.put("param", (String)node.getProperty(PARAM));
					obj.put("return_type", (String)node.getProperty(RETURN_TYPE));
					obj.put("parse_state", (boolean)node.getProperty(IS_PARSE));
					
					tx.success();
				}
					obj.put("file_locatioon", (String)getFileLocation(node));
					obj.put("invoke_method", "");//getInvokeListInfo(searchInvokeMethod(node))
					obj.put("invoked_method", getInvokeListInfo(searchInvokerMethod(node)));
					
					
					return obj.toString();
			
				
			} catch (JSONException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				//tx.success();
				return "";
			}catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				//tx.success();
				return "";
			}
			
		
		

	}
	
	public String getInvokeListInfo(ArrayList<Node> list){
		
		try {
			try (Transaction tx = database.beginTx()){
				if(list==null)
					return "";
				ArrayList<JSONObject> objList = new ArrayList<JSONObject>();
				for(int i=0;i<list.size();++i){
					Node node = list.get(i);
					String pc = ((String)node.getProperty(CLASS_INFO)).replace("/", "");
					String mn = (String)(node.getProperty(METHOD_NAME));
					JSONObject obj = new JSONObject();
					obj.put("id", (long)(pc+mn).hashCode());
					obj.put("name", mn);
					//obj.put("path", (String)node.getProperty(PATH));
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
	
	public ArrayList<Node> searchByName(String methodName,String param) {
		Label label = DynamicLabel.label("methodLabel");
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		ArrayList<Node> resultParamNodes = new ArrayList<Node>();
		try (Transaction tx = database.beginTx()) {
			for (Node node : database.findNodesByLabelAndProperty(label,
					METHOD_NAME, methodName)) {
				if(containParam((String)node.getProperty(PARAM), param))
					resultParamNodes.add(node);
				else
					resultNodes.add(node);
				
			}
			tx.success();
		}
		
		sortH2L(resultParamNodes);
		sortH2L(resultNodes);
		
		//加到结尾
		for(int i=0;i<resultNodes.size();++i)
			resultParamNodes.add(resultNodes.get(i));
		
		return resultParamNodes;

	}
	
	public ArrayList<Node> searchByID(long id) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		try (Transaction tx = database.beginTx()) {
		
			Node node = methodIndex.get(METHOD_ID, id).getSingle();
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
	
	public boolean containParam(String param,String target){
		
		if(param.equals(""))
			return false;
		
		String []a = param.split(",");
		String []b = target.split(",");
		
		//boolean flag = false;
		boolean []flag = new boolean[b.length];
		for(int i=0;i<b.length;++i)
			flag[i]=false;
		for(int i = 0;i<b.length;++i){
			for(int j=0;j<a.length;++j){
				if((b[i].toLowerCase()).equals(a[j].toLowerCase())){
					flag[i]=true;
					break;
				}		
			}
		}
		
		for(int i=0;i<flag.length;++i)
			if(!flag[i])
				return false;
		
		return true;
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

	// find the methods which invoke this method
	public ArrayList<Node> searchInvokerMethod(Node methodNode) {
		// wait to be check null or ""

		try {
			ArrayList<Node> resultNodes = new ArrayList<Node>();
			try (Transaction tx = database.beginTx()) {
				// TraversalDescription td=
				// database.traversalDescription().relationships(Relationships.INVOKE,
				// Direction.INCOMING);
				// for(Path path: td.traverse(methodNode)){
				// resultNodes.add(path.startNode());
				// }
				for (Relationship iter : methodNode.getRelationships(
						Relationships.INVOKE, Direction.INCOMING)) {
					resultNodes.add(iter.getStartNode());
				}
				tx.success();
				return resultNodes;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a method");
			return null;
		}

	}

	// find the methods which is invoked by this method
	public ArrayList<Node> searchInvokeMethod(Node methodNode) {
		try {
			ArrayList<Node> resultNodes = new ArrayList<Node>();
			try (Transaction tx = database.beginTx()) {
				// TraversalDescription td=
				// database.traversalDescription().breadthFirst()
				// .relationships(Relationships.INVOKE,
				// Direction.OUTGOING).evaluator(Evaluators.atDepth(1));
				// // TraversalDescription td=
				// database.traversalDescription().relationships(Relationships.INVOKE,
				// Direction.OUTGOING);
				// for(Path path: td.traverse(methodNode)){
				// resultNodes.add(path.endNode());
				// }
				for (Relationship iter : methodNode.getRelationships(
						Relationships.INVOKE, Direction.OUTGOING)) {
					resultNodes.add(iter.getEndNode());
				}
				tx.success();
				return resultNodes;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a method");
			return null;
		}
	}

	public Node getContainClass(Node methodNode) {
		try {
			try (Transaction tx = database.beginTx()) {
				Node classNode = methodNode.getSingleRelationship(
						Relationships.CONTAIN_METHOD, Direction.INCOMING).getStartNode();
				return classNode;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a method");
			return null;
		}

	}

	public String getFileLocation(Node methodNode) throws IOException {
		try {
			try (Transaction tx = database.beginTx()) {
				if(!(boolean)methodNode.getProperty(IS_PARSE))
					return null;
				Node classNode = methodNode.getSingleRelationship(
						Relationships.CONTAIN_METHOD, Direction.INCOMING).getStartNode();
/*				Node proNode = classNode.getSingleRelationship(
						Relationships.CONTAIN_CLASS, Direction.INCOMING).getStartNode();*/
				if(classNode==null)
					return null;
				String path = (String) classNode
						.getProperty(PATH);
	/*			packageName = packageName.replace('.', '\\');
				String fileLocation = (String) proNode
						.getProperty("fileLocation");
				String className = (String) classNode.getProperty("className");
				String classLocation = fileLocation + '\\' + packageName + '\\'
						+ className + ".java";
				try {
					new FileReader(classLocation);
				} catch (FileNotFoundException e) {
					String classLocationForSC = getFileLocationForSC(
							fileLocation, packageName, className, classLocation);
					System.out.println(classLocationForSC);
					return classLocationForSC;
				}*/
				tx.success();
				return path;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a method");
			return null;
		}

	}

	public String getFileLocationForSC(String fileLocation, String packageName,
			String className, String classLocation) throws IOException {
		File Dir = new File(fileLocation + '\\' + packageName + '\\');
		String content;
		for (File iter : Dir.listFiles()) {
			StringBuilder fileData = new StringBuilder(1000);
			BufferedReader reader = new BufferedReader(new FileReader(
					iter.getCanonicalPath()));
			char[] buf = new char[10];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
			content = fileData.toString();
			if (content.contains("class " + className)) {
				return iter.getCanonicalPath();
			}
		}
		return "Class not found";
	}

}
