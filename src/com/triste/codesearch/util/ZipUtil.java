package com.triste.codesearch.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

	//public static boolean flag=false;
	public ZipUtil(){};
	//解压所有压缩包,并保存项目名
	public static void unzip(String sourcePath, String targetPath, String allProNameData) throws IOException{
		File file;
		File[] files;
		ArrayList<String> zipFilePath;
		FileWriter proData;
		ArrayList<String> projectName;
		
		proData = new FileWriter(allProNameData);
		projectName = new ArrayList<String>();
		zipFilePath = new ArrayList<String>();
		
		file = new File(sourcePath);
		files = file.listFiles();
		for(File f : files){
			String zipName = f.getName();
			//String proName = zipName.substring(0, zipName.length() - 4);
			//projectName.add(proName);
			zipFilePath.add(f.getCanonicalPath());
		}
		
		//解压zip压缩包
		for(int i = 0; i < zipFilePath.size(); i++){
			unzipFile(zipFilePath.get(i), targetPath);
		}
		 
		file = new File(targetPath);
		files = file.listFiles();
		for(File f : files){
			//String zipName = f.getName();
			String proName = f.getName();;//zipName.substring(0, zipName.length() - 4);
			projectName.add(proName);
			//zipFilePath.add(f.getCanonicalPath());
		}
		
		
		//保存项目名
		for(int i = 0; i < projectName.size(); i++){
			proData.write(projectName.get(i) + "\r\n");
		}
		

		proData.close();
		
		

		
	}
	
	//解压单个压缩包
	public static void unzipFile(String zipPath, String targetPath) throws IOException{
		File zipFile = new File(zipPath);
		File targetFile = new File(targetPath);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}
		ZipFile zip = new ZipFile(zipFile);
		for(Enumeration entries = zip.entries();entries.hasMoreElements();){
			ZipEntry entry = (ZipEntry)entries.nextElement();
			String zipEntryName = entry.getName();
			InputStream in = zip.getInputStream(entry);  
            String outPath = targetPath + File.separator + zipEntryName;
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists()){  
                file.mkdirs(); 
            }  
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if(new File(outPath).isDirectory()){  
                continue;  
            } 
            
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while((len=in.read(buf1))>0){  
                out.write(buf1,0,len);  
            }  
            in.close();  
            out.close();
        }
	}
}
