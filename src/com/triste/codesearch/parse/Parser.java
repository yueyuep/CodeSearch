package com.triste.codesearch.parse;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import com.triste.codesearch.maps.Maps;
import com.triste.codesearch.node.ProjectNode;
import com.triste.codesearch.parse.Requestor;

//Create ASTs
public class Parser {
	
	private Requestor requestor;
	
	//Parameters used in setEnvioronment
	private String[] classpathEntries;
	private String[] sourcepathEntries;
	
	//Parameters used in createASTs
	private String[] sourceFilePaths;
	
	public Parser(String projectName,
			String projectPath,
			Collection<String> classpathEntries,
			String[] sourcepathEntries,
			Collection<String> sourceFilePaths){
		ProjectNode p=new ProjectNode(projectName, projectPath);
		p.setParseState(true);
		Visitor.map.addProject(p);
		
		requestor = new Requestor(projectName, projectPath);
		this.classpathEntries = classpathEntries.toArray(
				new String[classpathEntries.size()]);
		this.sourcepathEntries = sourcepathEntries;
		this.sourceFilePaths = sourceFilePaths.toArray(
				new String[sourceFilePaths.size()]);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setEnvironment(this.classpathEntries,
				this.sourcepathEntries, 
				null, true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
//		System.out.println(this.sourceFilePaths[0]);
		if(parser!=null)
			parser.createASTs(this.sourceFilePaths, null, new String[0], requestor, null);
	}
	
/*	public Parser(Collection<String> classpathEntries,
			String[] sourcepathEntries,
			Collection<String> sourceFilePaths){
		
		ProjectNode p=new ProjectNode(ASTMain.projectPath, Maps.currentProjectPath);
		p.setParseState(true);
		Visitor.map.addProject(p);
		
		
		requestor = new Requestor();
		this.classpathEntries = classpathEntries.toArray(
				new String[classpathEntries.size()]);
		this.sourcepathEntries = sourcepathEntries;
		this.sourceFilePaths = sourceFilePaths.toArray(
				new String[sourceFilePaths.size()]);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setEnvironment(this.classpathEntries,
				this.sourcepathEntries, 
				null, true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
//		System.out.println(this.sourceFilePaths[0]);
		parser.createASTs(this.sourceFilePaths, null, new String[0], requestor, null);
	}*/
}
