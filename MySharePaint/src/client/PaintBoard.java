package client;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import config.configs;


/*
 * 
 * 角色：客户端
 * 功能：提供GUI界面，共享白板
 */
public class PaintBoard extends Thread implements ActionListener,MouseListener  {
	
	PaintJpan pan=new PaintJpan();   
	JFrame painter=new JFrame();
	JToolBar jt=new JToolBar("工具栏");
	JPanel jp2=new JPanel(new BorderLayout());
	
	JPanel jp3=new JPanel();
	JPanel jp4=new JPanel(new FlowLayout());
	JPanel jp5=new JPanel(new FlowLayout());
	
	/*
	 * 图形资源加载
	 */
    ImageIcon line=new ImageIcon(configs.LINESHAPE);      //直线
	ImageIcon rect=new ImageIcon(configs.RECTSHAPE);      //矩形
	ImageIcon ellipse=new ImageIcon(configs.ELLIPSESHAPE);//椭圆
	ImageIcon curly=new ImageIcon(configs.CURVESHAPE);    //曲线
	
	
	
	/*
	 * 颜色资源加载
	 */
	ImageIcon r=new ImageIcon(configs.REDCOLOUR);          //红色
	ImageIcon b=new ImageIcon(configs.BLUECOLOUR);         //蓝色
	ImageIcon g=new ImageIcon(configs.GREENCOLOUR);          //绿色
	ImageIcon bk=new ImageIcon(configs.BLACKCOLOUR);     //黑色
	
	
	
	/*
	 * 按钮
	 */
	
	/*
	 * 形状按钮
	 */
	JButton lin=new JButton(line);               
	JButton rec=new JButton(rect);
	JButton ell=new JButton(ellipse);
	JButton cur=new JButton(curly); 
	
	/*
	 * 功能按钮
	 */
	JButton clear_record= new JButton("清空记录");  
	JButton clear= new JButton("清空画板");
	
	/*
	 * 颜色按钮
	 */
	JButton red=new JButton(r);
	JButton green=new JButton(g);
	JButton blue=new JButton(b);
	JButton black=new JButton(bk);
	JButton log=new JButton("连接");              //与服务器建立或断开连接
	
	/*
	 * 标签
	 */
	
	JLabel portlLabel=new JLabel("port:");
	JLabel ipLabel=new JLabel("IP:");
	JLabel namelJLabel=new JLabel("用户名:");
	
	
	/*
	 * 文本框
	 */
	JTextField port=new JTextField(""+configs.PORT);         //服务器端口号
	JTextField ip=new JTextField(configs.HOST);           //服务器IP
	JTextField name=new JTextField();
	JTextField chat_mess=new JTextField("回车发送");
	static JTextArea chat_record =new JTextArea(20,20);   //存放聊天记录
	
	
	JScrollPane scrollBar;        //滚动条控件
	
	
	
	/*
	 * 连接所用资源
	 */
	boolean send=false;
	static String message=null;
	Socket theSocket;
	BufferedReader in;
	
