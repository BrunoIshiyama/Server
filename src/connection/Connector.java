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
		String[] command = new String(info).split(" ");
		switch (command[0]) {
			case "history":
				break;
			case "help":
				break;
			case "exec":
				break;
			case "shut":
				break;
			case "uninstall":
				break;
			case "pwd":
				break;
			case "mv":
				break;
			case "cp":
				break;
			case "cd":
				break;
			case "cat":
				break;
			case "install":
				break;
			case "ls":
				break;
			case "rm":
				break;
			case "touch":
				break;
			case "mkdir":
				break;
			default:
				break;
		}
	}

	public static void main(String[] args) {
//		try {
//			Connector.getInstance().awaitClients();
		Tools t = new Tools();
		System.out.println(t.listFiles("C:\\Users\\BrunoYujiIshiyama\\Desktop"));
//		} catch (IOException e) {
		// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
}
