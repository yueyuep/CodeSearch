package com.triste.codesearch.ui;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.triste.codesearch.parse.ASTMain;

import com.triste.codesearch.util.ProNameUtil;

public class ParserControllerUI extends JFrame{
	private static int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	private int width;
	private int height;
	private JButton btn_start;
	private JButton btn_stop;
	public static JTextField info;
	private JPanel panel;
	
	public ParserControllerUI(){
		
		btn_start = new JButton("开始解析");
		btn_start.setBounds(25, 70, 100, 30);
		btn_start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		btn_stop = new JButton("暂停解析");
		btn_stop.setBounds(140, 70, 100, 30);
		btn_stop.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		info = new JTextField();
		info.setBounds(25, 20, 340, 30);
		info.setEditable(false);
		
		panel = new JPanel();
		panel.setLayout(null);
		panel.add(btn_start);
		panel.add(btn_stop);
		panel.add(info);
		getContentPane().add(panel);
		
		width = 400;
		height = 150;
		setSize(width, height);
		setBounds((screenWidth - width) / 2, 
				(screenHeight - height) / 2, 
				width, height);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				super.windowClosing(e);
				try {
					ProNameUtil.saveProName(ASTMain.allProNameData);
					//ProNameUtil.saveProName(ASTMain.allProNameData);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btn_start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				info.setText("解析中~~~~~~~");
				ASTMain.IS_PARSING = "YES";
			}
		});
		
		btn_stop.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				info.setText("正在准备暂停解析，请勿关闭...");
				ASTMain.IS_PARSING = "NO";
			}
		});
	}
}
