package com.triste.codesearch.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ProNameUtil {
	public static ArrayList<String> ProName = new ArrayList<String>();
	public static ArrayList<String> NewProName = new ArrayList<String>();
	
	public ProNameUtil(){};
	
	public static void readProName(String dataFilePath) throws IOException{
		FileReader fr = new FileReader(dataFilePath);
		BufferedReader br = new BufferedReader(fr);
		String name;
		while((name = br.readLine()) != null){
			ProName.add(name);
			NewProName.add(name);
		}
		br.close();
		fr.close();
	}
	
	public static void saveProName(String dataFilePath) throws IOException{
		FileWriter fw = new FileWriter(dataFilePath);
		for(int i = 0; i < NewProName.size(); i++){
			fw.write(NewProName.get(i)+ "\r\n");
		}
		fw.close();
	}
}
