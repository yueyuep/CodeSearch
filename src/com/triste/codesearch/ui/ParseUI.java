package com.triste.codesearch.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.triste.codesearch.parse.ParseMain;

public class ParseUI extends JFrame{
	
	//JButton btn_file=new JButton("文件");
	JButton btn_parse=new JButton("解压文件");
	JButton btn_pagerank=new JButton("计算pagerank值");
	public JTextField text_state=new JTextField("");//正在解析...
	public JTextField text_current_project=new JTextField("");//当前解析项目:
	public JTextField text_current_file=new JTextField("");//当前解析文件:
	public JTextField text_info1=new JTextField("");
	public JTextField text_info=new JTextField("");
	JButton btn_start=new JButton("开始解析");
	JButton btn_stop=new JButton("暂停解析");
	JButton btn_exit=new JButton("正常退出");
	JButton btn_shutdowm=new JButton("强制关闭");
	JPanel panel=new JPanel();
	JPanel parse_panel=new JPanel();
	public static ParseUI ui = null;
	
	public static ParseUI getUI(){
		if(ui == null)
			ui = new ParseUI();
		
		return ui;
	}
	
	
	private ParseUI(){
        GridBagLayout gb = new GridBagLayout();
        
        GridBagConstraints s = new GridBagConstraints();
        this.setLayout(gb);
        
        s.fill=GridBagConstraints.BOTH;
        
        /*s.gridwidth=1;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个 
        s.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间 
        s.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间 
        gb.setConstraints(btn_file, s);
        this.add(btn_file);*/
        
        s.gridwidth=1; 
        s.weightx = 0; 
        s.weighty=0;
        gb.setConstraints(btn_parse, s);
        this.add(btn_parse);
        
        s.gridwidth=0; 
        s.weightx = 0; 
        s.weighty=0;
        gb.setConstraints(panel, s);
        this.add(panel);
        
        s.gridwidth=0; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(text_state, s);
        this.add(text_state);
        
        s.gridwidth=0; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(text_current_project, s);
        this.add(text_current_project);
        
        s.gridwidth=0; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(text_current_file, s);
        this.add(text_current_file);
        
        s.gridwidth=0; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(text_info1, s);
        this.add(text_info1);
        
        s.gridwidth=0; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(text_info, s);
        this.add(text_info);
        
  
        
        s.gridwidth=2; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(btn_start, s);
        this.add(btn_start);
        
        /*JPanel j2=new JPanel();
        s.gridwidth=1; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(j2, s);
        this.add(j2);*/
        
        s.gridwidth=2; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(btn_stop, s);
        this.add(btn_stop);
        
        /*JPanel j3=new JPanel();
        s.gridwidth=1; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(j3, s);
        this.add(j3);*/
        
        s.gridwidth=1; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(btn_exit, s);
        this.add(btn_exit);
        
        s.gridwidth=0; 
        s.weightx = 1; 
        s.weighty=0; 
        gb.setConstraints(btn_shutdowm, s);
        this.add(btn_shutdowm);
        
        s.gridwidth=0; 
        s.weightx = 0; 
        s.weighty=0;
        gb.setConstraints(btn_pagerank, s);
        this.add(btn_pagerank);
        
        initFunction();
	}
	
	public void initFunction(){
		/*btn_file.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//ParseMain.initAllProjects();
			}
		});*/
		
		btn_parse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				String []s = null;
				ParseMain.main(s,ui);
				
			}
		});
		
		btn_start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				ParseMain.ifStartFlag=true;
				text_state.setText("正在解析...");
			}
		});
		
		
		btn_stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				ParseMain.ifStartFlag=false;
				text_state.setText("正在暂停，请稍后...");
			}
		});
		
		btn_exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				ParseMain.ifexit=true;
			}
		});
		
		btn_shutdowm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
			ParseMain.shutdown();
				
			System.out.println("-------------------------【【【【end】】】】------------------------------");
			}
		});
		
		btn_pagerank.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				
			}
		});
		
		btn_pagerank.setVisible(false);
		
	}
	
	public void setPagerankBtn(){
		btn_pagerank.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		ParseUI ui=getUI();//new ParseUI();
		//ui.setTitle("ParseUI");
		//ui.pack();
		ui.setSize(500, 226);
		//ui.setSize(600, 220);
		ui.setVisible(true);
		//ui.setLocationRelativeTo(null);
	}

}
