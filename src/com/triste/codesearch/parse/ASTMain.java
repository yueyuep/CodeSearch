package com.triste.codesearch.parse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.triste.codesearch.database.Save;
import com.triste.codesearch.maps.Maps;
import com.triste.codesearch.parse.Parser;
import com.triste.codesearch.ui.ParserControllerUI;

import com.triste.codesearch.util.ProNameUtil;
import com.triste.codesearch.util.ZipUtil;

//Parse project
public class ASTMain {
//	private static String projectPath = "RxJava-1.x.zip";
	//E:\Java\Code_Search\Parse\src\com\ast\codesearch
	//public static boolean indexFlag=true;
	public static String projectPath = "";
	//public static String projectPath = "src/com/ast/codesearch";
	//private static File file;
	
	private static ArrayList<String> sourceFilePaths;
	private static ArrayList<String> classpathEntries;
	private static String[] sourcepathEntries;
	

	private static String allZipPath = "zip";
	private static String allProjectPath = "project";
	public static String allProNameData = "allProjectName.dat";
	private static String projectName;
	public static String IS_PARSING = "";
	public static Save save=null;
	
	public ASTMain() throws IOException{
		sourceFilePaths = new ArrayList<String>();
		classpathEntries = new ArrayList<String>();
		sourcepathEntries = new String[1];
	}
	
/*	public void getPath() throws Exception{
		file = new File(projectPath);
		sourcepathEntries[0] = file.getCanonicalPath();
		System.out.println(sourcepathEntries[0]);
		Maps.currentProjectPath=sourcepathEntries[0];
		traversal(file);
	}*/
	
	public static void getPath(File file) throws Exception{
		sourcepathEntries[0] = file.getCanonicalPath();
		System.out.println(sourcepathEntries[0]);
		Maps.currentProjectPath=sourcepathEntries[0];
		traversal(file);
	}
	
	public static void traversal(File file) throws IOException{
		File[] files = file.listFiles();
		for(File f : files){
			if(f.isDirectory()){
				traversal(f);
			}else if (f.getName().endsWith(".java")){
				sourceFilePaths.add(f.getCanonicalPath());
			}else if (f.getName().endsWith(".jar")){
				classpathEntries.add(f.getCanonicalPath());
			}
		}
	}
	
	public static void main(String args[]) throws Exception{
		ParserControllerUI pc = new ParserControllerUI();
		ASTMain ast = new ASTMain();
		File file = new File(allProNameData);
		if(!file.exists()){
			pc.info.setText("解压中...");
			ZipUtil.unzip(allZipPath, allProjectPath, allProNameData);//(allZipPath, allProjectPath, allProNameData);
			pc.info.setText("解压完毕");
			System.out.println("解压完毕");
		}
		int counter = 0;
		ProNameUtil.readProName(allProNameData);
		System.out.println(ProNameUtil.ProName.size());
		boolean fff=true;
		//save=new Save();
		//save.initDB();
		while(fff){
			if(IS_PARSING.equals("YES")){
				if(counter < ProNameUtil.ProName.size()){
					if(counter > 0){
						ProNameUtil.NewProName.remove(0);
					}
					String pName = ProNameUtil.ProName.get(counter);
					String pPath = allProjectPath + File.separator + pName;
					System.out.println("【【【【【【【【【【【【【【【【【【【【【【【【【【"+pPath);
					File f = new File(pPath);
					getPath(f);
					projectName = f.getName();
					projectPath = f.getCanonicalPath();
					System.out.println("sourcePathEntries:"+sourcepathEntries[0]);
					System.out.println("projectName:"+projectName);
					System.out.println("projectPath:"+projectPath);
					Parser parser = new Parser(
							projectName,
							projectPath,
							classpathEntries,
							sourcepathEntries, 
							sourceFilePaths);
					counter++;
					//Visitor.map.displayMap();
					//save.setMap(Visitor.map);
					//save.saveToDB();
					if(IS_PARSING.equals("NO")){
						pc.info.setText("解析已停止，可以关闭。");
						fff=false;
						System.out.println("have modify");
						break;
					}
				}
				else{
					pc.info.setText("解析完成！");
					ProNameUtil.NewProName.remove(0);
					ProNameUtil.saveProName(allProNameData);
					fff=false;
					System.out.println("have modify");
					break;
				}
			}else if(IS_PARSING.equals("NO")){
				continue;
			}else{
				System.out.print(IS_PARSING);
				continue;
			}
		}
		
		//save.shutdown();
		
		System.out.println("end");
	}

	
/*	public void traversal(File file) throws IOException{
		File[] files = file.listFiles();
		for(File f : files){
			if(f.isDirectory()){
//				System.out.println(f.getCanonicalPath());
				traversal(f);
			}else if (f.getName().endsWith(".java")){
				sourceFilePaths.add(f.getCanonicalPath());
			}else if (f.getName().endsWith(".jar")){
				classpathEntries.add(f.getCanonicalPath());
			}
		}
	}*/
	
/*	public static void main(String args[]) throws Exception{
		ASTMain ast = new ASTMain();
		ast.getPath();
		//System.out.println(classpathEntries+" "+sourcepathEntries+" "+sourceFilePaths);
		Parser parser = new Parser(classpathEntries,
				sourcepathEntries,
				sourceFilePaths);
		
		//Visitor.map.displayMap();
		Save save=new Save(Visitor.map);
		save.initDB();
		save.saveToDB();
	}*/
}
