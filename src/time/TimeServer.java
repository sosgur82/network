package time;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServer {

	private static final int PORT = 6000;
	private static final int BUFFER_SIZE = 1024;
	public static void main(String[] args) {
		//1. socket 생성
		DatagramSocket socket = null;
		DatagramPacket sendPacket = null;
		try {
		 socket =  new DatagramSocket(PORT);
		 while(true){
			 //2. 데이터 수신
			 DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
			 socket.receive(receivePacket);//block
			 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
			 String data = format.format(new Date());
			 String message = new String(receivePacket.getData(),0,receivePacket.getLength(),"UTF-8");
			 System.out.println("[UDP Echo Server] received:"+message);
			 if(message.equals("")) {
				 byte[] sendData = data.getBytes("UTF-8");
				 sendPacket = new DatagramPacket(sendData, sendData.length,receivePacket.getAddress(),receivePacket.getPort());
				 socket.send(sendPacket);
			 }else {
				 byte[] sendData = message.getBytes("UTF-8");
				 sendPacket = new DatagramPacket(sendData, sendData.length,receivePacket.getAddress(),receivePacket.getPort());
				 socket.send(sendPacket);
			 }
		 }
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (socket != null && socket.isClosed() == false) {
				socket.close();
			}
		}
	}	
}
