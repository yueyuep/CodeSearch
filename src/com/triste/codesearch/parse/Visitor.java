package com.triste.codesearch.parse;

import java.util.ArrayList;
import java.util.Random;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.triste.codesearch.maps.Maps;
import com.triste.codesearch.node.ClassNode;
import com.triste.codesearch.node.MethodNode;
import com.triste.codesearch.node.ProjectNode;
import com.triste.codesearch.node.ClassNode.ClassType;

public class Visitor extends ASTVisitor{
	static Maps map=null;
	static boolean isInNestClass=false;
	
	public static void clearMap(){
		map=new Maps();
	}
	
	public Visitor(){
		
	}
	
	//import
/*	public boolean visit(ImportDeclaration node){
		String packageName = node.getName().getFullyQualifiedName();
		//System.out.println("【Package】" + packageName);
		//System.out.println("");
		return true;
	}*/
	
	//class
	/*public boolean visit(TypeDeclaration node){
		String className = node.getName().getFullyQualifiedName();
		boolean ifUpdateLastClass=true;//因为有可能有嵌套类
		isInNestClass=false;
		try{
			ITypeBinding binding = node.resolveBinding();
			//System.out.println("【Class】" + className);
			String classPackageName = binding.getPackage().getName();
			//System.out.println("(package)" + classPackageName);
			//当前节点
			ClassNode classNode=new ClassNode(className, classPackageName, Maps.currentFilePath);
			classNode.setParseState(true);
			map.addNode(map.getProjectNode(), classNode);
			map.setClassNode(classNode);
			///////////////////////当前类为嵌套类
			if(binding.isNested()){
				isInNestClass=true;
				ifUpdateLastClass=false;
				map.addNestClassNode(map.getExternalClassNode(), classNode);
				//System.out.println("<Nest>");
			}
			///////////////////////不是嵌套类
			//是接口
			if(node.isInterface()){
				//System.out.print("Interface");
				classNode.setClassType(ClassType.INTERFACE);
				if(node.getSuperclassType() != null){
					String superClassName = node.getSuperclassType().resolveBinding().getName();
					String extendPackage=node.getSuperclassType().resolveBinding()
							.getPackage().getName();
					ClassNode classNode2=new ClassNode(superClassName,extendPackage);
					classNode2.setClassType(ClassType.INTERFACE);
					//map.addNode(map.getProjectNode(), classNode);
					map.addNode(classNode, classNode2);
					//System.out.print(", extends " + superClassName);
				}
				//System.out.println("");
			}
			//是class
			else{
				//System.out.print("Class");
				//extends class
				if(node.getSuperclassType() != null){
					String extendName = node.getSuperclassType().resolveBinding().getName();
					String extendPackage = node.getSuperclassType().resolveBinding()
							.getPackage().getName();
					//System.out.print(", extends " + extendName 
							+ "(" + extendPackage + "),");
					ClassNode classNode2=new ClassNode(extendName,extendPackage);
					//map.addNode(map.getProjectNode(), classNode2);
					map.addNode(classNode, classNode2);
				}
				//implements interface
				if(!node.superInterfaceTypes().isEmpty()){
					//System.out.print(" implements ");
					for(ITypeBinding i : node.resolveBinding().getInterfaces()){
						String implementName = i.getErasure().getName();
						String implementPackage = i.getPackage().getName();
						//System.out.print(implementName + "(" + implementPackage + ") ");
						ClassNode classNode2=new ClassNode(implementName, implementPackage);
						classNode2.setClassType(ClassType.INTERFACE);
						//map.addNode(map.getProjectNode(), classNode2);
						map.addNode(classNode, classNode2);
					}
				}
				//System.out.println("");
				if(ifUpdateLastClass)
				map.setExternalClassNode(classNode);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("");
		return true;
	}
	*/
	
	
	public boolean visit(TypeDeclaration node){
		String typeName = node.getName().getFullyQualifiedName();
		
		String param = "";
		try {
			ITypeBinding binding = node.resolveBinding();
			String classPackageName = binding.getPackage().getName();
			/////////////////////////////////////////////////////////////////////////////////////////
			///嵌套类
			if (binding.isNested()){
				//System.out.println(">>>>>>>>>>嵌套类<<<<<<<<<<");
				//System.out.println(classPackageName+"---"+typeName);
				ClassNode classNode=new ClassNode(typeName, classPackageName,Maps.currentFilePath );
				classNode.setParseState(true);
				map.addNode(map.getProjectNode(), classNode);
				map.setLastAllClassNode(classNode);		
				//是接口
				if(node.isInterface()){
					//System.out.print("Interface");
					classNode.setClassType(ClassType.INTERFACE);
					
				/*	if(node.getSuperclassType() != null){
						String superClassName = node.getSuperclassType().resolveBinding().getName();
						String extendPackage=node.getSuperclassType().resolveBinding()
								.getPackage().getName();
						ClassNode extendClassNode=new ClassNode(superClassName, extendPackage);
						extendClassNode.setClassType(ClassType.INTERFACE);
						//System.out.print(", extends " + superClassName);
						map.addNode(classNode, extendClassNode);
					}*/
					//interface extends interface
					if(!node.superInterfaceTypes().isEmpty()){
						//System.out.print(" implements ");
						for(ITypeBinding i : node.resolveBinding().getInterfaces()){
							String implementName = i.getErasure().getName();
							String implementPackage = i.getPackage().getName();
							//System.out.print(implementName + "(" + implementPackage + ") ");
							ClassNode classNode2=new ClassNode(implementName, implementPackage);
							classNode2.setClassType(ClassType.INTERFACE);
							map.addNode(classNode, classNode2);
						
						}
					}
					//System.out.println("");
				}
				//是class
				else{
					//System.out.print("Class");
					//class extends class
					if(node.getSuperclassType() != null){
						String extendName = node.getSuperclassType().resolveBinding().getName();
						String extendPackage = node.getSuperclassType().resolveBinding()
								.getPackage().getName();
						//System.out.print(", extends " + extendName + "(" + extendPackage + "),");
						ClassNode extendClassNode=new ClassNode(extendName,extendPackage);
						map.addNode(classNode, extendClassNode);
						
					}
					//class implements interface
					if(!node.superInterfaceTypes().isEmpty()){
						//System.out.print(" implements ");
						for(ITypeBinding i : node.resolveBinding().getInterfaces()){
							String implementName = i.getErasure().getName();
							String implementPackage = i.getPackage().getName();
							//System.out.print(implementName + "(" + implementPackage + ") ");
							ClassNode classNode2=new ClassNode(implementName, implementPackage);
							classNode2.setClassType(ClassType.INTERFACE);
							map.addNode(classNode, classNode2);
						
						}
					}
					//System.out.println("");
				
				}
						
				map.addNestClassNode(map.getClassNode(),classNode);
				
				
				for (IMethodBinding mBinding : binding.getDeclaredMethods()){
					if (mBinding.getName() != ""){
						param = getParamInMethod(mBinding);
						String returnType=mBinding.getReturnType().getName();
						//System.out.println(returnType+" "+mBinding.getName()+ "(" + param + ")");
						MethodNode methodNode=new MethodNode(mBinding.getName(), classNode, returnType, param);
						methodNode.setParseState(true);
						map.addNode(classNode, methodNode);
					}
				}
			} 
			////////////////////////////////////////////////////////////////////////////////////////////////
			//非嵌套类
			else{
				//System.out.println(">>>>>>>>>>不是嵌套类<<<<<<<<<<");
				ClassNode classNode=new ClassNode(typeName, classPackageName, Maps.currentFilePath);
				classNode.setParseState(true);
				map.addNode(map.getProjectNode(), classNode);
				map.setClassNode(classNode);
				map.setLastAllClassNode(classNode);	
				//是接口
				if(node.isInterface()){
					//System.out.print("Interface");
					classNode.setClassType(ClassType.INTERFACE);
				/*	if(node.getSuperclassType() != null){
						String superClassName = node.getSuperclassType().resolveBinding().getName();
						String extendPackage=node.getSuperclassType().resolveBinding()
								.getPackage().getName();
						ClassNode extendClassNode=new ClassNode(superClassName, extendPackage);
						extendClassNode.setClassType(ClassType.INTERFACE);
						//System.out.print(", extends " + superClassName);
						map.addNode(classNode, extendClassNode);
					}*/
					//interface extends interface
					if(!node.superInterfaceTypes().isEmpty()){
						//System.out.print(" implements ");
						for(ITypeBinding i : node.resolveBinding().getInterfaces()){
							String implementName = i.getErasure().getName();
							String implementPackage = i.getPackage().getName();
							//System.out.print(implementName + "(" + implementPackage + ") ");
							ClassNode classNode2=new ClassNode(implementName, implementPackage);
							classNode2.setClassType(ClassType.INTERFACE);
							map.addNode(classNode, classNode2);
						
						}
					}
					
					//System.out.println("");
				}
				//是class
				else{
					//System.out.print("Class");
					//class extends class
					if(node.getSuperclassType() != null){
						String extendName = node.getSuperclassType().resolveBinding().getName();
						String extendPackage = node.getSuperclassType().resolveBinding()
								.getPackage().getName();
						//System.out.print(", extends " + extendName + "(" + extendPackage + "),");
						ClassNode extendClassNode=new ClassNode(extendName,extendPackage);
						map.addNode(classNode, extendClassNode);
						
					}
					//class implements interface
					if(!node.superInterfaceTypes().isEmpty()){
						//System.out.print(" implements ");
						for(ITypeBinding i : node.resolveBinding().getInterfaces()){
							String implementName = i.getErasure().getName();
							String implementPackage = i.getPackage().getName();
							//System.out.print(implementName + "(" + implementPackage + ") ");
							ClassNode classNode2=new ClassNode(implementName, implementPackage);
							classNode2.setClassType(ClassType.INTERFACE);
							map.addNode(classNode, classNode2);
						
						}
					}
					//System.out.println("");
				
				}
				
				
				////System.out.println(classPackageName+"---"+typeName);
				//ClassNode classNode=new ClassNode(typeName,classPackageName,Maps.currentFilePath);
				//map.setClassNode(classNode);
				//map.addNode(map.getProjectNode(), classNode);
				for (IMethodBinding mBinding : binding.getDeclaredMethods()){
					//MethodDeclaration mDec=(MethodDeclaration)mBinding.getMethodDeclaration();
					//mBinding.get
					
					if (mBinding.getName() != ""){
						param = getParamInMethod(mBinding);
						//param=getFormatParamType(param);
						String returnType=mBinding.getReturnType().getName();
						//System.out.println(returnType+" "+mBinding.getName()+ "(" + param + ")");

						MethodNode methodNode=new MethodNode(mBinding.getName(), classNode, returnType, param);
						methodNode.setParseState(true);
						map.addNode(classNode, methodNode);
					}
				}
			}
		} catch(Exception e){
			//System.out.println("Error occured while parsing type.");
			//e.printStackTrace();
		}
		//System.out.println("");
		return true;
	}
	
	
	public boolean visit(AnonymousClassDeclaration node){
		////System.out.println(">>>>>>>>>>>匿名类<<<<<<<<<<");
		ITypeBinding binding = node.resolveBinding();
		ArrayList<String> paramTypes = new ArrayList<String>();
		String param = "";
		////System.out.println(">>>>>>>>>>>匿名类<<<<<<<<<<");
		////System.out.println(binding.getSuperclass().getQualifiedName());
		if(binding!=null){
			int i=0;
			for(IMethodBinding mBinding : binding.getDeclaredMethods()){
				++i;
				if(mBinding.getName() != ""){
					param = getParamInMethod(mBinding);

					////System.out.println(mBinding.getName() + "("+ getFormatParamType(param) + ")");
				}
			}
			return true;
		}
		else{
			return false;
		}
		
	}
	
	
	//method
    public boolean visit(MethodDeclaration node){
    	
    	//map.setMethodNode(node);
    	String methodName = node.getName().getFullyQualifiedName();
		String returnType;
		/*if(node.getReturnType2()!=null)
		returnType=node.getReturnType2().toString();
		else {
			returnType="";
		}*/
		////System.out.println("before binding"+methodName);
		IMethodBinding binding = node.resolveBinding();

		////System.out.println("after binding");
		if(binding!=null){
			ITypeBinding classBinding=binding.getDeclaringClass();
			returnType=binding.getReturnType().getName();
			String param=getParamInMethod(binding);
			MethodNode methodNode=new MethodNode(methodName,
					new ClassNode(classBinding.getName(), classBinding.getPackage().getName(), Maps.currentFilePath), 
					returnType, param);
			
	    	
	    	if(map.containMethodNode(methodNode)){
	    		map.setMethodNode(methodNode);
	    		////System.out.println("【in methodDeclaration】:setMethodNode "+methodNode.toString());
	    	}
			
	    	return true;
		}
		else{
			return false;
		}
		
		

    }
    
