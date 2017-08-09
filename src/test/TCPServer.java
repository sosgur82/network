package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer {
	private static final int SERVER_PORT = 5000;
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			//1. 서버 소켓 생성
			serverSocket = new ServerSocket();
			//2. 바인딩(binding)
			InetAddress inetAddress = InetAddress.getLocalHost();
			String localHostAddress = inetAddress.getHostAddress();
			
			serverSocket.bind(new InetSocketAddress(localHostAddress, SERVER_PORT));
			System.out.println("[server] binding " + localHostAddress + ":" + SERVER_PORT );
			
			//3. accept(연결 요청 기다림)
			Socket socket = serverSocket.accept();
			//연결전까지 대기 (블로킹)
			
			//4. 연결 성공
			InetSocketAddress remoteSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
			int remoteHostPort = remoteSocketAddress.getPort();
			String remoteHostAddress = remoteSocketAddress.getAddress().getHostAddress();
			System.out.println("[server] connected from " + remoteHostAddress + ":" + remoteHostPort);
				try {
					//5. I/O Stream 받아오기
					InputStream is = socket.getInputStream();
					OutputStream os = socket.getOutputStream();
					while(true) {
						//6. 데이터 읽기(read)
						byte[] buffer = new byte[256];
						int readByteCount = is.read(buffer); //블락
						if(readByteCount <= -1) {
							System.out.println("[server] disconnection by client");
							//소켓에서 -1은 상대방에서 연결을 끊을때
							break;
						}
						String data = new String(buffer, 0, readByteCount,"UTF-8");
						System.out.println("server received: " + data);
						//7. 데이터 쓰기
						os.write(data.getBytes("UTF-8"));
					}
					}catch(SocketException e){
					 // 상대편이 소켓을 정상적으로 닫지않고 종료한 경우
						System.out.println("[server] sudden closed by client");
					}catch(IOException e) {
						e.printStackTrace();
					}finally {
						if(socket != null && serverSocket.isClosed() == false ) {
							serverSocket.close();
						}
					}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}finally {
			if(serverSocket != null && serverSocket.isClosed() == false) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
