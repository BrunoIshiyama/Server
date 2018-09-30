package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
		System.out.println("Awaiting Clients...");
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
		System.out.println("Managing  Client\'s requests");
		
		InputStream input = s.getInputStream();
		byte[] info = new byte[input.available()];
		input.read(info);
		Tools t = new Tools();
		String[] command = new String(info).split(" ");
		System.out.println("message;"+command[0]);
		switch (command[0]) {
			case "history":
				System.out.println("HISTORY");
				t.saveToHistory(command);
				String result = t.history();
				OutputStream os = s.getOutputStream();
				os.write(result.getBytes());
				os.flush();
				break;
			case "help":
				t.saveToHistory(command);
				break;
			case "exec":
				t.saveToHistory(command);
				break;
			case "shut":
				t.saveToHistory(command);
				break;
			case "uninstall":
				t.saveToHistory(command);
				break;
			case "pwd":
				t.saveToHistory(command);
				break;
			case "mv":
				t.saveToHistory(command);
				break;
			case "cp":
				t.saveToHistory(command);
				break;
			case "cd":
				t.saveToHistory(command);
				break;
			case "cat":
				t.saveToHistory(command);
				break;
			case "install":
				t.saveToHistory(command);
				break;
			case "ls":
				t.saveToHistory(command);
				break;
			case "rm":
				t.saveToHistory(command);
				break;
			case "touch":
				t.saveToHistory(command);
				break;
			case "mkdir":
				t.saveToHistory(command);
				break;
			default:
				break;
		}
	}

	public static void main(String[] args) {
		try {
			Connector.getInstance().awaitClients();
		} catch (IOException e) {
//		 TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
