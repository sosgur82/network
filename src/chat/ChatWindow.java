package chat;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ChatWindow {
	private Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;

	public ChatWindow( Socket socket ) {
		this.socket = socket;
		
		frame = new Frame( "채팅방" );
		pannel = new Panel();
		buttonSend = new Button( "Send" );
		textField = new TextField();
		textArea = new TextArea( 30, 80 );
	}

	public void show() throws IOException {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener( new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		frame.setVisible(true);
		frame.pack();
		
		bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
		printWriter = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8 ), true );
		new ChatClientReceiveThread().start();
	}
	
	private void quit() {
		try {
			printWriter.println( "quit" );
			
			if( socket != null && socket.isClosed() == false) {
				socket.close();
			}
			
			System.exit(0);
		} catch( IOException ex ) {
			ChatClientApp.log( "error:" + ex );
		}		
	}
	
	private void sendMessage() {
		String message = textField.getText();
		
		// 빈 메세지
		if( "".equals( message ) ) {
			return;
		}
		
		if ( "/q".equals( message ) == true ) {
			// /q 프로토콜 처리
			quit();
			return;
		} 
		
		// 메시지 처리
		printWriter.println( "message:" + message );
		textField.setText( "" );
		textField.requestFocus();		
	}
	
	private class ChatClientReceiveThread extends Thread {
		@Override
		public void run() {
			try {
				while( true ) {
					String data = bufferedReader.readLine();
					if( data == null ) {
						ChatClientApp.log( "Disconnection by Server" );
						break;
					}
					textArea.append( data + "\n" );
				}
			} catch( SocketException ex ) {
				ChatClientApp.log( "Closed by Server" );
			} catch( IOException ex ) {
				ChatClientApp.log( "error:" + ex );
			}
		}
	}	
}