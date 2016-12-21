package com.triste.codesearch.parse;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import com.triste.codesearch.maps.Maps;
import com.triste.codesearch.node.ProjectNode;
import com.triste.codesearch.parse.Visitor;

public class Requestor extends FileASTRequestor {
	private static Visitor visitor;
	
	public Requestor() {
	}
	/*
	public void acceptAST(String sourceFilePath, CompilationUnit ast){
		Maps.currentFilePath=sourceFilePath;
		System.out.println(sourceFilePath);
		visitor = new Visitor();
		ast.accept(visitor);
		super.acceptAST(sourceFilePath, ast);
		System.out.println(sourceFilePath);
	}*/
	
	public Requestor(String projectName, String projectPath) {

		//System.out.println("¡¾Project£º" + projectName + "¡¿" + projectPath);
	}
	
	public void acceptAST(String sourceFilePath, CompilationUnit ast){
 		Maps.currentFilePath=sourceFilePath;
		//System.out.println(sourceFilePath);
		visitor = new Visitor();
		ast.accept(visitor);
		super.acceptAST(sourceFilePath, ast);
		//System.out.println(sourceFilePath);
	}
}
