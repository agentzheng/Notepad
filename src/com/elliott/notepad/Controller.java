package com.elliott.notepad;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.*;

public class Controller {

	private MainFrame mainFrame;
	//this sign for if the file has been edited
	public boolean isEdited = false;//是否已经被修改过
	//file path which now is been reading 
	public File filePath = null;//读写文件路径
	//file chooser dialog
	public static JFileChooser file = new JFileChooser();;//文件对话框
	
	public  void setMainFrame(MainFrame mf){
		this.mainFrame = mf;
	}
	//新建文件
	//create new file
	public void createFile(){
		if(!isEdited){
			mainFrame.rollbackItem.setEnabled(false);
			mainFrame.body.setText("");
		}else{
			//获取用户点击的信息
			//get info for what has the user choosen
			//ask for if the file has not been saved,just to save it then create a new one
			int choose = JOptionPane.showConfirmDialog(null, "是否将更改保存到 无标题？");
			//if choose yes,save the file then create a new one
			if(choose==JOptionPane.YES_OPTION){ //点击是
				saveFile();
				mainFrame.body.setText("");
			//if choose no,not to save the file but create a new one
			}else if(choose==JOptionPane.NO_OPTION){//点击否
				mainFrame.body.setText("");
			}
		}
		
		//更新标题
		//upadate the application for title
		mainFrame.fileName="无标题";
		updateTitle();
		isEdited = false;
		
	}
	//打开文件
	//open file 
	public void openFile(){
		if(!isEdited){
			executeOpen();
		}else{
			//获取用户点击的信息
			//ask for if the file has not been saved,just to saved it then to open a file
			int choose = JOptionPane.showConfirmDialog(null, "您还没有保存，" +
					"是否保存再打开其他文件？");
			//if choose yes,save the file then onpen a new one
			if(choose==JOptionPane.YES_OPTION){ //点击是
				saveFile();
				isEdited = false;
				executeOpen();
			//if choose no,donnot save the file but open a new one
			}else if(choose==JOptionPane.NO_OPTION){//点击否
				executeOpen();				
			}
		}
		
	}
	//执行读取文件
	//this is a method to execute to open a file
	public void executeOpen(){
		//打开文件对话框
		//show open file dialog
		int value = file.showOpenDialog(mainFrame);
		//判断是否点击了打开
		//judge if user choose open option
		if(value == JFileChooser.APPROVE_OPTION){
			//获取用户所选的文件
			//get which file has been choosen
			filePath = file.getSelectedFile();
			//判断文件是否存在,如果存在则读取文件
			//judge the file if it's  existed
			if(filePath.exists()){
				//把原来的内容清空
				//clear the mainframe body then read the file show it in the body
				mainFrame.body.setText("");
				mainFrame.fileName = filePath.getName();
				readFile();
			//if the file is not existed
			}else{//如果文件不存在
				//show the dialog to say the file is not existed
				JOptionPane.showMessageDialog(null, "您选择的文件不存在，请检查正确！");
				file.showOpenDialog(mainFrame);
			}
		}		
	}
	
	//读取文件
	//read the file with code utf-8
	public void readFile(){
		//逐行读取		
		BufferedReader br;
		try {
			FileInputStream fs = new FileInputStream(filePath);
			InputStreamReader inReader = new InputStreamReader(fs,"UTF-8");
			br = new BufferedReader(inReader);
			String str;
			while((str=br.readLine())!=null){
				mainFrame.body.append(str+"\n");
			}
			mainFrame.body.setCaretPosition(0); //将光标放在最前面
			br.close();
			//update the title for application frame
			updateTitle();//更新标题
			isEdited = false;							
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 				
	}
	
	//保存文件
	//save the file
	public void saveFile(){

		//判断选择的文件是否存在
		//judge the file has been choosen is existed or not
		//if the file is existed
		if(filePath !=null){ //如果存在
			writeFile();
			isEdited = false;
		//if the file is not existed,save it as a new one
		}else{ //文件路为空则是用户要新建的
			saveForFile();
		}
	}
	
	//另存为文件
	//save as method
	public void saveForFile(){
		int value = file.showSaveDialog(mainFrame);
		if(value == JFileChooser.APPROVE_OPTION){
			filePath = file.getSelectedFile();
			mainFrame.fileName = filePath.getName();
			//judge the file if it's existed
			//判断文件是否已经存在
			if(filePath.exists()){
				//show the dialog to ask when the file is existed then overcover it.
				if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(mainFrame,
						"您设置的文件已经存在，是否要覆盖它？")){
					writeFile();
					isEdited = false;
					//再把该文件加载进来
					//read the file again
					readFile();
				}
			}else{
				writeFile();
				isEdited = false;
			}
			
			updateTitle();//更新标题
			
		}		
	}
		