	int logNum=1;
	boolean isConnect=false;           //判断是否连接状态
	Thread thread;
	
	
	 //创建客户端窗口
    public JPanel creatFrame()                 
    {
    	JPanel allpan=new JPanel(new BorderLayout());
    	
		jt.setBorderPainted(true);
		jt.setLayout(new GridLayout(4,2)); //4行2列的图形或颜色标签
		jt.add(black); 
		jt.add(lin);
		jt.add(red);
		jt.add(rec);
		jt.add(blue);
		jt.add(ell);
		jt.add(green);
		jt.add(cur);
		
		scrollBar=new JScrollPane(chat_record); //滚动条控件,chat_record存放聊天记录
		//设置聊天记录的多行文本框
		chat_record.setBorder(BorderFactory.createTitledBorder("聊天消息"));
		chat_record.setEditable(false);
		chat_record.setLineWrap(true);
		//设置聊天记录的多行文本框
		
		chat_mess.setColumns(18); //单行信息发送框
		
		//聊天记录与信息发送框放在同一个panel里面
		jp5.add(chat_mess);       
	    jp5.add(clear_record);
	    //聊天记录与信息发送框放在同一个panel里面
	    
		jp2.add(scrollBar,BorderLayout.CENTER);
		jp2.add(jp5,BorderLayout.PAGE_END);
		
		//jp3存放frame最上面的几个控件，包括用户名，port，IP等
		jp3.add(namelJLabel);
		jp3.add(name);
	    jp3.add(portlLabel);
	    jp3.add(port);
	    jp3.add(ipLabel);
	    jp3.add(ip);
	    jp3.add(log);
	    port.setColumns(20);
	    ip.setColumns(20);
	    name.setColumns(20);
	    
	    setLabelShape(portlLabel);
	    setLabelShape(namelJLabel);
	    setLabelShape(ipLabel);
	    
		jp4.add(clear); //清空画板
       
		allpan.add(jt,BorderLayout.WEST); //顶层panel
		allpan.add(jp2,BorderLayout.EAST);
		allpan.add(jp3,BorderLayout.NORTH);
		allpan.add(jp4,BorderLayout.PAGE_END);
		allpan.add(pan,BorderLayout.CENTER);
		
		setTextShape(port);
		setTextShape(ip);
		setTextShape(name);
		setTextShape(chat_mess);
		//添加监听事件,添加说明文字
		lin.addActionListener(this);  
		lin.setToolTipText("直线");
		rec.addActionListener(this);
		rec.setToolTipText("矩形");
		ell.addActionListener(this);
		ell.setToolTipText("椭圆");
		cur.addActionListener(this);
	    cur.setToolTipText("曲线");
		
	    
		red.addActionListener(this);
		red.setToolTipText("红色画笔");
		green.addActionListener(this);
		green.setToolTipText("绿色画笔");
		blue.addActionListener(this);
		blue.setToolTipText("蓝色画笔");
		black.addActionListener(this);
		black.setToolTipText("黑色画笔");
		
		
		
		clear.addActionListener(this);  //清空画板的监听
		log.addActionListener(this);    //log为连接按钮
		chat_mess.addActionListener(this); //消息发送框
		clear_record.addActionListener(this); //清空记录按钮
		setButtonShape(clear);
		setButtonShape(clear_record);
		setButtonShape(log);
		pan.addMouseListener(this);  //添加鼠标监听事件
		
		
		chat_record.setFont(new Font("宋体", Font.PLAIN, 15) );
		chat_record.setBackground(new Color(0xEDED89));
		return allpan;
    }
    
    
    //设置按钮样式
    public void setButtonShape(JButton jButton) {
    	jButton.setPreferredSize(new Dimension(100,30));
		jButton.setBackground(new Color(0x57B1ED));
		jButton.setBorder(BorderFactory.createRaisedBevelBorder());
		jButton.setFont(new  Font("宋体",Font.BOLD,16));
    }
    
    //设置文本域样式
    public void setTextShape(JTextField jTextField) {
    	jTextField.setFont(new Font("宋体", Font.PLAIN, 15) );
    	jTextField.setBackground(new Color(0xEDED89));
    	
    }
    
    //设置标签样式
    public void setLabelShape(JLabel jLabel) {
    	jLabel.setFont(new Font("黑体", Font.BOLD, 15) );
    	jLabel.setBackground(new Color(0xEDED89));
    	
    }
    
