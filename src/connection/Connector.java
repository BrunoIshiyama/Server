package connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector {
	public static final int PORT = 9797;
	private static ServerSocket serverSocket;
	private static Connector instance;
<<<<<<< HEAD

	private Connector() throws IOException {
		serverSocket = new ServerSocket(PORT);
	}

	public static Connector getInstance() throws IOException {
		if (instance == null)
			return instance = new Connector();
		return instance;
	}

=======
	private Connector() throws IOException {
		serverSocket = new ServerSocket(PORT);
	}
	public static Connector getInstance() throws IOException {
		if(instance ==  null) return instance = new Connector();
		return instance;
	}
>>>>>>> 7183aa3c1ebd1450305256144f9722d6c7073f1e
	public void awaitClients() {
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket s = serverSocket.accept();
<<<<<<< HEAD
						if (s.isConnected()) {
							System.out.println("Client connected... " + s.getInetAddress() + ":" + s.getPort());
							new Thread(new Runnable() {
								public void run() {

=======
						System.out.println("Client connected... "+s.getInetAddress()+":"+s.getPort());
						new Thread(new Runnable() {
							public void run() {
								while(s.isConnected()) {
>>>>>>> 7183aa3c1ebd1450305256144f9722d6c7073f1e
									try {
										manageClient(s);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
<<<<<<< HEAD

								}
							}).start();
						}
					}
				} catch (IOException e) {
=======
								}
							}
						}).start();
					}
				}catch(IOException e) {
>>>>>>> 7183aa3c1ebd1450305256144f9722d6c7073f1e
					e.printStackTrace();
				}
			}
		}).start();
	}
<<<<<<< HEAD

	public void manageClient(Socket s) throws IOException {
=======
	public void manageClient(Socket s) throws IOException{
>>>>>>> 7183aa3c1ebd1450305256144f9722d6c7073f1e
		InputStream input = s.getInputStream();
		byte[] info = new byte[input.available()];
		input.read(info);
		System.out.println(new String(info));
	}
<<<<<<< HEAD

	public static void main(String[] args) {
		try {
			Connector.getInstance().awaitClients();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
=======
>>>>>>> 7183aa3c1ebd1450305256144f9722d6c7073f1e
}
