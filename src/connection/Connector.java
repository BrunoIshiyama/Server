/*
 * Essa classe contem o metodo main e e' responsavel por aceitar a conexao com o cliente e coloca-lo em sua propria thread de execucao
 */
package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import actions.Tools;

public class Connector {
	// porta padrao
	public static final int PORT = 9797;
	private static ServerSocket serverSocket;
	private static Connector instance;

	private Connector() throws IOException {
		serverSocket = new ServerSocket(PORT);
	}

	// cria somente uma instancia de servidor
	public static Connector getInstance() throws IOException {
		if (instance == null)
			return instance = new Connector();
		return instance;
	}

	// espera a chegada de clientes e ao aceitar a conexao cria uma thread de
	// execucao para o cliente
	public void awaitClients() {
		System.out.println("Awaiting Clients...");
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket s = serverSocket.accept();
						if (s.isConnected()) {
							System.out.println("Client connected... " + s.getInetAddress() + ":" + s.getPort());
							// thread de execucao do cliente
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

	// realiza o fechamento da conexao assim como o do socket
	public void closeConnection(Socket s) {
		try {
			System.out.println("Closing Communication with Client: " + s.getInetAddress() + ":" + s.getLocalPort());
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// tarefa da execucao do cliente, isto e' pegar a mensagem enviada pelo cliente
	// e encaminhar para a funcao correta
	public void manageClient(Socket s1) throws IOException {
		Socket s = s1;
		Tools t = new Tools();
		String oldMessage = "";
		String oldReturn = "";
		System.out.println("Managing  Client\'s requests");
		LOOP: while (true) {
			InputStream input = s.getInputStream();
			byte[] info = new byte[input.available()];
			input.read(info);

			String[] command = new String(info).split(" ");
			String sw = "";
			for (String st : command) {
				sw += " " + st;
			}
			if (sw.trim().equals(oldMessage) || sw.trim().isEmpty()) {
				continue;
			} else {
				oldMessage = sw.trim();
				System.out.println("message: \"" + sw.trim() + "\"");
				OutputStream os = s.getOutputStream();
				String result = "";
				switch (command[0]) {
				case "history":
					System.out.println("HISTORY");
					t.saveToHistory(command);
					result = t.history();
					os.write(result.getBytes());
					os.flush();
					break;
				case "help":
					t.saveToHistory(command);
					result = t.help();
					os.write(result.getBytes());
					os.flush();
					break;
				case "exec":
					t.saveToHistory(command);
					result = "exec command is missing an argument";
					if(command.length>1) {
						result = t.execute(command[1]);
					}
					
					os.write(result.getBytes());
					os.flush();
					break;
				case "shut":
					t.saveToHistory(command);
					os = s.getOutputStream();
					result = t.shutdown();
					os.write(result.getBytes());
					os.flush();
					break LOOP;
				case "uninstall":
					t.saveToHistory(command);
					result = "uninstall command is missing an argument";
					if(command.length>1) {
						result = t.uninstall(command[1]);
					}
					
					os.write(result.getBytes());
					os.flush();
					break;
				case "pwd":
					t.saveToHistory(command);
					result = t.printWorkDir();
					os.write(result.getBytes());
					os.flush();
					break;
				case "mv":
					t.saveToHistory(command);
					result = "mv command is invalid";
					if (command.length > 2 && !command[1].isEmpty()) {
						result = t.moveFile(command[1], command[2]);
					}
					os.write(result.getBytes());
					os.flush();
					break;
				case "cp":
					t.saveToHistory(command);
					result = "cp command is invalid";
					if(command.length>2) {
						String src = command[1].equals("\\.") ? t.getCurrentPath() : command[1];
						String to = command[2].equals("\\.") ? t.getCurrentPath() : command[2];
						result = t.copy(src, to);
					}
					os.write(result.getBytes());
					os.flush();
					
					break;
				case "cd":
					t.saveToHistory(command);
					break;
				case "cat":
					t.saveToHistory(command);
					result = "File not found";
					if (command.length > 1 && !command[1].isEmpty()) {
						result = t.readFileContent(command[1]);
					}
					os.write(result.getBytes());
					os.flush();
					break;
				case "install":
					t.saveToHistory(command);
					result = "install command is missing an argument";
					if(command.length>1) {
						result = t.install(command[1]);
					}
					
					os.write(result.getBytes());
					os.flush();
				case "ls":
					t.saveToHistory(command);
					result = "ls path not found";
					if (command.length == 1) {
						result = t.listFiles("");
					} else if (command.length > 1) {
						String path = command[1];
						if (path.equals("\\.") || path.isEmpty()) {
							result = t.listFiles("");
						} else {
							result = t.listFiles(path);
						}
					}
					os.write(result.getBytes());
					os.flush();
					break;
					//TODO
				case "rm":
					t.saveToHistory(command);
					result = "rm command is invalid";
					os.write(result.getBytes());
					os.flush();
					break;
				case "touch":
					t.saveToHistory(command);
					result = "touch command is invalid";
					if (command.length > 1 && !command[1].isEmpty()) {
						result = t.createFile(command[1]);
					}
					os.write(result.getBytes());
					os.flush();
					break;
				case "mkdir":
					t.saveToHistory(command);
					result = "mkdir command is invalid";
					if (command.length > 1 && !command[1].isEmpty()) {
						result = t.createFolder(command[1]);
					}
					os.write(result.getBytes());
					os.flush();
					break;
				default:
					os = s.getOutputStream();
					result = "Command not Found";
					os.write(result.getBytes());
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
