package test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalHost {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			InetAddress inAddress = InetAddress.getLocalHost();
			String hostName = inAddress.getHostName();
			String hostAddress = inAddress.getHostAddress();
			byte[] addresses = inAddress.getAddress();
			System.out.println(hostName);
			System.out.println(hostAddress);
			for (int i=0; i< addresses.length; i++) {
				System.out.print(addresses[i] & 0x000000ff);//비트연산으로 제대로 나오게 표시하기
				if(i<3) {
					System.out.print(".");
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