     //处理收到的消息（协议）
    private void mesReceiveAndHandle(String s)     
	{
		String[] mes=s.split(":");	                //“：”作为分隔符，第一个冒号之前的内容为消息的类型
		String str;
		
		switch (mes[0]) {
		case "log":
		{   
			//登录消息
			
			if(mes[1].equals(name.getText()))
				chat_record.append("您已成功登录！\n");
			else 
			{
				chat_record.append(mes[1]+" 登录成功！\n");
			}
			
			break;
		}
		
		case "quit":{
			//断开连接消息
			chat_record.append(mes[1]+" 已断开连接！\n");
			
			break;
		}
		
		
		case "clear":{
			//清除画版消息
			pan.drawClear();
			
			break;
		}
		case "chat":{
			//聊天消息
			
			//判断发送消息的角色
			Date now = new Date();
			  SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");//可以方便地修改日期格式
			  String dateString= dateFormat.format( now )+" ";
			if(mes[1].equals(name.getText()))
	    	{ 
				
				  
	    		
	    		chat_record.append(dateString+"我说: "+mes[2]+"\n");
	    	}
	    	else 
	    	{
	    		chat_record.append(dateString+mes[1]+"说: "+mes[2]+"\n");
			}
			break;
		}
		default:{
			
			//其他为绘画消息
			//存放坐标
			int x1=Integer.valueOf(mes[1]);     
			int y1=Integer.valueOf(mes[2]);
			int x2=Integer.valueOf(mes[3]);
			int y2=Integer.valueOf(mes[4]);
			str=mes[5];                        //存放颜色类型
			if(mes[0].equals("line"))          //类型为line则画直线
			    PaintJpan.mode=1;
			else if(mes[0].equals("rect"))
				PaintJpan.mode=2;
			else if(mes[0].equals("ell"))
				PaintJpan.mode=3;
			else if(mes[0].equals("cur"))
				PaintJpan.mode=4;
			if(str.equals("red"))       //颜色类型为red时设置画笔颜色为红色,并把当前颜色显示为红色
		    {
				//bla.setIcon(r);
				PaintJpan.color=Color.red;
		    }
		    else if(str.equals("green"))
		    {
		    	//bla.setIcon(g);
		    	PaintJpan.color=Color.green;
		    }
		    else if(str.equals("blue"))
		    {
		    	//bla.setIcon(b);
		    	PaintJpan.color=Color.blue;
		    }
		    else if(str.equals("black"))
		    {
		    	//bla.setIcon(bk);
		    	PaintJpan.color=Color.black;
		    }
			pan.drawReceive(x1, y1, x2, y2);    //调用drawReceive方法
			break;
		}
			
		}
				
    }
    
    
    
    
      //用于接收服务器发来的消息
	public void run()             
	{
		
		try 
		{
			String line=" ";
			while(isConnect&&line!=null)
			{
				
				line=in.readLine();
				if(line!=null)
				{
					mesReceiveAndHandle(line);//消息不为空则处理消息
					
				}
			}
		} catch (IOException e){}
	}
	
	
	
	
	//用于和服务器建立连接
    public boolean connect()        
    {
		String ports=port.getText();          //获得服务器端口号
		String ips=ip.getText();              //获得ip
		if(theSocket!=null)
			return false;
		int por=Integer.valueOf(ports);
		try 
		{
			if(!name.getText().equals("")&&!ip.getText().equals("")&&!port.getText().equals(""))//文本框中ip和端口不为空
			{
				theSocket=new Socket(ips,por);
				in=new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
				pan.out=new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()),true); 
				isConnect=true;
				thread=new Thread(this);      //连接建立成功后，线程开启
				thread.start();
				PaintJpan.lineboolean=true;
			}
			else {
				return false;
			}
		} 
		catch (Exception e) 
		{
			isConnect=false;
			return false;
		}
		return true;
			   
	}
    
    
    
    //断开和服务器的连接
	public void disconnect()           
	{
	     if(theSocket!=null)
	     {
		     try 
		     {
		    	 isConnect=false;
		    	 theSocket.close();
			
		     } catch (Exception e)
		     {
			  
		     }
		     
		     //状态改变
		     theSocket=null;
		     PaintJpan.lineboolean=false;
			 PaintJpan.rectboolean=false;
			 PaintJpan.ellboolean=false;
			 PaintJpan.curboolean=false;
			 PaintJpan.isClear=false;
	    }
	}
	
	
	
	//事件响应
    public void actionPerformed(ActionEvent e)    
    {
    	//根据被点击的按钮资源判断
    	//System.out.println("被点击！");
    	
        if(e.getSource()==clear_record)         //未建立连接时只有清空记录按钮和连接按钮有效
	    {   
        	//清除聊天记录
			 chat_record.setText(null);
	    }
    	else if(e.getSource()==log)
		{
			if(logNum==1)               //logNum=1时log按钮的文本内容为“连接”，响应的事件是和服务器建立连接
			{
				if(connect())
				{
					logNum=2;           //连接成功后logNum=2
					log.setText("断开");
					pan.out.flush();
					pan.out.println("log"+":"+name.getText());//发送登录消息
				}
				else 
				{     JOptionPane.showMessageDialog(null, "登录失败！请检查用户名、IP等是否正确输入!","出错了！",JOptionPane.ERROR_MESSAGE);//弹出小对话框
					 chat_record.append("登录失败！请检查用户名、IP等是否正确输入!\n");
				}
			}
			else if(logNum==2)  //logNum=2时log按钮的文本内容为“断开”，响应的事件是断开和服务器的连接
			{
				logNum=1;
				log.setText("连接");
				if(isConnect)
				{
					pan.out.flush();
					pan.out.println("quit"+":"+name.getText());
					chat_record.append("您已成功退出！\n");
				}
				disconnect();
			}
		}
    	else if(e.getSource()==chat_mess)
    		
    		
		{  


		
			String s="chat"+":"+name.getText()+":"+chat_mess.getText();       
			if(isConnect&&!chat_mess.getText().equals(""))
			{   
				
				//发送聊天消息
				System.out.println(s);
				pan.out.println(s);
				chat_mess.setText(null);
			}
			else if(isConnect&&chat_mess.getText().equals(""))
			{
				chat_record.append("请输入至少一个字符！\n");
			}
			else if(!isConnect)
			{
				chat_record.append("请先登录！\n");
			}
		}
    	else if(!isConnect)
    	{
    		chat_record.append("请先登录！\n");
    	}
    	else
    	{
			if(e.getSource()==lin)          //点击画直线
			{
				PaintJpan.lineboolean=true;
				PaintJpan.rectboolean=false;
				PaintJpan.ellboolean=false;
				PaintJpan.curboolean=false;
				PaintJpan.isClear=false;
				changeBackColorForButton(0,1);
			}

			if(e.getSource()==rec)      //点击画矩形
			{
				PaintJpan.lineboolean=false;
				PaintJpan.rectboolean=true;
				PaintJpan.ellboolean=false;
				PaintJpan.curboolean=false;
				PaintJpan.isClear=false;
				changeBackColorForButton(1,1);
			}
			if(e.getSource()==ell)     //点击画椭圆
			{   
				
				PaintJpan.lineboolean=false;
				PaintJpan.rectboolean=false;
				PaintJpan.ellboolean=true;
				PaintJpan.curboolean=false;
				PaintJpan.isClear=false;
				changeBackColorForButton(2,1);
			}
			if(e.getSource()==cur)   //点击画曲线
			{
				PaintJpan.location[0]=0;
				PaintJpan.location[1]=0;
				PaintJpan.location[2]=0;
				PaintJpan.location[3]=0;
				PaintJpan.lineboolean=false;
				PaintJpan.rectboolean=false;
				PaintJpan.ellboolean=false;
				PaintJpan.curboolean=true;
				PaintJpan.isClear=false;
				
				changeBackColorForButton(3,1);
			}
	
            if(e.getSource()==red)     //点击选择红色
			{
				
				PaintJpan.color=Color.red;
				changeBackColorForButton(1,0);
			}

            else if(e.getSource()==green)
			{
				
				PaintJpan.color=Color.green;
				changeBackColorForButton(3,0);
			}
            else if(e.getSource()==blue)
			{
				
				PaintJpan.color=Color.blue;
				changeBackColorForButton(2,0);
			}
            else if(e.getSource()==black) {
				PaintJpan.color=Color.black;
			changeBackColorForButton(0,0);
            }

            else if(e.getSource()==clear)          //清空画板
			{
				
				PaintJpan.isClear=true;
	
				pan.drawClear();
				pan.messCraetAndSend();
				   
			}
			
 
    	}
				
	}
    
    
    
    //被点击图形的高亮
    public void changeBackColorForButton(int clickedButton,int col) {
    	
    	boolean states[]= {false,false,false,false};//点击状态 初始都未被点击
    	JButton buttons[]=new JButton[4];
    	if(col==1) {
    			
    	buttons[0]=lin;
    	buttons[1]=rec;
    	buttons[2]=ell;
    	buttons[3]=cur;//分别对应直线、矩形、椭圆、曲线的点击状态
    	}
    	else {
    		buttons[0]=black;
    		buttons[1]=red;
    		buttons[2]=blue;
    		buttons[3]=green;//对应黑色，红色，蓝色，绿色的点击状态
    	}
    	
    	states[clickedButton]=true;
    	for(int i=0;i<4;i++) {
    		if(states[i]==true) {
    			buttons[i].setBackground(new Color(0x09C7F7));
    		}else {
    			buttons[i].setBackground(new Color(0xFFFFFF));
    		}
    	  }
    	}
    
    	
    
    
    @Override
	public void mouseClicked(MouseEvent arg0) 
	{	
		if(!isConnect)
		{
			JOptionPane.showMessageDialog(null, "请先登录！","请先登录！",JOptionPane.INFORMATION_MESSAGE);//弹出小对话框
			
			chat_record.append("请先登录！");
			chat_record.append("\n");
		}
	}
    
    
    
    /*
     * 接口必须实现的函数，在这里不必实现
     */
	public void mouseEntered(MouseEvent arg0) 
	{
		
	}
	public void mouseExited(MouseEvent arg0) 
	{
		
	}
	public void mousePressed(MouseEvent arg0) 
	{
		
	}
	public void mouseReleased(MouseEvent arg0)
	{
		
	}

    
	public static void main(String[] args)
	{
    	JFrame frame=new JFrame("共享白板");
    	PaintBoard bb=new PaintBoard();
    	frame.setContentPane(bb.creatFrame());   //设置frame面板内容
    	frame.setBackground(new Color(0x57EDED));         //设置背景色
    	frame.setSize(1200,500);                   //设置外部frame大小
    	frame.setLocationRelativeTo(null);      //参数值为null,窗口将置于屏幕的中央。  
    	frame.setResizable(false);            //为true生成的窗体可以自由改变大小，false则不可调整
    	frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);  //设置关闭操作
    	frame.setVisible(true);                               //设置可见性
    	Toolkit tool = frame.getToolkit();    //得到一个Toolkit对象
    	Image image = tool.getImage(configs.ICONFILE);
    	frame.setIconImage(image);          //给Frame设置图标

        
	}
}