	//写入文件
	//write the new things to a new file with code utf-8
	public void writeFile(){
		
		PrintWriter pw;
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
			pw = new PrintWriter(osw);
			pw.println(mainFrame.body.getText());
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//剪切
	//cut function
	public void cut(){
		copy();
		//标记开始位置
		//sign the start position
		int start = mainFrame.body.getSelectionStart();
		//标记结束位置
		//sign the end position
		int end = mainFrame.body.getSelectionEnd();
		//删除所选段
		//delete the content from start position to end position
		mainFrame.body.replaceRange("", start, end);
		
	}
	
	//复制
	//copy method
	public void copy(){
		//拖动选取文本
		//temp string to save the content has been selected by using the mouse
		String temp = mainFrame.body.getSelectedText();
		//把获取的内容复制到连续字符器，这个类继承了剪贴板接口
		//put temp string to StringSelection object which is a class implements clipboard
		StringSelection text = new StringSelection(temp);
		//把内容放在剪贴板
		//put the StringSelection object to clipboard
		mainFrame.clipboard.setContents(text, null);
	}
	
	//粘贴
	//paste method
	public void paste(){
		
		//Transferable，把剪贴板的内容转换成数据
		//use Transferable object to make the content of clipboard into data
		Transferable contents = mainFrame.clipboard.getContents(this);
		//DataFalvor
		DataFlavor flavor = DataFlavor.stringFlavor;
		//如果可以转换
		//if can be converted
		if(contents.isDataFlavorSupported(flavor)){
			String str;
			try {//开始转换
				//start to convert
				str=(String)contents.getTransferData(flavor);
				//如果要粘贴时，鼠标已经选中了一些字符
				//if paste when the mouse has selected some string things
				if(mainFrame.body.getSelectedText()!=null){
					//定位被选中字符的开始位置
					//sign the selecting start position
					int start = mainFrame.body.getSelectionStart();
					//定位被选中字符的末尾位置
					//sign the selecting end position
					int end = mainFrame.body.getSelectionEnd();
					//把粘贴的内容替换成被选中的内容
					//paste the real string things between the selecting start and end position
					mainFrame.body.replaceRange(str, start, end);

				}else{
					//获取鼠标所在TextArea的位置
					//get the mouse focus position in TextArea
					int mouse = mainFrame.body.getCaretPosition();
					//在鼠标所在的位置粘贴内容
					//paste the real things to the position
					mainFrame.body.insert(str, mouse);
				}
				
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch(IllegalArgumentException e){
				e.printStackTrace();
			}
		}
		
		
	}
	
	//全选
	//select all method
	public void selectAll(){
		//选中全部内容
		//select all content
		mainFrame.body.selectAll();
	}
	
	//撤销
	//undo method
	public void rollback(){
		if(mainFrame.undoMgr.isSignificant()){
			try {
				mainFrame.undoMgr.undo();
			}catch(Exception e) {
				mainFrame.rollbackItem.setEnabled(false);
			}
		}
	}

	public void find(String str)
	{
		//查找对话框
		JDialog search=new JDialog(mainFrame,"查找");
		search.setSize(500, 200);
		search.setLocation(450,350);
		JLabel label_1=new JLabel("查找内容:");

		final JTextField textField_1=new JTextField(5);
		textField_1.setText(str);
		JButton findBtn=new JButton("查找下一个");

		JButton cancelBtn=new JButton("取消");
		JPanel panel=new JPanel(null);

		final JCheckBox matchCheckBox=new JCheckBox("区分大小写(C)");

		ButtonGroup bGroup=new ButtonGroup();
		final JRadioButton upButton=new JRadioButton("向上(U)");
		final JRadioButton downButton=new JRadioButton("向下(U)");
		downButton.setSelected(true);
		bGroup.add(upButton);
		bGroup.add(downButton);

		JPanel directionPanel=new JPanel();
		directionPanel.setBorder(BorderFactory.createTitledBorder("方向"));
		//设置directionPanel组件的边框;
		//BorderFactory.createTitledBorder(String title)创建一个新标题边框，使用默认边框（浮雕化）、默认文本位置（位于顶线上）、默认调整 (leading) 以及由当前外观确定的默认字体和文本颜色，并指定了标题文本。


		label_1.setBounds(10,30,80,30);


		textField_1.setBounds(label_1.getX()+ label_1.getWidth()+5,label_1.getY(),240,label_1.getHeight());

		//查找下一个按钮
		findBtn.setBounds(textField_1.getX()+textField_1.getWidth()+30,label_1.getY(),120,30);
		cancelBtn.setBounds(findBtn.getX(),findBtn.getY()+findBtn.getHeight()+20,findBtn.getWidth(),findBtn.getHeight());

		matchCheckBox.setBounds(label_1.getX(),textField_1.getY()+textField_1.getHeight()+30,120,60);
		directionPanel.setLayout(new GridLayout(1,2));
		directionPanel.setBounds(matchCheckBox.getX()+matchCheckBox.getWidth(),matchCheckBox.getY(),200,60);
		directionPanel.add(upButton);
		directionPanel.add(downButton);





		panel.add(label_1);
		panel.add(textField_1);

		panel.add(findBtn);
		panel.add(cancelBtn);


		panel.add(matchCheckBox);
		panel.add(directionPanel);

		search.add(panel);

		search.setVisible(true);
		search.setResizable(false);


		//为查找下一个 按钮绑定监听事件
		findBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				boolean isDown=downButton.isSelected();
				myFind(matchCheckBox.isSelected(),textField_1.getText(),isDown);
			}
		});


		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				search.dispose();
			}
		});
	}


	public void replace(String str){
		//替换对话框
		JDialog search=new JDialog(mainFrame,"替换");
		search.setSize(500, 250);
		search.setLocation(450,350);
		JLabel label_1=new JLabel("查找内容:");
		JLabel label_2=new JLabel("替换为:");
		final JTextField findText=new JTextField(5);
		findText.setText(str);
		final JTextField replaceText=new JTextField(5);
		JButton findBtn=new JButton("查找下一个");
		JButton replaceBtn=new JButton("替换");
		JButton replaceAllBtn=new JButton("替换全部");
		JButton cancelBtn=new JButton("取消");
		JPanel panel=new JPanel(null);

		label_1.setBounds(10,30,80,30);
		label_2.setBounds(label_1.getX(),label_1.getY()+label_1.getHeight()+5,label_1.getWidth(),label_1.getHeight());

		findText.setBounds(label_1.getX()+ label_1.getWidth()+5,label_1.getY(),220,label_1.getHeight());
		replaceText.setBounds(label_2.getX()+label_2.getWidth()+5,label_2.getY(),findText.getWidth(),findText.getHeight());

		findBtn.setBounds(findText.getX()+findText.getWidth()+10,label_1.getY(),120,30);
		replaceBtn.setBounds(findBtn.getX(),findBtn.getY()+findBtn.getHeight()+5,findBtn.getWidth(),findBtn.getHeight());
		replaceAllBtn.setBounds(findBtn.getX(),replaceBtn.getY()+replaceBtn.getHeight()+5,findBtn.getWidth(),findBtn.getHeight());
		cancelBtn.setBounds(findBtn.getX(),replaceAllBtn.getY()+replaceAllBtn.getHeight()+5,findBtn.getWidth(),findBtn.getHeight());


		final JCheckBox matchCheckBox=new JCheckBox("区分大小写(C)");
		matchCheckBox.setBounds(label_1.getX(),replaceText.getY()+replaceText.getHeight()+30,120,60);

		panel.add(label_1);
		panel.add(findText);
		panel.add(label_2);
		panel.add(replaceText);

		panel.add(findBtn);
		panel.add(replaceBtn);
		panel.add(replaceAllBtn);
		panel.add(cancelBtn);


		panel.add(matchCheckBox);

		panel.setVisible(true);


		search.add(panel);
		search.setVisible(true);
		search.setResizable(false);




		//为查找下一个 按钮绑定监听事件

		findBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				myFind(matchCheckBox.isSelected(),findText.getText(),true);
			}
		});

		//"替换"按钮监听
		replaceBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(mainFrame.body.getSelectedText()==null) {
					myFind(matchCheckBox.isSelected(),findText.getText(),true);
				}
				else{
					mainFrame.body.replaceSelection(replaceText.getText());
					myFind(matchCheckBox.isSelected(),findText.getText(),true);
				}
			}
		});//"替换"按钮监听结束

		//"全部替换"按钮监听
		replaceAllBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String replaced=mainFrame.body.getText().replace(findText.getText(),replaceText.getText());
				mainFrame.body.setText(replaced);
				mainFrame.body.setCaretPosition(0);
			}//while循环结束

		});//"替换全部"方法结束


		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				search.dispose();
			}
		});
	}

	void myFind(boolean matchCase, String findText, boolean isDown)
	{
		int k=0,m=0;
		final String source,strA,strB;
		source=mainFrame.body.getText();


		//"区分大小写(C)"的JCheckBox是否被选中
		if(matchCase) {//区分大小写
			strA=source;
			strB=findText;
		}
		else{ //不区分大小写,此时把所选内容全部化成大写(或小写)，以便于查找
			strA=source.toUpperCase();
			strB=findText.toUpperCase();
		}


		if(!isDown)
		{
			if(mainFrame.body.getSelectedText()==null)
				k=strA.lastIndexOf(strB,mainFrame.body.getCaretPosition()-1);
			else
				k=strA.lastIndexOf(strB, mainFrame.body.getCaretPosition()-findText.length()-1);
			if(k>-1) {   //String strData=strA.subString(k,strB.getText().length()+1);
				mainFrame.body.setCaretPosition(k);
				mainFrame.body.select(k,k+strB.length());
			}
			else {
				JOptionPane.showMessageDialog(null,"找不到您查找的内容！","查找",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else {
			if(mainFrame.body.getSelectedText()==null)
				k=strA.indexOf(strB,mainFrame.body.getCaretPosition()+1);
			else {
				k = strA.indexOf(strB, mainFrame.body.getCaretPosition() - findText.length() + 1);
			}
			if(k>-1) {   //String strData=strA.subString(k,strB.getText().length()+1);
				mainFrame.body.setCaretPosition(k);
				mainFrame.body.select(k,k+strB.length());
			}
			else {
				JOptionPane.showMessageDialog(null,"找不到您查找的内容！","查找",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}



	
	//关于
	//mainPgae
	public void mainPgae(){
		try {
			//String url = "http://www.baidu.com";
			String url = "https://github.com/agentzheng/Notepad";
			java.net.URI uri = java.net.URI.create(url);
			// 获取当前系统桌面扩展
			java.awt.Desktop dp = java.awt.Desktop.getDesktop();
			// 判断系统桌面是否支持要执行的功能
			if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
				dp.browse(uri);// 获取系统默认浏览器打开链接
			}
		} catch (java.lang.NullPointerException e) {
			// 此为uri为空时抛出异常
			e.printStackTrace();
		} catch (java.io.IOException e) {
			// 此为无法获取系统默认浏览器
			e.printStackTrace();
		}
	}

	public void about(){
//		//show the dialog for introduce the copyright of this appliaction
		JOptionPane.showMessageDialog(null,
				"记事本\nCopyright2018 Elliott Zheng\nEmail：admin@hypercube.top");

	}

	//退出
	//exit method
	public void exit(){
		if(!isEdited){
			System.exit(0);
		}else{
			//获取用户点击的信息
			//if the file has not been save,ask for it then exit
			int choose = JOptionPane.showConfirmDialog(null, "您还没有保存，" +
					"是否保存再退出？");
			//if choose yes,save the file then exit
			if(choose==JOptionPane.YES_OPTION){ //点击是
				saveFile();
				System.exit(0);
			//if choose no,just exit
			}else if(choose==JOptionPane.NO_OPTION){//点击否
				System.exit(0);
			}
		}
	}


	//更新标题
	//update the title for application mainframe
	public void updateTitle(){
		//like simple notepad
		mainFrame.setTitle(mainFrame.fileName+"- 记事本");
	}
	
	//在构造函数中设置file的默认选项
	//make the default saving file path to desktop
	public Controller(){
		File path = new File(file.getCurrentDirectory().getParent()+"\\Desktop\\");
		file.setCurrentDirectory(path);
	}
}
