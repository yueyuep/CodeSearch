package com.triste.codesearch.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.neo4j.cypher.internal.compiler.v2_1.docbuilders.internalDocBuilder;
import org.neo4j.cypher.internal.helpers.Converge.iterateUntilConverged;

import com.triste.codesearch.database.Save;
import com.triste.codesearch.ui.ParseUI;

import com.triste.codesearch.util.ProNameUtil;
import com.triste.codesearch.util.ZipUtil;

public class ParseMain {
	
	private static final String EXTERNEL_FOLDER_PATH="AllData";
	
	private static String allZipsFolder=EXTERNEL_FOLDER_PATH+File.separator+"zip";
	private static String allProjectsFolder=EXTERNEL_FOLDER_PATH+File.separator+"project";
	private static String projectList=EXTERNEL_FOLDER_PATH+File.separator+"projectsList.txt";
	//temp�µ�����
	private static String tempProjectFolder=EXTERNEL_FOLDER_PATH+File.separator+"temp";
	private static String javaFileList=tempProjectFolder+File.separator+"javaFileList.txt";
	private static String jarFileList=tempProjectFolder+File.separator+"jarFileList.txt";
	private static String sourcePath=tempProjectFolder+File.separator+"sourcePath.txt";

	private static String projectPath="";
	private static String projectName="";
	
	private static ArrayList<String> sourceFilePaths;//java�ļ�
	private static ArrayList<String> classpathEntries;//���õ�jar��
	private static String[] sourcepathEntries;//��Ŀ·��+����
	
	public static boolean ifStartFlag=false;//trueΪ����
	public static boolean ifexit=false;
	
	public static Save save;
	
	public ParseMain() {
		// TODO �Զ����ɵĹ��캯�����
		sourceFilePaths = new ArrayList<String>();
		classpathEntries = new ArrayList<String>();
		sourcepathEntries = new String[1];
	}
	
