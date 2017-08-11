package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

public class TCPClient {
	private static final String SERVER_IP = "192.168.1.32";
	private static final int SERVER_PORT = 5000;
	public static void main(String[] args) {
		Socket socket = null;
		try {
			//1. 소켓 생성
		 socket = new Socket();
		 
		 	//1-1 socket Buffer size 확인
		 	int receiveBufferSize = socket.getReceiveBufferSize();
		 	int sendBufferSize = socket.getSendBufferSize();
		 	System.out.println(receiveBufferSize + ":" + sendBufferSize);
		 	
		 	//1-2 SocketBufferSize 변경
		 	socket.setReceiveBufferSize(10*1024);
		 	socket.setSendBufferSize(10*1024);
		 	
		 	receiveBufferSize = socket.getReceiveBufferSize();
		 	sendBufferSize = socket.getSendBufferSize();
		 	System.out.println(receiveBufferSize + ":" + sendBufferSize);
		 	
		 	//1-3 SO_NODELAY(Nagle Algorithm Off)
		 	socket.setTcpNoDelay(true);
		 	
		 	//1-4 SO_TIMEOUT
		 	socket.setSoTimeout(13000);
		 	
			//2. 서버연결
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			
			//3. I/O 받아오기
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			//4. 쓰기/읽기
			String data = "hello";
			os.write(data.getBytes("UTF-8"));
			
			byte[] buffer = new byte[256];
			int readByteCount = is.read(buffer);
			if(readByteCount <= -1) {
				System.out.println("[client] disconnection by server");
				return;
			}
			data = new String(buffer, 0 , readByteCount, "UTF-8");
			System.out.println("[client] received : " + data);
			
		}catch (SocketTimeoutException e) {
			System.out.println("read Timeout");
		} 
		catch (ConnectException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