  //invokedMethod
    public boolean visit(MethodInvocation node){
    	
    	String methodName = node.getName().getIdentifier();
		String param = "";
		/////////////////////读取参数
		ArrayList<String> paramTypes = new ArrayList<String>();
		if(node.resolveMethodBinding() != null){
			try{
				for(ITypeBinding i : node.resolveMethodBinding()
						.getParameterTypes()){
					if(!i.getQualifiedName().equals(null)){
						paramTypes.add(i.getQualifiedName());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			if(paramTypes.size() == 0)
				;
			else if(paramTypes.size() == 1){
				param += getFormatParamType(paramTypes.get(paramTypes.size() - 1)); 
			}
			else{
				for(int i = 0; i < paramTypes.size() - 1; i++){
					param += getFormatParamType((paramTypes.get(i)) + ",");
				}
				param += getFormatParamType(paramTypes.get(paramTypes.size() - 1));
			}
			}
		else
		{
			param = "ErrorParam"+(int)(Math.random()*1000);	
		}
    	////////////////////////////
		String invokeClassName="";
		String invokePackageName="";
		String returnType="";
		IMethodBinding binding=node.resolveMethodBinding();
		if(binding!=null)
			returnType=binding.getReturnType().getName();
		else {
			returnType="ErrorType"+(int)(Math.random()*1000);
		}
		
		try{
			if(node.getExpression() != null){
				invokeClassName = node.getExpression().resolveTypeBinding().getErasure().getName();
				invokePackageName = node.getExpression().resolveTypeBinding().getPackage().getName();
					//System.out.println("【MethodInvocation】<by class>" + methodName + "(" + param + ")"+ "  " + "(invoked)" + invokePackageName + "." + invokeClassName);
				MethodNode methodNode=new MethodNode(methodName, 
						new ClassNode(invokeClassName, invokePackageName), "", param);
				map.addNode(map.getMethodNode(), methodNode);
				//System.out.println("【in invokeMethod】: "+node.getExpression());
			}
			else{	
				//System.out.println("【MethodInvocation】<by method>" + methodName + "(" + param + ")");	
			}
			}catch(Exception e){
				//System.out.println("There is a exception in MethodInvocation!");
			}

    	
    	return true;
    }
	
	//method
/*	public boolean visit(MethodDeclaration node){
		String methodName = node.getName().getFullyQualifiedName();
		String returnType;
		if(node.getReturnType2()!=null)
		returnType=node.getReturnType2().toString();
		else {
			returnType="";
		}
		//读取参数
		ArrayList<String> paramTypes = new ArrayList<String>();
		try{
			IMethodBinding binding = node.resolveBinding();
			for(ITypeBinding i : binding.getParameterTypes()){
				if(!i.getQualifiedName().equals(null)){
					paramTypes.add(i.getQualifiedName());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		String param = "";
		if(paramTypes.size() == 0){
		}else if(paramTypes.size() == 1){
			param += paramTypes.get(paramTypes.size() - 1); 
		}else{
			for(int i = 0; i < paramTypes.size() - 1; i++){
				param += (paramTypes.get(i) + ",");
			}
			param += paramTypes.get(paramTypes.size() - 1);
		}
		//System.out.println("【Method】 " +returnType +" "+methodName 
				+ "(" + param + ")");
		
		MethodNode methodNode=new MethodNode(methodName, map.getClassNode(), returnType, param);
		map.addNode(map.getClassNode(), methodNode);
		map.setMethodNode(methodNode);
		
		
		//System.out.println("");
		return true;
	}
	*/
	
	//invokedMethod
/*	public boolean visit(MethodInvocation node){
		String methodName = node.getName().getIdentifier();
		String param = "";
		//读取参数
		ArrayList<String> paramTypes = new ArrayList<String>();
		if(node.resolveMethodBinding() != null){
			try{
				for(ITypeBinding i : node.resolveMethodBinding()
						.getParameterTypes()){
					if(!i.getQualifiedName().equals(null)){
						paramTypes.add(i.getQualifiedName());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			if(paramTypes.size() == 0){
			}else if(paramTypes.size() == 1){
				param += paramTypes.get(paramTypes.size() - 1); 
			}else{
				for(int i = 0; i < paramTypes.size() - 1; i++){
					param += (paramTypes.get(i) + ",");
				}
				param += paramTypes.get(paramTypes.size() - 1);
			}
			}
			else
				param = "Error"+(int)(Math.random()*1000);
		
		
		if(node.resolveMethodBinding() != null){
		try{
			for(ITypeBinding i : node.resolveMethodBinding().getParameterTypes()){
				if(!i.getQualifiedName().equals(null)){
					paramTypes.add(i.getQualifiedName());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		}
		String param = "";
		if(paramTypes.size() == 0){
		}else if(paramTypes.size() == 1){
			param += paramTypes.get(paramTypes.size() - 1); 
		}else{
			for(int i = 0; i < paramTypes.size() - 1; i++){
				param += (paramTypes.get(i) + ",");
			}
			param += paramTypes.get(paramTypes.size() - 1);
		}
		try{
		if(node.getExpression() != null){
			String invokeClassName = node.getExpression().resolveTypeBinding().getErasure().getName();
			String invokePackageName = node.getExpression().resolveTypeBinding().getPackage().getName();
				//System.out.println("【MethodInvocation】<by class>" + methodName + "(" + param + ")"
						+ "  " + "(invoked)" + invokePackageName + "." + invokeClassName);
			MethodNode methodNode=new MethodNode(methodName, 
					new ClassNode(invokeClassName, invokePackageName), "", param);
			map.addNode(map.getMethodNode(), methodNode);
		}
		else{
			
			//System.out.println("【MethodInvocation】<by method>" + methodName + "(" + param + ")");
			
		}
		}catch(Exception e){
			//System.out
					.println("There is a exception in MethodInvocation!");
		}
		//System.out.println("");
		return true;
	}
	
	*/
	
	public String getParamInMethod(IMethodBinding mBinding){
		ArrayList<String> paramTypes = new ArrayList<String>();
		String param = "";
		try{
			for (ITypeBinding i : mBinding.getParameterTypes()){
				if(!i.getQualifiedName().equals(null)){
					paramTypes.add(i.getQualifiedName());
				}
			}
		} catch(Exception e){
			//System.out.println("Error occured while parsing method parameters.");
		}
		if(paramTypes.size() == 0){
		}else if(paramTypes.size() == 1){
			param += getFormatParamType(paramTypes.get(paramTypes.size() - 1)); 
		}else{
			for(int i = 0; i < paramTypes.size() - 1; i++){
				param += getFormatParamType((paramTypes.get(i) + ","));
			}
			param += getFormatParamType(paramTypes.get(paramTypes.size() - 1));
		}
		
		
		return param;
	}
	
	public String getFormatParamType(String param){
		
		if(!param.equals("")){
			int i=param.lastIndexOf(".")+1;
			int e=param.length();
			////System.out.println(i+" "+e);
			return param.substring(i, e);
		}
		else 
			return param;
	}
	
}