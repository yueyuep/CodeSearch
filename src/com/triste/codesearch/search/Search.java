package com.triste.codesearch.search;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.graphdb.Node;

public class Search {
	
///////////////////////////////////search method
	public static String SearchMethod(String name,String param){
		
		String s= "";
		
		SearchMethod sm = new SearchMethod();
		
		ArrayList<Node> list = sm.searchByName(name,param);
		
		sm.shutdown();
		return s;
	}
	

	
	///////////////////////////////////search class
	public static String SearchClass(){
		
		String s= "";
		
		
		
		
		return s;
	}
	
	

	public static void main(String[] args) {
		
		
		SearchMethod sm = new SearchMethod();
		
		//String ss = sm.search(-1592701910);
		String ss = sm.search("println", "String");
		//System.out.println(ss);
		String s = "example3.txt";
		try {
			FileWriter proData = new FileWriter(s);
			proData.write(ss);
			proData.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		sm.shutdown();
		
		
/*		SearchClass sm = new SearchClass();

		
		//String ss = sm.search("Filter");
		String ss = sm.search(-135018565);
		String s = "class3.txt";
		try {
			FileWriter proData = new FileWriter(s);
			proData.write(ss);
			proData.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	
		sm.shutdown();*/
		System.out.println("query end");
		
		
		// TODO 自动生成的方法存根
		
/*		JSONObject obj = new JSONObject();
		ArrayList<JSONObject> s= new ArrayList<JSONObject>();
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		try {
			obj1.put("id", 101);
			obj1.put("name", "wang");
			obj2.put("id", 201);
			obj2.put("name", "li");
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		s.add(obj1);
		s.add(obj2);
		JSONArray arr = new JSONArray((Collection)s);
		
		System.out.println(arr.toString());*/
		
		
	}

}
