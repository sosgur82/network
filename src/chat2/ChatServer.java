package chat2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private static final int SERVER_PORT = 9090;
public static void main(String[] args) {

		List<PrintWriter> listPrintWriters = new ArrayList<PrintWriter>();
		ServerSocket serverSocket = null;
		
		try {
			//1. 서버 소켓 생성
			serverSocket = new ServerSocket();
			
			//2. 바인딩( Binding )
			InetAddress inetAddress = InetAddress.getLocalHost();
			String localhostAddress = inetAddress.getHostAddress();
			
			serverSocket.bind( new InetSocketAddress( localhostAddress, SERVER_PORT ) );
			consoleLog( "binding " + localhostAddress + ":" + SERVER_PORT );
			
			while( true ) {
				//3. 연결 요청 기다림( accept )
				Socket socket = serverSocket.accept();   // blocking
				new ServerThread( socket, listPrintWriters ).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if( serverSocket != null && serverSocket.isClosed() == false ) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void consoleLog( String log ) {
		System.out.println( "[server:" + Thread.currentThread().getId() + "] " + log );
	}	
}
