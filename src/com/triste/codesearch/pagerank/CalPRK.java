package com.triste.codesearch.pagerank;

public class CalPRK {
	
	static Database d;

	public CalPRK(){}
	
	public static void CalculatePRK(){
		d = new Database();
		d.initDB();
		System.out.println("----------begin cal method------------");
		CalMethodPRK m = new CalMethodPRK();
		m.setPK();
		System.out.println("----------end cal method---------");
/*		System.out.println("----------begin cal class------------");
		CalClassPRK c = new CalClassPRK();
		c.setPK();
		System.out.println("----------end cal class---------");*/
	}
	
	public static void main(String[] args){
		CalPRK.CalculatePRK();
		CalPRK.d.shutdown();
		System.out.println("----------shutdown---------");
	}
	
}
