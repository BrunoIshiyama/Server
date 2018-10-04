package actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

public class Tools {
	private String currentPath;
	public static final int HISTORY_SIZE = 80;
	private String[] commandHistory = new String[HISTORY_SIZE];
	private int historyPos = 0;
	//TODO save history over the commands
	public Tools() {
		currentPath = System.getProperty( "user.home" );
		// TODO Change folders in a right way
	}
	public String readStream(OutputStream os) {
		String s = null;
		byte[] bytes = new byte[1024];
		
		try {
			os.write(bytes);
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s = new String(bytes);
		return s;
	}
	public void setCurrentPath(String newPath) {
		currentPath = newPath;
	}
	public String getCurrentPath() {
		return currentPath;
	}
	public void saveToHistory(String...command) {
		StringBuilder result = new StringBuilder();
		for(String s : command) {
			result.append(s+" ");
		}
		commandHistory[historyPos=((historyPos+1)%HISTORY_SIZE)] = result.toString().trim();
	}
	// mkdir
	public String createFolder(String path) {
		File f = new File(path);
		if (f.mkdirs()) {
			return "Folder created : " + f.getName();
		}
		return "Could not create folder.\nPath received: " + path;
	}
	
	// touch
	public String createFile(String path) throws IOException {
		File f = new File(path);
		if (f.createNewFile()) {
			return "File created at: " + path;
		}
		return "The file " + path + " already exists.";
	}
	// rm
	public String removeFile(String path) throws IOException {
		File f = new File(path);
		String ans = f.getName();
		Files.delete(f.toPath());
		return "Removed: " + ans;
	}
	// rm -r
	public String removeRecursively(String path) throws IOException {
		File f = new File(path);
		File[] files = f.listFiles();
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			if (file.isDirectory()) {
				sb.append(removeRecursively(file.getAbsolutePath()));
			}
			sb.append(removeFile(file.getAbsolutePath()) + "\n");
		}
		return sb.toString();
	}
	// ls
	public String listFiles(String path) {
		if(path.isEmpty()) {
			path = currentPath;
		}
		File f = new File(path);
		File[] files = f.listFiles();
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			sb.append((file.isDirectory()?"DIR":"FILE")+"\t"+file.getName()+"\n");
		}
		return sb.toString();
	}
	//cat
	public String readFileContent(String path) throws FileNotFoundException {
		path = currentPath+"/"+path;
		Scanner sc = new Scanner(new File(path));
		StringBuilder sb = new StringBuilder();
		while(sc.hasNext()) {
			sb.append(sc.nextLine()+"\n");
		}
		sc.close();
		return sb.toString();
	}
	//cd TODO
	public String changeDir(String path) {
		currentPath = path;
		return path;
	}
	//mv
	public String moveFile(String src,String target) throws IOException {
		copy(src, target);
		Files.delete(new File(src).toPath());
		return null;
	}
	//cp
	public String copy(String from,String destination) throws IOException {
		File f = new File(destination);
		f.createNewFile();
		File source = new File(from);
		System.setOut(new PrintStream(f));
		Scanner sc = new Scanner(new FileInputStream(source));
		while(sc.hasNext()) {
			System.out.println(sc.nextLine());
		}
		System.setOut(System.out);
		sc.close();
		return "File Copied";
	}
	// pwd
	public String printWorkDir() {
		return getCurrentPath();
	}
	// install TODO
	public String install(String program) {
		return null;
	}
	// uninstall TODO
	public String uninstall(String program) {
		return null;
	}
	// shut
	public String shutdown() {
		return "end";
	}
	//exec
	public String execute(String program) throws IOException {
		Process proc = Runtime.getRuntime().exec("/bin/bash -c "+program);
		String output = readStream(proc.getOutputStream());
		currentPath = output;
		StringBuilder sb = new StringBuilder();
		sb.append("Executing "+program+"\n");
		sb.append(output);
		return sb.toString();
	}
	// help TODO
	public String help() {
		return null;
	}
	// history
	public String history() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int checkPosition = historyPos;
		while(i<HISTORY_SIZE) {
			if(historyPos-i<0) {
				checkPosition = historyPos-i + HISTORY_SIZE;
			}else {
				checkPosition = historyPos-i;
			}
			i++;
			String currentCommand = commandHistory[checkPosition];
			if(currentCommand == null || currentCommand.isEmpty() || currentCommand.equals("null")) {
				i++;
				continue;
			}
			sb.append(i+"-"+currentCommand+"\n");
		}
		return sb.toString();
	}
}
