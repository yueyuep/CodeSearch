package com.triste.codesearch.search;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Match {
	public static void main(String[] args) {
		String path = "D:\\programming\\test\\src\\RectManager.java";
		readMethod(path,"main");
		readClass(path,"RectManager");
	}

	static void readMethod(String s,String key) {
		String keyword=key;
		final String Before_of_Function="p[(\\w+)\\s]*?\\b(";
		final String After_of_Function=")\\b\\(.*?\\)\\s*?\\{[^{}]*?(\\{.*?\\})*[^{}]*?\\}";
		//final String Before_of_Class="\\b(class)\\b.\\b(";
		//final String After_of_Class=")\\b.*?\\{[^{}]*?(\\{.*?\\})*[^{}]*?\\}";
		String temp = null;
		String sou = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(s));
			while ((temp = in.readLine()) != null) {
				sou += temp+"     ";
			}
		} catch (Exception e) {
			System.out.println("error file");
			e.printStackTrace();
		}
		//Before_of_Class+keyword+After_of_Class
		//Before_of_Function+keyword+After_of_Function
		Pattern p = Pattern.compile(Before_of_Function+keyword+After_of_Function,Pattern.MULTILINE);
		Matcher m=p.matcher(sou);
		while(m.find()){
			System.out.println(m.group(0).replaceAll("     ", "\n"));
		}
		
	}
	
	static void readClass(String s,String key) {
		String keyword=key;
		//final String Before_of_Function="p[(\\w+)\\s]*?\\b(";
		//final String After_of_Function=")\\b\\(.*?\\)\\s*?\\{[^{}]*?(\\{.*?\\})*[^{}]*?\\}";
		final String Before_of_Class="\\b(class)\\b.\\b(";
		final String After_of_Class=")\\b.*?\\{[^{}]*?(\\{.*?\\})*[^{}]*?\\}";
		String temp = null;
		String sou = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(s));
			while ((temp = in.readLine()) != null) {
				sou += temp+"     ";
			}
		} catch (Exception e) {
			System.out.println("error file");
			e.printStackTrace();
		}
		//Before_of_Class+keyword+After_of_Class
		//Before_of_Function+keyword+After_of_Function
		Pattern p = Pattern.compile(Before_of_Class+keyword+After_of_Class,Pattern.MULTILINE);
		Matcher m=p.matcher(sou);
		while(m.find()){
			System.out.println(m.group(0).replaceAll("     ", "\n"));
		}
		
	}
    
}

