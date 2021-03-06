package actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Scanner;

public class Tools {
	private String currentPath;
	public static final int HISTORY_SIZE = 80;
	private String[] commandHistory = new String[HISTORY_SIZE];
	private int historyPos = 0;

	public Tools() {
		currentPath = System.getProperty("user.home");
	}

	public String readStream(InputStream os) {
		String s = null;
		byte[] bytes = new byte[4096];
		try {
			os.read(bytes);
		} catch (IOException e) {
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

	public void saveToHistory(String... command) {
		StringBuilder result = new StringBuilder();
		for (String s : command) {
			result.append(s + " ");
		}
		commandHistory[historyPos = ((historyPos + 1) % HISTORY_SIZE)] = result.toString().trim();
	}

	// mkdir
	public String createFolder(String path) {
		String filePath = path;
		if (!path.contains(currentPath)) {
			filePath = currentPath + "/" + path;
		}
		File f = new File(filePath);
		if (f.mkdirs()) {
			return "Folder created : " + f.getName();
		}
		return "Could not create folder.\nPath received: " + path;
	}

	// touch
	public String createFile(String path) throws IOException {
		String filePath = path;
		if (!path.contains(currentPath)) {
			filePath = currentPath + "/" + path;
		}
		File f = new File(filePath);
		if (f.createNewFile()) {
			return "File created at: " + path;
		}
		return "The file " + path + " already exists.";
	}

	// rm
	public String removeFile(String path) {
		String filePath = path;
		if (!path.contains(currentPath)) {
			filePath = currentPath + "/" + path;
		}
		File f = new File(filePath);
		String ans = f.getName();
		try {
			Files.delete(f.toPath());
		} catch (IOException e) {
			File f1 = new File(e.getMessage());
			if (f1.isDirectory()) {
				return "This directory have files in it, either remove them by hand or use rm -r to remove everything in this file";
			} else {
				return "No such file or directory ";
			}
		}
		return "Removed: " + ans;
	}

	// rm -r
	public String removeRecursively(String path) {
		File f = new File(currentPath + "/" + path);
		File[] files = f.listFiles();
		StringBuilder sb = new StringBuilder();
		if (files == null || files.length == 0) {
			sb.append(removeFile(f.getPath()) + "\n");
		} else {
			for (File file : files) {
				if (file.isDirectory()) {
					sb.append(removeRecursively(file.getPath()));
				}
				sb.append(removeFile(file.getPath()) + "\n");
			}
		}
		return sb.toString();
	}

	// ls
	public String listFiles(String path) {
		Process proc = null;
		try {
			proc = Runtime.getRuntime()
					.exec(new String[] { "/bin/bash", "-c", "cd " + currentPath + "; ls -lh " + path });
		} catch (IOException e) {
			return "No such file or directory";
		}
		String output = readStream(proc.getInputStream());
		output = output.trim();
		String error = readStream(proc.getErrorStream());
		char[] chars = error.toCharArray();
		// Checagem de valor em branco ou caractere nulo
		if (error.trim().isEmpty() || chars[0] == Character.MIN_VALUE) {
			return output;
		} else {
			return "No such file or directory";
		}

	}

	// cat
	public String readFileContent(String path) {
		String filePath = path;
		if (!path.contains(currentPath)) {
			filePath = currentPath + "/" + path;
		}
		Scanner sc = null;
		try {
			sc = new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return "No such file or directory";
		}
		StringBuilder sb = new StringBuilder();
		while (sc.hasNext()) {
			sb.append(sc.nextLine() + "\n");
		}
		sc.close();
		return sb.toString();
	}

	public String changeDir(String path) {
		Process proc = null;
		try {
			proc = Runtime.getRuntime()
					.exec(new String[] { "/bin/bash", "-c", "cd " + currentPath + "; cd " + path + " ; pwd" });
		} catch (IOException e) {
			return "This directory does not exist";
		}
		String output = readStream(proc.getInputStream());
		output = output.trim();
		String error = readStream(proc.getErrorStream());
		char[] chars = error.toCharArray();
		// Checagem de valor em branco ou caractere nulo
		if (error.trim().isEmpty() || chars[0] == Character.MIN_VALUE) {
			currentPath = output;
		} else {
			return "This directory does not exist";
		}
		return currentPath;
	}

	// mv
	public String moveFile(String from, String destination) {
		String filePathFrom = from;
		if (!from.contains(currentPath)) {
			filePathFrom = currentPath + "/" + from;
		}
		String filePathDest = destination;
		if (!destination.contains(currentPath)) {
			filePathDest = currentPath + "/" + destination;
		}
		File f = new File(filePathDest);
		if (f.isDirectory()) {
			String[] info = from.split("/");
			String filename = info[info.length - 1];
			copy(from, destination + "/" + filename);
			try {
				Files.delete(new File(filePathFrom).toPath());
			} catch (Exception e) {
				return "No such file or directory";
			}
		} else {
			copy(from, destination);
			try {
				Files.delete(new File(filePathFrom).toPath());
			} catch (Exception e) {
				return "No such file or directory";
			}
		}
		return "File moved to: " + filePathDest;
	}

	// cp
	public String copy(String from, String destination) {
		String filePathFrom = from;
		if (!from.contains(currentPath)) {
			filePathFrom = currentPath + "/" + from;
		}
		String filePathDest = destination;
		if (!destination.contains(currentPath)) {
			filePathDest = currentPath + "/" + destination;
		}
		File f = new File(filePathDest);
		try {
			if (f.isDirectory()) {
				String[] info = from.split("/");
				String filename = info[info.length - 1];
				f = new File(filePathDest + "/" + filename);
			}
			f.createNewFile();
			File source = new File(filePathFrom);
			System.setOut(new PrintStream(f));
			Scanner sc = new Scanner(new FileInputStream(source));
			while (sc.hasNext()) {
				System.out.println(sc.nextLine());
			}
			System.setOut(System.out);
			sc.close();
		} catch (Exception e) {
			return "No such file or directory";
		}
		return "File Copied";
	}

	// pwd
	public String printWorkDir() {
		return getCurrentPath();
	}

	// shut
	public String shutdown() {
		return "end";
	}

	// exec
	public String execute(String... program) throws IOException {
		String job = "";
		for (String p : program) {
			if(p.equals("exec")) continue;
			job += " " + p;
		}
		System.out.println(job);
		Process proc = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", job.trim() });
		String output = readStream(proc.getInputStream());
		StringBuilder sb = new StringBuilder();
		sb.append("Executing \"" + job.trim() + "\"\n");
		sb.append(output);
		output = readStream(proc.getErrorStream());
		sb.append(output);
		return sb.toString();
	}

	public String listProcesses() throws IOException {
		Process proc = Runtime.getRuntime().exec("/bin/bash -c ps");
		String output = readStream(proc.getInputStream());
		StringBuilder sb = new StringBuilder();
		sb.append(output);
		return sb.toString();
	}

	// history
	public String history() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int checkPosition = historyPos;
		while (i < HISTORY_SIZE) {
			if (historyPos - i < 0) {
				checkPosition = historyPos - i + HISTORY_SIZE;
			} else {
				checkPosition = historyPos - i;
			}
			i++;
			String currentCommand = commandHistory[checkPosition];
			if (currentCommand == null || currentCommand.isEmpty() || currentCommand.equals("null")) {
				i++;
				continue;
			}
			sb.append(i + "-" + currentCommand + "\n");
		}
		return sb.toString();
	}
}
