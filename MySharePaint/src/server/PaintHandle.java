package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * 对每一个客户端开设一个线程处理句柄
 */
public class PaintHandle extends Thread{
	
	private Socket socket;//与相应客户端连接的socket
	
	private BufferedReader inBufferedReader;//输入流
	
	private PrintWriter outPrintWriter;//输出流
	
	private MyServer parentServer;//服务器对象
	
	private Thread thread;
	
	private int thePort;
	
	private boolean disconnect=false;//是否处于连接状态

	
	
	public PaintHandle(Socket socket, MyServer myServer) {
		this.socket=socket;
		this.parentServer=myServer;
		
		try {
			inBufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));//从socket连接获取输入流
			
			outPrintWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);//从socket连接获取输出流
			
			thePort=socket.getPort();
			
			thread=new Thread(this);
			
			thread.start();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//
	}

	
    
	//服务器提供的转发消息发送给客户端
	public void sendMessage(String messageString) {
		
		outPrintWriter.flush();
		outPrintWriter.println(messageString);
		
	}
	
	//处理客户端发来的消息
	public void handleMessage(String messageString) {
		String[] str=messageString.split(":");
		switch (str[0]) {
		case "log":
		{
			//连接申请
			System.out.println("一个客户端建立连接");
			parentServer.broadcastMessageToEachClient(messageString);
			outPrintWriter.flush();
			break;
		}
		case "quit":
		{   
			
			//退出申请
			System.out.println("一个客户端释放连接");
			disconnectClient();
			parentServer.broadcastMessageToEachClient(messageString);
			break;
		}
		
		default:
		{   
			//普通消息
			parentServer.broadcastMessageToEachClient(messageString);
			break;
			
		}
			
			
		}
	}



	//与服务器断开连接
	private void disconnectClient() {
		
		try {
			inBufferedReader.close();
		     outPrintWriter.close();//关闭输入输出流
			socket.close();//关闭socket连接
			parentServer.removeConnection(this);//连接池中关闭
		    disconnect=true;//连接状态改变
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	//线程运行体，获取消息
	public void run() {
		String messageString;
		try {
			while((messageString=inBufferedReader.readLine())!=null) {
				System.out.println("收到："+messageString);
				handleMessage(messageString);//处理消息
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
