package connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.tools.Tool;

import actions.Tools;

public class Connector {
	public static final int PORT = 9797;
	private static ServerSocket serverSocket;
	private static Connector instance;

	private Connector() throws IOException {
		serverSocket = new ServerSocket(PORT);
	}

	public static Connector getInstance() throws IOException {
		if (instance == null)
			return instance = new Connector();
		return instance;
	}

	public void awaitClients() {
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket s = serverSocket.accept();
						if (s.isConnected()) {
							System.out.println("Client connected... " + s.getInetAddress() + ":" + s.getPort());

							try {
								manageClient(s);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				} catch (IOException e) {
				}
			}
		}).start();
	}

	public void manageClient(Socket s) throws IOException {
		InputStream input = s.getInputStream();
		byte[] info = new byte[input.available()];
		input.read(info);
		System.out.println(new String(info));
	}

	public static void main(String[] args) {
//		try {
//			Connector.getInstance().awaitClients();
			System.out.println(Tools.listFiles("C:\\Users\\BrunoYujiIshiyama\\Desktop"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
