package thread;

public class MultiThreadEx {

	public static void main(String[] args) {
		Thread thread1 = new AlphabetThread();
		thread1.start();
		for(int i=0; i< 10; i++) {
			System.out.print(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Thread thread2 = new Thread(new DigitThread());
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (char c = 'a'; c<='z'; c++) {
					System.out.print(c);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
//		for (char c = 'a'; c<='z'; c++) {
//			System.out.print(c);
//		}
//		
	}

}
