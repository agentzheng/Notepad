package com.elliott.notepad;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.undo.UndoManager;


public class MainFrame extends JFrame{
 
	private static final long serialVersionUID = 1L;
	//set the default file name as no title
	public String fileName = "无标题";
	private Controller con;
	private JScrollPane sp = new JScrollPane();
	public JTextArea body = new JTextArea();
	//use for paste function
	public JMenuItem pasteItem; //粘贴功能
	//use for mouse right click menu
	private JPopupMenu popMenu = new JPopupMenu();     //右击鼠标弹出的菜单
	//use for auto wrap
	public JCheckBoxMenuItem lineCheckItem = new JCheckBoxMenuItem (); //自动换行选项
	//use for choose font
	public JMenuItem fontItem = new JMenuItem(); //字体选项
	//use for undo function
	public UndoManager undoMgr = new UndoManager(); //撤销管理器
	//use clipboard to temporary storage of information
	public Clipboard clipboard = null; //剪贴板
	
	public void setController(Controller con){
		this.con = con;
	}

	//相关变量
	int start=0;//查找开始位置
	int end=0;//查找结束位置

	//初始化
	//initialization
	public void init(){
		//设置ICON
		java.net.URL imgURL = MainFrame.class.getResource("icon.png");
		ImageIcon imgIcon = new ImageIcon(imgURL);
		Image img = imgIcon.getImage();
		this.setIconImage(img);

		//设置默认字体
		body.setFont(new Font("微软雅黑", Font.PLAIN, 18));

		//set application title as file name plus simple notepad
		this.setTitle(fileName+" - 记事本");
		this.setSize(1500,800);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				//overwrite close function
				con.exit(); //重写默认关闭按钮		
			}

		});
		
		//菜单栏
		//menu bar
		JMenuBar mb = new JMenuBar();		
		mb.setBackground(Color.white);
		mb.setFont(new Font("微软雅黑", Font.PLAIN, 15));

		//定义“文件”菜单，包含新建，打开，保存，另存为，退出功能
		//define some functions in file menu
		JMenu fileMenu = new JMenu();
		fileMenu.setText("文件(F)");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		//新建
		//new file function
		JMenuItem newItem = new JMenuItem();
		newItem.setMnemonic(KeyEvent.VK_N);
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
		newItem.setText("\t新建\t(N)");
		newItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.createFile();
			}
			
		});
		
		//打开
		//open file function
		JMenuItem openItem = new JMenuItem();
		openItem.setMnemonic(KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		openItem.setText("\t打开\t(O)...");
		openItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.openFile();
			}
			
		});
		
		//保存
		//save file function
		JMenuItem saveItem = new JMenuItem();
		saveItem.setText("\t保存\t(S)");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		//saveItem.setEnabled(false);
		saveItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.saveFile();
			}
			
		});
		
		//另存为
		//save as file function
		JMenuItem saveForItem = new JMenuItem();
		saveForItem.setMnemonic(KeyEvent.VK_A);
		saveForItem.setText("\t另存为\t(A)...");
		saveForItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.saveForFile();
			}
			
		});
		
		//退出
		//exit function
		JMenuItem exitItem = new JMenuItem();
		exitItem.setText("\t退出\t(X)");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.exit();
			}
			
		});
		
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveForItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);		
		mb.add(fileMenu);
		
		//定义“编辑”菜单，包含剪切，复制，粘贴，全选，撤销功能
		//define some functions into edit menu
		JMenu editMenu = new JMenu();
		editMenu.setText("\t编辑(E)");
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		//剪切
		//cut function
		final JMenuItem cutItem = new JMenuItem();
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
		cutItem.setText("\t剪切");
		cutItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.cut();				
			}
			
		});
				
		//复制
		//copy function
		final JMenuItem copyItem = new JMenuItem();
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
		copyItem.setText("\t复制");
		copyItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.copy();
			}
			
		});
		
		//粘贴
		//paste function
		pasteItem = new JMenuItem();
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
		pasteItem.setText("\t粘贴");
		pasteItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.paste();
			}
			
		});
		
		//全选
		//select all function
		JMenuItem selectAllItem = new JMenuItem();
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK));
		selectAllItem.setText("\t全选");

		selectAllItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.selectAll();
			}
			
		});
		
		//撤销
		//undo function
		JMenuItem rollbackItem = new JMenuItem();
		rollbackItem.setMnemonic(KeyEvent.VK_U);
		rollbackItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));

		rollbackItem.setText("\t撤销(U)");
		rollbackItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.rollback();
			}
			
		});

		//查找
		//find function
		JMenuItem findItem = new JMenuItem("查找");
		findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
		findItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				find(body.getSelectedText());
			}

		});

		//替换
		//replace function
		JMenuItem replaceItem = new JMenuItem("替换");
		replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK));
		replaceItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				replace(body.getSelectedText());
			}

		});




		editMenu.add(rollbackItem);
		editMenu.addSeparator();
		editMenu.add(cutItem);
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.addSeparator();
		editMenu.add(findItem);
		editMenu.add(replaceItem);
		editMenu.add(selectAllItem);

		editMenu.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(body.getText()==""){
					cutItem.setEnabled(false);
					copyItem.setEnabled(false);
				}else{
					cutItem.setEnabled(true);
					copyItem.setEnabled(true);
				}
				
				if(clipboard.getContents(this)==null){
					pasteItem.setEnabled(false);
				}else{
					pasteItem.setEnabled(true);
				}
			}
			
		});
		mb.add(editMenu);
		
		//格式菜单，具有自动换行可选和字体设置功能
		//define some functions into data format menu
		JMenu formatMenu = new JMenu();
		formatMenu.setText("格式(O)");
		formatMenu.setMnemonic(KeyEvent.VK_O);
		//自动换行
		//auto wrap
		lineCheckItem.setState(true);

		lineCheckItem.setMnemonic(KeyEvent.VK_W);
		lineCheckItem.setText("自动换行(W)");
		lineCheckItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				body.setLineWrap(lineCheckItem.getState());
			}
			
		});

		fontItem = new JMenuItem("\t字体(F)");
		fontItem.setMnemonic(KeyEvent.VK_F);

		fontItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFontChooser one = new JFontChooser(body.getFont(), body.getForeground());
				one.showDialog(null,500,200);
				//获取选择的字体
				Font font=one.getSelectedfont();

				if(font!=null){
					body.setFont(font);
				}
			}

		});

		formatMenu.add(lineCheckItem);
		formatMenu.add(fontItem);
		mb.add(formatMenu);


		//查看 菜单
		JMenu viewMenu = new JMenu("查看(V)");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		JMenuItem statusItem=new JMenuItem("\t状态栏(S)");
		statusItem.setEnabled(false);

		viewMenu.add(statusItem);
		mb.add(viewMenu);

		//关于菜单
		//define a menu to show mainPgae information
		JMenu helpMenu = new JMenu();
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.setText("帮助(H)");
		JMenuItem mainPageItem = new JMenuItem("\t查看项目主页");
		JMenuItem aboutItem = new JMenuItem("\t关于记事本");
		mainPageItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.mainPgae();
			}
			
		});

		aboutItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.about();
			}

		});

		helpMenu.add(mainPageItem);
		helpMenu.add(aboutItem);
		mb.add(helpMenu);
		
		//添加菜单栏
		this.setJMenuBar(mb);
		
		//剪切
		final JMenuItem cutItem2 = new JMenuItem();
		cutItem2.setText("剪切");
		cutItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.cut();				
			}
			
		});
				
		//复制
		//copy function for mouse right click menu
		final JMenuItem copyItem2 = new JMenuItem();
		copyItem2.setText("复制");
		copyItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.copy();
			}
			
		});
		
		//粘贴
		//paste function for mouse right click menu
		final JMenuItem pasteItem2 = new JMenuItem();
		pasteItem2.setText("粘贴");
		pasteItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.paste();
			}
			
		});
		
		//全选
		//select all function for mouse right click menu
		JMenuItem selectAllItem2 = new JMenuItem();
		selectAllItem2.setText("全选");
		selectAllItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.selectAll();
			}
			
		});
		
		//撤销
		//undo function for mouse right click menu
		final JMenuItem rollbackItem2 = new JMenuItem();
		rollbackItem2.setText("撤销");
		rollbackItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.rollback();
			}
			
		});

		//查找
		//find function for mouse right click menu
		final JMenuItem findItem2 = new JMenuItem("查找");
		findItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				find(body.getSelectedText());
			}

		});


		popMenu.add(rollbackItem2);
		popMenu.add(cutItem2);
		popMenu.add(copyItem2);
		popMenu.add(pasteItem2);
		popMenu.add(selectAllItem2);
		popMenu.add(findItem2);

		body.setLineWrap(true);
		body.addKeyListener(new KeyAdapter(){
			//只要按下键盘，文件就是被修改过
			//if the keyboard has been pressed,sign it as the file has been edited
			@Override
			public void keyTyped(KeyEvent e) {
				con.isEdited = true;				
			}
	
		});
		body.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				int getCode =e.getButton();
				//鼠标右击事件
				//the code for mouse right click action
			if(getCode==3){				
					//如果文本区域没有内容，不能剪切和复制
					//if content has nothing,we can't cut or copy
					if(body.getText()==""){
						cutItem2.setEnabled(false);
						copyItem2.setEnabled(false);
					}else{
						cutItem2.setEnabled(true);
						copyItem2.setEnabled(true);
					}
					//如果剪贴板为空，不能粘贴
					//if the clipboard is empty,we can't paste
					if(clipboard.getContents(this)==null){
						pasteItem.setEnabled(false);
						pasteItem2.setEnabled(false);
					}else{
						
						pasteItem.setEnabled(true);
					}
					//显示位置在鼠标所在位置
					//show the popup menu near the mouse focus
					popMenu.setLocation(e.getXOnScreen(),e.getYOnScreen());
					popMenu.setVisible(true);
				}
				else{
					//隐藏
					//hide the popup menu
					popMenu.setVisible(false);
				}
			}
		});
		//添加撤销管理器
		//add undo manager to the frame
		body.getDocument().addUndoableEditListener(undoMgr);

		//设置滚动条一直存在，像windows 的notepad 一样
		sp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportView(body);

		this.add(sp);

		//get the system's clipboard
		clipboard = getToolkit().getSystemClipboard();//获取系统剪贴板

	}

	public void find(String str)
	{
		//查找对话框
		JDialog search=new JDialog(this,"查找");
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

	public void replace(String str)
	{

		//替换对话框
		JDialog search=new JDialog(this,"替换");
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
                if(body.getSelectedText()==null) {
                    myFind(matchCheckBox.isSelected(),findText.getText(),true);
                }
                else{
                    body.replaceSelection(replaceText.getText());
                    myFind(matchCheckBox.isSelected(),findText.getText(),true);
                }
        }
        });//"替换"按钮监听结束

        //"全部替换"按钮监听
        replaceAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String replaced=body.getText().replace(findText.getText(),replaceText.getText());
                body.setText(replaced);
                body.setCaretPosition(0);
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
        source=body.getText();


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
            if(body.getSelectedText()==null)
                k=strA.lastIndexOf(strB,body.getCaretPosition()-1);
            else
                k=strA.lastIndexOf(strB, body.getCaretPosition()-findText.length()-1);
            if(k>-1) {   //String strData=strA.subString(k,strB.getText().length()+1);
                body.setCaretPosition(k);
                body.select(k,k+strB.length());
            }
            else {
                JOptionPane.showMessageDialog(null,"找不到您查找的内容！","查找",JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else {
            if(body.getSelectedText()==null)
                k=strA.indexOf(strB,body.getCaretPosition()+1);
            else {
                k = strA.indexOf(strB, body.getCaretPosition() - findText.length() + 1);
            }
            if(k>-1) {   //String strData=strA.subString(k,strB.getText().length()+1);
                body.setCaretPosition(k);
                body.select(k,k+strB.length());
            }
            else {
                JOptionPane.showMessageDialog(null,"找不到您查找的内容！","查找",JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }



	public MainFrame(){
		init();
	}

    public static void main(String[] args) {

        MainFrame mainFrame = new MainFrame();
        Controller controller = new Controller();

        mainFrame.setController(controller);
        controller.setMainFrame(mainFrame);

        mainFrame.setVisible(true);
    }
}
