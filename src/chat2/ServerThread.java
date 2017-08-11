package chat2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import chat.ChatServer;

public class ServerThread extends Thread {
	private static final String PROTOCOL_DIVIDER = ":";
	private Socket socket;
	private String nickname;
	private List<PrintWriter> listPrintWriters;

	
	public ServerThread(Socket socket, List<PrintWriter> listPrintWriters) {
		this.socket = socket;
		this.listPrintWriters = listPrintWriters;
	}
	@Override
	public void run() {
		try {
			// I/O Stream 받아오기
			BufferedReader br = new BufferedReader( new InputStreamReader( socket.getInputStream(), "UTF-8") );
			PrintWriter pw = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), "UTF-8" ), true );
			//연결 성공
			InetSocketAddress remoteSocketAddress = 
					(InetSocketAddress)socket.getRemoteSocketAddress();
			int remoteHostPort = remoteSocketAddress.getPort();
			String remoteHostAddress = remoteSocketAddress.getAddress().getHostAddress();
			consoleLog( "connected from " + remoteHostAddress + ":" + remoteHostPort );
									
					while( true ) {
						
						// 6. 데이터 읽기 (read)
						String message = br.readLine();
						if( message == null ) { // 정상종료
							consoleLog( "disconnection by client" );
							break;
						}
						
						String[] tokens = message.split( PROTOCOL_DIVIDER );
						if( "join".equals( tokens[0] ) ) {
							doJoin( pw, tokens[1] );
						} else if( "message".equals( tokens[0] ) ){
							doMessage( tokens[1] );
						} else if( "quit".equals( tokens[0] ) ){
							doQuit( pw );
							break;
						} else {
							ChatServer.log( "에러: 알수 없는 요청명령(" + tokens[0] + ")" );
						}
						
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

			private void doQuit( PrintWriter printWriter ) {
				// PrintWriter 제거
				removePrintWriter( printWriter );
				
				//퇴장 메세지 브로드캐스팅
				String data = nickname + "님이 퇴장하였습니다.";
				broadcast( data );
			}
			
			private void doMessage( String message ) {
				String data = nickname + ":" + message;
				broadcast( data );
			}
			
			private void doJoin( PrintWriter printWriter, String nickname ) {
				//1. 닉네임 저장
				this.nickname = nickname;

				//2. 메세지 브로드캐스팅
				String message = nickname + "님이 입장했습니다.";
				broadcast( message );
				
				//3. Writer Pool 에 저장
				addPrintWriter( printWriter );

				//4. ack
				printWriter.println( "join:ok" );
				printWriter.flush();
			}
			
			private void addPrintWriter( PrintWriter printWriter ) {
				synchronized( listPrintWriters ) {
					listPrintWriters.add( printWriter );
				}
			}
			
			private void removePrintWriter( PrintWriter printWriter ) {
				synchronized( listPrintWriters ) {
					listPrintWriters.remove( printWriter );
				}
			}
			
			private void broadcast( String data ) {
				synchronized( listPrintWriters ) {
				
					int count = listPrintWriters.size();
					for( int i = 0; i < count; i++ ) {
						PrintWriter printWriter = listPrintWriters.get( i );
						printWriter.println( data );
						printWriter.flush();
					}

				}
			}
}
