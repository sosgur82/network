package echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class EchoServerReceiveThread extends Thread {
	private Socket socket;
	
	public EchoServerReceiveThread( Socket socket ) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		//4. 연결 성공
		InetSocketAddress remoteSocketAddress = 
				(InetSocketAddress)socket.getRemoteSocketAddress();
		int remoteHostPort = remoteSocketAddress.getPort();
		String remoteHostAddress = remoteSocketAddress.getAddress().getHostAddress();
		consoleLog( "connected from " + remoteHostAddress + ":" + remoteHostPort );
		
		try {
			//5. I/O Stream 받아오기
			BufferedReader br = new BufferedReader( new InputStreamReader( socket.getInputStream(), "UTF-8") );
			PrintWriter pw = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), "UTF-8" ), true );
			
			while( true ) {
				// 6. 데이터 읽기 (read)
				String message = br.readLine();
				if( message == null ) { // 정상종료
					consoleLog( "disconnection by client" );
					break;
				}
				consoleLog( "received:" + message );
				
				// 7. 데이터 쓰기
				pw.println( message );
			}
			
		} catch( SocketException e ) {
			// 상대편이 소켓을 정상적으로 닫지 않고 종료한 경우
			consoleLog( "sudden closed by client" );
		} catch( IOException e ) {
			e.printStackTrace();
		} finally {
			try {
				if( socket != null && socket.isClosed() == false ) {
					socket.close();
				}
			} catch( IOException e ) {
				e.printStackTrace();
			}
		}
	}
	
	private void consoleLog( String log ) {
		System.out.println( "[server:" + getId() + "] " + log );
	}
}