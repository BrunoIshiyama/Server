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

							new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										manageClient(s);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).start();

						}
					}
				} catch (IOException e) {
				}
			}
		}).start();
	}
	public void closeConnection(Socket s) {
		try {
			System.out.println("Closing Communication with Client: "+s.getInetAddress()+":"+s.getLocalPort());
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void manageClient(Socket s1) throws IOException {
		Socket s = s1;
		Tools t = new Tools();
		String oldMessage = "";
		System.out.println("Managing  Client\'s requests");
		LOOP:
		while (true) {
			InputStream input = s.getInputStream();
			byte[] info = new byte[input.available()];
			input.read(info);

			String[] command = new String(info).split(" ");
			String sw = "";
			for (String st : command) {
				sw += " " + st;
			}
			if (sw.trim().equals(oldMessage)|| sw.trim().isEmpty()) {
				continue;
			} else {
				oldMessage = sw.trim();
				System.out.println("message: \"" + sw.trim() + "\"");
				OutputStream os = s.getOutputStream();
				switch (command[0]) {
				case "history":
					System.out.println("HISTORY");
					t.saveToHistory(command);
					String result = t.history();
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
					os = s.getOutputStream();
					String resp = t.shutdown();
					os.write(resp.getBytes());
					os.flush();
					break LOOP;
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
					os = s.getOutputStream();
					os.write("No Command Found".getBytes());
					os.flush();
					break;
				}
			}
		}
		closeConnection(s);
	}

	public static void main(String[] args) {
		try {
			Connector.getInstance().awaitClients();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
