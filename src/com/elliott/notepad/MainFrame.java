package com.elliott.notepad;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.UndoManager;


public class MainFrame extends JFrame implements DocumentListener {
 
	private static final long serialVersionUID = 1L;
	//set the default file name as no title
	public String fileName = "无标题";
	private Controller controller;
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

    //创建和添加状态栏
	public JLabel statusLabel=new JLabel("第1行，第1列        ",SwingConstants.RIGHT);

	public void setController(Controller con){
		this.controller = con;
	}

    String pattern = "第{0}行，第{1}列        ";

    public JMenuItem rollbackItem = new JMenuItem();

    private ArrayList<JMenuItem> searchActions=new ArrayList<>();
    private ArrayList<JMenuItem>selectActions=new ArrayList<>();
    private boolean searchEnable=false;


    //系统剪贴板
    Toolkit toolkit=Toolkit.getDefaultToolkit();
    Clipboard clipBoard=toolkit.getSystemClipboard();

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

        //添加插入符侦听器，以便侦听任何插入符的更改通知。
        body.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                try {
                    //e.getDot() 获得插入符的位置。
                    int offset = e.getDot() ;

                    //getLineOfOffset(int offset)  将组件文本中的偏移量转换为行号
                    int row = body.getLineOfOffset(offset);

                    //getLineStartOffset(int line)   取得给定行起始处的偏移量。
                    //getLineEndOffset(int line)     取得给定行结尾处的偏移量。
                    int col = e.getDot() - body.getLineStartOffset(row);
                    String status = MessageFormat.format(pattern,Integer.toString(row+1),Integer.toString(col+1));
                    statusLabel.setText(status);
                    // 在状态栏中显示当前光标所在行号、所在列号

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //设置标题
		this.setTitle(fileName+" - 记事本");
		this.setSize(1500,800);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				//overwrite close function
				controller.exit(); //重写默认关闭按钮
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
				controller.createFile();
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
				controller.openFile();
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
				controller.saveFile();
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
				controller.saveForFile();
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
				controller.exit();
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


        //当选择编辑菜单时，设置剪切、复制、粘贴、删除等功能的可用性
        editMenu.addMenuListener(new MenuListener() {
            public void menuCanceled(MenuEvent e) {
                checkMenuItemEnabled();
            }
            public void menuDeselected(MenuEvent e) {
                checkMenuItemEnabled();
            }
            public void menuSelected(MenuEvent e){
                checkMenuItemEnabled();
            }
        });

        //剪切
		//cut function
		final JMenuItem cutItem = new JMenuItem();
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
		cutItem.setText("\t剪切");
		cutItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.cut();
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
				controller.copy();
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
				controller.paste();
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
				controller.selectAll();
			}
			
		});
		
		//撤销
		//undo function
        rollbackItem.setEnabled(false);
		rollbackItem.setMnemonic(KeyEvent.VK_U);
		rollbackItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));

		rollbackItem.setText("\t撤销(U)");
		rollbackItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.rollback();
			}
			
		});

		//查找
		//find function
		JMenuItem findItem = new JMenuItem("查找");
		findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
		findItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.find(body.getSelectedText());
			}

		});

		//替换
		//replace function
		JMenuItem replaceItem = new JMenuItem("替换");
		replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK));
		replaceItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.replace(body.getSelectedText());
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
        JCheckBoxMenuItem statusItem=new JCheckBoxMenuItem("\t状态栏(S)");
        statusItem.setState(true);
		statusItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setVisible(statusItem.getState());
            }
        });

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
				controller.mainPgae();
			}
			
		});

		aboutItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.about();
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
				controller.cut();
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
				controller.copy();
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
				controller.paste();
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
				controller.selectAll();
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
				controller.rollback();
			}
			
		});

		//查找
		//find function for mouse right click menu
		final JMenuItem findItem2 = new JMenuItem("查找");
		findItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				controller.find(body.getSelectedText());
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
				controller.isEdited = true;
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

        body.getDocument().addDocumentListener(this);

		//设置滚动条一直存在，像windows 的notepad 一样
		sp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportView(body);

		this.add(sp);



		this.add(statusLabel,BorderLayout.SOUTH);//向窗口添加状态栏标签

		//get the system's clipboard
		clipboard = getToolkit().getSystemClipboard();//获取系统剪贴板



        searchActions.add(findItem);
        searchActions.add(replaceItem);

        for(JMenuItem item:searchActions) {
            item.setEnabled(false);
        }

        selectActions.add(cutItem);
        selectActions.add(copyItem);

        for(JMenuItem item:selectActions) {
            item.setEnabled(false);
        }



	}

    //检查编辑菜单中选项的可用性
    public void checkMenuItemEnabled() {
        String selectText=body.getSelectedText();
        boolean selected=(selectText!=null);
        for(JMenuItem item : selectActions){
            item.setEnabled(selected);
        }

        //可不可以进行搜索
        boolean searchable=body.getText().length()!=0;
        for (JMenuItem item : searchActions) {
            item.setEnabled(searchable);
        }


        //可不可以黏贴
        Transferable contents=clipBoard.getContents(this);
        pasteItem.setEnabled(contents!=null);

    }


    //实现DocumentListener接口中的方法
    public void removeUpdate(DocumentEvent e) {
	    rollbackItem.setEnabled(true);
    }

    public void insertUpdate(DocumentEvent e) {
	    rollbackItem.setEnabled(true);
    }
    public void changedUpdate(DocumentEvent e) {
	    rollbackItem.setEnabled(true);
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