	public static void initAllProjects(){
		File file = new File(projectList);
		if(!file.exists()){
			try {
				ZipUtil.unzip(allZipsFolder, allProjectsFolder, projectList);
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
	}
	
	public static boolean initUnfinishedProject(){
		File file=new File(tempProjectFolder);
		if(file.exists()){
			FileReader fr;
			try {
				fr = new FileReader(javaFileList);
				BufferedReader br = new BufferedReader(fr);
				String name;
				while((name = br.readLine()) != null){
					sourceFilePaths.add(name);
				}
				
				fr=new FileReader(jarFileList);
				br=new BufferedReader(fr);
				while((name = br.readLine()) != null){
					classpathEntries.add(name);
				}
				
				fr=new FileReader(sourcePath);
				br=new BufferedReader(fr);
				if((name = br.readLine()) != null){
					projectPath=sourcepathEntries[0]=name;
				}
				if((name=br.readLine())!=null)
					projectName=name;
				
				br.close();
				fr.close();
				
				
			} catch (FileNotFoundException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			
			
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean initOneNewProject(){
		
		int size;
		size=sourceFilePaths.size();
		for(int i=0;i<size;++i)
			sourceFilePaths.remove(0);
		size=classpathEntries.size();
		for(int i=0;i<size;++i)
			classpathEntries.remove(0);
		sourcepathEntries[0]="";
			
		
		projectName=getTopProjectName();
		if(projectName.equals("")){
			File f=new File(projectList);
			f.delete();
			return false;
		}
			
		String path=allProjectsFolder+File.separator+projectName;
		//System.out.println(path);
		File file=new File(path);
		try {
			projectName=file.getName();
			sourcepathEntries[0]=projectPath=file.getCanonicalPath();
		// System.out.println(file.getCanonicalPath());
			traversal(file);
			
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static void deleteTempFolder(){
		File file=new File(tempProjectFolder);
		if(file.exists()){
			ParseMain.deleteAllFilesOfDir(file);
			System.out.println("delete temp success");
		}
	}
	
	private static void deleteAllFilesOfDir(File path) {  
	    if (!path.exists())  
	        return;  
	    if (path.isFile()) {  
	        path.delete();  
	        return;  
	    }  
	    File[] files = path.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        deleteAllFilesOfDir(files[i]);  
	    }  
	    path.delete();  
	} 
	
	public static void saveUnfinishedProject(){
		
		FileWriter fw;
		PrintWriter pw;
		try {
			File file = new File(tempProjectFolder);
			  //�ж��ļ����Ƿ����,����������򴴽��ļ���
			  if (!file.exists()) {
			   file.mkdir();
			  }
			
			fw = new FileWriter(javaFileList);
			pw =new PrintWriter(fw);
			for(int i=0;i<sourceFilePaths.size();++i){
				pw.println(sourceFilePaths.get(i));
			}
			pw.close();
			fw.close();
			
			fw=new FileWriter(jarFileList);
			pw=new PrintWriter(fw);
			for(int i=0;i<classpathEntries.size();++i)
				pw.println(classpathEntries.get(i));
			fw.close();
			pw.close();
			
			fw=new FileWriter(sourcePath);
			pw=new PrintWriter(fw);
			pw.println(sourcepathEntries[0]);
			pw.println(projectName);
			fw.close();
			pw.close();
			
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
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
	
	public static int getProjectCount(){
		FileReader fr;
		int count=0;
		try {
			File file = new File(projectList);
			if(file.exists()){
				fr = new FileReader(projectList);
				BufferedReader br = new BufferedReader(fr);
				String name;
				
				while((name = br.readLine()) != null)
					++count;
				br.close();
				fr.close();
			}		
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			//e.printStackTrace();
			System.out.println("projectList does not exist!");
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		System.out.println("ddddd");
		return count;
	}
	
	public static String getTopProjectName(){
		FileReader fr;
		String getName="";
		try {
			fr = new FileReader(projectList);
			BufferedReader br = new BufferedReader(fr);
			String name;
			if((name=br.readLine())!=null)
				getName=name;
			ArrayList<String> list=new ArrayList<String>();
			while((name = br.readLine()) != null){
				list.add(name);
			}
			br.close();
			fr.close();
			
			FileWriter fw=new FileWriter(projectList);
			PrintWriter pw =new PrintWriter(fw);
			for(int i=0;i<list.size();++i){
				pw.println(list.get(i));
			}
			pw.close();
			fw.close();
			
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
		return getName;
	}
	
	public static void shutdown(){
		save.shutdown();
		
		if(sourceFilePaths.size()!=0){////��java�ļ���û������
			saveUnfinishedProject();
			System.out.println("save unfinished success");
		}
	}
	
	public static void loop(){
		
		
	}

	public static void printInfo(){
		System.out.println("----------project name----");
		System.out.println(sourcepathEntries[0]+"\n");
		System.out.println("----------java�ļ�--------");
		for(int i=0;i<sourceFilePaths.size();++i)
			System.out.println(sourceFilePaths.get(i));
		System.out.println("----------jar��-----------");
		for(int i=0;i<classpathEntries.size();++i)
			System.out.println(classpathEntries.get(i));
		
		
	}

	public static void main(String[] args,ParseUI ui) {
			
		Thread t = new Thread(){
			@Override
			public void run(){
				// TODO �Զ����ɵķ������
				ParseMain pm=new ParseMain();

				
				System.out.println("start");
				//ParseUI ui=new ParseUI();
				//ui.setSize(500, 200);
				ui.setVisible(true);
				initAllProjects();
				int projectCount=getProjectCount();
				int finishedProject=0;
				if(initUnfinishedProject()){
					System.out.println("----------------in unfinishedProject---------------");
					++projectCount;
					printInfo();
				}
				
				boolean isEnterParsing = false;
				
				ui.text_info1.setText("��"+projectCount+"����Ŀ��"+"�ѽ�����"+finishedProject+"����Ŀ");
				ifStartFlag=false;
				save=new Save();
				save.initDB();
				while(!ifexit){
					System.out.println("in");
					if(ifStartFlag){//���ܿ�ʼ����
						//���java�ļ��Ѿ�������
						if(sourceFilePaths.size()==0){
							if(initOneNewProject()){
								System.out.println("-----------------��init a new project��-------------------");
								printInfo();}
							else {
								deleteTempFolder();
								ui.text_info1.setText("������Ŀ�ѽ����꣬���Թرա�");
								break;
							}
						}
						//û������
						else{
						    int allsize=sourceFilePaths.size();
							int finishedsize=0;
							ui.text_info.setText("����Ŀ��"+allsize+"���ļ���"+"�ѽ�����"+finishedsize+"���ļ�");
							while((!ifexit)&&ifStartFlag&&sourceFilePaths.size()!=0){//��ÿjava�ļ��ֿ�����
								
								ui.text_current_project.setText("��ǰ������Ŀ:"+projectPath);
								ArrayList<String> tempJavaList=new ArrayList<String>();
								tempJavaList.add(sourceFilePaths.get(0));
								ui.text_current_file.setText("��ǰ�����ļ�:"+sourceFilePaths.get(0));
								System.out.println("��ǰ�����ļ�:"+"��������"+projectPath+"��������:  "+sourceFilePaths.get(0));
								sourceFilePaths.remove(0);
								
								Visitor.clearMap();
								Parser parser = new Parser(
										projectName,
										projectPath,
										classpathEntries,
										sourcepathEntries, 
										tempJavaList);
								//Visitor.map.displayMap();
								//System.out.println("-------------------------");
								//Visitor.map.displayExtendsMap();
								save.setMap(Visitor.map);
								save.saveToDB();
								++finishedsize;
								ui.text_info.setText("����Ŀ��"+allsize+"���ļ���"+"�ѽ�����"+finishedsize+"���ļ�");
								if(!ifStartFlag)
									ui.text_state.setText("����ͣ���ɰ��˳��رա�");
								
								isEnterParsing = true;
								
							}
							if(sourceFilePaths.size()==0)
								++finishedProject;
							ui.text_info1.setText("��"+projectCount+"����Ŀ��"+"�ѽ�����"+finishedProject+"����Ŀ");
						}	
					}
				}
				
				if(isEnterParsing)
					ui.setPagerankBtn();
					
				
				shutdown();
				
				System.out.println("-------------------------��������end��������------------------------------");
				
				
				/*ParseMain parse=new ParseMain();
				ParseMain.initAllProjects();
				ParseMain.initOneNewProject();
				ParseMain.saveUnfinishedProject();*/
				//ParseMain.deleteTempFolder();

			}
		};
	
		t.start();
	
	}

}
