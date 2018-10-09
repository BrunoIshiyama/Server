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
import decryptor.Decryptor;
import encryptor.Encryptor;

public class Connector {
	// porta padrao
	public static final int PORT = 9797;
	private static ServerSocket serverSocket;
	private static Connector instance;
	// valores para encriptar e desencriptar
	long primeP = 19;
	long primeQ = 23;
	long keyE = 7;
	long clientKey = 31*13;
	long clientKeyE = 11;
	Encryptor enc = new Encryptor(clientKey, clientKeyE);
	Decryptor dec = new Decryptor(primeP, primeQ, keyE);
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
										System.out.println(
												"Connection lost with: " + s.getInetAddress() + ":" + s.getPort()+". Due to "+e.getMessage());
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
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InputStream input = s.getInputStream();
			String message = dec.decrypt(input);
			OutputStream os = s.getOutputStream();
			String[] command = message.split(" ");
			String sw = "";
			for (String st : command) {
				sw += " " + st;
			}
			char[] chars = sw.toCharArray();
			// Checagem de valor em branco ou caractere nulo
			if (sw.trim().isEmpty() || chars[0] == Character.MIN_VALUE) {
				String keepAlive = new Character(Character.MIN_VALUE) + "";
				os.write(keepAlive.getBytes());
				os.flush();
				continue LOOP;
			}
			// se a mensagem enviada for a mesma que a anterior entao nao processe novamente
			if (sw.trim().equals(oldMessage)) {
				send(oldReturn, os);
				continue LOOP;
			} else {

				oldMessage = sw.trim();
				System.out
						.println("message [" + s1.getInetAddress() + ":" + s1.getPort() + "] : \"" + sw.trim() + "\"");

				String result = "";
				// do comando digitado selecione qual se encaixa
				switch (command[0]) {
				case "ps":
					// salve no historico
					t.saveToHistory(command);
					//realize a acao de listagem
					result = t.listProcesses();
					oldReturn = result;
					//responda ao cliente
					send(result, os);
					break;
				case "history":
					t.saveToHistory(command);
					result = t.history();
					oldReturn = result;
					send(result, os);
					break;
				case "help":
					t.saveToHistory(command);
					result = t.help();
					oldReturn = result;
					send(result, os);
					break;
				case "exec":
					t.saveToHistory(command);
					result = "exec command is missing an argument";
					//se houver argumentos entao execute
					if (command.length > 1) {
						result = t.execute(command[1]);
					}
					oldReturn = result;
					send(result, os);
					break;
				case "shut":
					t.saveToHistory(command);
					os = s.getOutputStream();
					result = t.shutdown();
					oldReturn = result;
					send(result, os);
					break LOOP;
				case "pwd":
					t.saveToHistory(command);
					result = t.printWorkDir();
					oldReturn = result;
					send(result, os);
					break;
				case "mv":
					t.saveToHistory(command);
					result = "mv command is invalid";
					if (command.length > 2 && !command[1].isEmpty()) {
						result = t.moveFile(command[1], command[2]);
					}
					oldReturn = result;
					send(result, os);
					break;
				case "cp":
					t.saveToHistory(command);
					result = "cp command is invalid";
					if (command.length > 2) {
						// se for digitado o . para copiar mude para o diretorio atual
						String src = command[1].equals("\\.") ? t.getCurrentPath() : command[1];
						String to = command[2].equals("\\.") ? t.getCurrentPath() : command[2];
						result = t.copy(src, to);
					}
					oldReturn = result;
					send(result, os);

					break;
				case "cd":
					t.saveToHistory(command);
					if (command.length > 1) {
						result = t.changeDir(command[1]);
					}
					oldReturn = result;
					send(result, os);
					break;
				case "cat":
					t.saveToHistory(command);
					result = "File not found";
					if (command.length > 1 && !command[1].isEmpty()) {
						result = t.readFileContent(command[1]);
					}
					oldReturn = result;
					send(result, os);
					break;
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
					oldReturn = result;
					send(result, os);
					break;
				// TODO
				case "rm":
					t.saveToHistory(command);
					result = "rm command is invalid";
					if (command.length > 2 && command[1].equals("-r")) {
						result = t.removeRecursively(command[2]);
					} else if (command.length > 1) {
						result = t.removeFile(command[1]);
					}
					oldReturn = result;
					send(result, os);
					break;
				case "touch":
					t.saveToHistory(command);
					result = "touch command is invalid";
					if (command.length > 1 && !command[1].isEmpty()) {
						result = t.createFile(command[1]);
					}
					oldReturn = result;
					send(result, os);
					break;
				case "mkdir":
					t.saveToHistory(command);
					result = "mkdir command is invalid";
					if (command.length > 1 && !command[1].isEmpty()) {
						result = t.createFolder(command[1]);
					}
					oldReturn = result;
					send(result, os);
					break;
				default:
					//caso o comando nao esteja mapeado entao mande uma mensagem
					result = "Command not Found";
					oldReturn = result;
					send(result, os);
					break;
				}

			}
		}

		closeConnection(s);
	}
	// metodo para enviar as mensagens para o cliente
	public void send(String result, OutputStream os) throws IOException {
		
		String encrypString = enc.encrypt(result);
		os.write(encrypString.getBytes());
		os.flush();
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
