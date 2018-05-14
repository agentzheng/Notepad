package elliott;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;

public class MainFrame extends JFrame{
	//设置组件

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu helpMenu;
	private JTextArea jTextArea;
	private JScrollPane jScrollPane;
	private JMenuItem newItem,openItem, closeItem, saveItem,saveAsItem;
	private JMenuItem undoItem,cutItem,copyItem,pasteItem,delItem,findItem,findNextItem,replaceItem,turnToItem,selectAllItem,timeDateItem;
	private JMenuItem aboutItem;
	private FileDialog open,save;
	private File file;  
	
	MainFrame() {
		Init();
	}
	
	public void Init(){
		JFrame frame = new JFrame("Notepad");
		frame.setBounds(300, 300, 700, 450);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();//菜单栏
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		helpMenu = new JMenu("Help");
		jTextArea = new JTextArea(10, 40);
		Font x = new Font("Monospaced",1,20);
		
		jTextArea.setFont(x);
		jTextArea.setLineWrap(true);//到达指定宽度则换行
		//应当首先利用构造函数指定JScrollPane的控制对象，此处为JTextArea，然后再讲JScrollPane
		//添加进面板
		jScrollPane = new JScrollPane(jTextArea);
		//设置滚动条自动出现
		jScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setViewportView(jTextArea);


		newItem = new JMenuItem("New");
		openItem = new JMenuItem("Open");
		saveItem = new JMenuItem("Save");
		saveAsItem = new JMenuItem("Save as");
		closeItem = new JMenuItem("Close");





		undoItem=new JMenuItem("Undo");
		copyItem=new JMenuItem("Copy");
		cutItem=new JMenuItem("Cut");
		pasteItem=new JMenuItem("Paste");
		delItem=new JMenuItem("Del");
		findItem=new JMenuItem("Find");
		findNextItem=new JMenuItem("Find next");
		replaceItem=new JMenuItem("Replace");
		turnToItem=new JMenuItem("Turn to");
		selectAllItem=new JMenuItem("Select all");
		timeDateItem=new JMenuItem("Time/Date");


		aboutItem = new JMenuItem("mainPgae");
		//添加两个选项卡到JMenu
		//添加字菜单项到菜单项

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);


		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(closeItem);


		editMenu.add(undoItem);
		editMenu.add(cutItem);
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.add(delItem);
		editMenu.add(findItem);
		editMenu.add(findNextItem);
		editMenu.add(replaceItem);
		editMenu.add(turnToItem);
		editMenu.add(selectAllItem);
		editMenu.add(timeDateItem);


		helpMenu.add(aboutItem);
		//放置菜单项及输入框
		frame.add(menuBar, BorderLayout.NORTH);
		frame.add(jScrollPane, BorderLayout.CENTER);
		
		
		//添加对话框，参见API文档Dialog构造方法  ;
		//FileDialog 类显示一个对话框窗口，用户可以从中选择文件。 
		//由于它是一个模式对话框，当应用程序调用其 show 方法来显示对话框时，它将阻塞其余应用程序，直到用户选择一个文件。 
		open = new FileDialog(frame,"Open file",FileDialog.LOAD);
	    save = new FileDialog(frame,"Save file",FileDialog.SAVE);
		
	    addEvent();
	    frame.setVisible(true);
	}
	
	/*
	 * 组件添加事件
	 */	
	public void addEvent() {
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		aboutItem.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Author:Elliott Zheng");
			}
		});
		
		 openItem.addActionListener(new ActionListener()//菜单条目监听：打开  
	        {  
	            public void actionPerformed(ActionEvent e)  
	            {  
	                open.setVisible(true);  
	                String dirPath = open.getDirectory();//获取对话框目录；FileDialog类的方法  
	                String fileName= open.getFile();    //获取对话框选定文件  
	                if(dirPath==null || fileName==null) //点取消  
	                    return; 
	                
	                jTextArea.setText("");//打开文件之前清空文本区域  
	                
	                file = new File(dirPath,fileName);  
	                try  
	                {  
	                    BufferedReader br = new BufferedReader(new FileReader(file));  
	                    String line = null;  
	                    while ((line=br.readLine()) !=null)  
	                    {  
	                    	//将给定文本追加到文档结尾。如果模型为 null 或者字符串为 null 或空，则不执行任何操作。 
	                    	//虽然大多数 Swing 方法不是线程安全的，但此方法是线程安全的。
	                    	jTextArea.append(line+"\r\n");  
	                    }  
	                }  
	                catch (IOException ie){  
	                    throw new RuntimeException("读取失败！");  
	                }  
	            }  
	        });  
		 
	        saveItem.addActionListener(new ActionListener()//菜单条目监听：保存  
	        {     
	            public void actionPerformed(ActionEvent e)  
	            {  
	                if(file==null)  
	                {  
	                    save.setVisible(true);  
	                    String dirPath = save.getDirectory();  
	                    String fileName= save.getFile();  
	                    if(dirPath==null || fileName==null)  
	                        return;  
	                    file = new File(dirPath,fileName);                
	                }  
	                try  
	                {  
	                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));  
	                    String text = jTextArea.getText();  
	                    bw.write(text);  
	                    bw.close();  
	                }  
	                catch (IOException ex)  
	                {  
	                    throw new RuntimeException();  
	                }  
	            }  
	        });  
	}
	
	public static void main(String[] args){
		new MainFrame();
	}
}
