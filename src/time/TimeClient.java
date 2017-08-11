package time;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class TimeClient {
		private static final String SERVER_IP = "192.168.1.32";
		private static final int SERVER_PORT = 6000;
		private static final int bUFFER_SIZE = 1024;
		public static void main(String[] args) {
			DatagramSocket socket = null;//0. 키보드 연결
			Scanner scanner = new Scanner(System.in);
			try {
				
				socket = new DatagramSocket();
				
				while(true) {
					//2. 사용자 입력
					System.out.print(">>");
					String message =scanner.nextLine();
					
//					if("".equals(message)) {
//						continue;
//					}
					if("QUit".equals(message)) {
						break;
					}
					
					byte[] sendData = message.getBytes("UTF-8");
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, new InetSocketAddress(SERVER_IP,SERVER_PORT));
					
					socket.send(sendPacket);
					
					//4. 메세지 수신
					DatagramPacket receivePacket = new DatagramPacket( new byte[bUFFER_SIZE], bUFFER_SIZE);
					socket.receive(receivePacket);
					
					message = new String(receivePacket.getData(),0,receivePacket.getLength(),"UTF-8");
					 System.out.println("[UDP Echo Client] received:"+message);

				}
				
				
			}catch(SocketException e){
				e.printStackTrace();
			}catch(IOException e) {
				e.printStackTrace();
			}finally {
				if(socket != null && socket.isClosed()==false) {
					socket.close();
				}
				scanner.close();
			}

		}

	}

