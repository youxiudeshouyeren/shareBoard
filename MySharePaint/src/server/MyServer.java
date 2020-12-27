package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import config.configs;

/*
 * 角色：服务器端
 * 功能：与多个客户端建立连接，并转发白板信息至每一个客户端
 */
public class MyServer {

	private final static int PORT=configs.PORT;//配置文件导入端口号
	
	private ServerSocket serverSocket;//serverSocket等待客户端请求，一旦获取连接请求，就创建Socket与其连接，起监听作用
	
	private ArrayList<PaintHandle> paintHandles;//保存所有连接对应的处理句柄
	
	private int clientCount;//处理句柄的数量，也即客户端数量
	
	
	
	public MyServer() {
		try {
			serverSocket=new ServerSocket(PORT);
			paintHandles=new ArrayList<PaintHandle>();//初始化服务器资源
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public void work() throws IOException {
		
		System.out.println("服务器开始运行！");
		while(true) {
			
			Socket socket=serverSocket.accept(); //服务器监听到一个连接，建立socket连接
			
			//System.out.println("监听到连接1！！");
			
			PaintHandle paintHandle=new PaintHandle(socket,this);//创建处理句柄线程
			
			//System.out.println("监听到连接2！！");
			
			paintHandles.add(paintHandle);//加入服务器连接池
			
			
			
			clientCount++;//客户端数量加1
		}
		
	}
	
	
	//客户端断开连接，服务器从连接池中删除
	public synchronized void removeConnection(PaintHandle paintHandle) {
		
		paintHandles.remove(paintHandle);//连接池中删除
		
		clientCount--;//客户端数量减少
	}
	
	
	//向每一个客户端广播字符串消息
	public void broadcastMessageToEachClient(String messageString) {
		System.out.println("广播："+messageString); //控制台打印输出
	//	System.out.println(paintHandles.size());
	//	System.out.println(clientCount);
		for(PaintHandle paintHandle:paintHandles ) {
			
			paintHandle.sendMessage(messageString);
		}
	}
	
	public static void main(String[] args) {
		MyServer myServer=new MyServer();
		try {
			myServer.work();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
}
