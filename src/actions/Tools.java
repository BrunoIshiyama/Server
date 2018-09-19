package actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Tools {

	public static String createFolder(String path) {
		File f = new File(path);
		if (f.mkdirs()) {
			return "Folder created : " + f.getName();
		}
		return "Could not create folder.\nPath received: " + path;
	}

	public static String createFile(String path) throws IOException {
		File f = new File(path);
		if (f.createNewFile()) {
			return "File created at: " + path;
		}
		return "The file " + path + " already exists.";
	}

	public static String removeFile(String path) throws IOException {
		File f = new File(path);
		String ans = f.getName();
		Files.delete(f.toPath());
		return "Removed: " + ans;
	}

	public static String removeRecursively(String path) throws IOException {
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
	public static String listFiles(String path) {
		File f = new File(path);
		File[] files = f.listFiles();
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			sb.append((file.isDirectory()?"DIR":"FILE")+"\t"+file.getName()+"\n");
		}
		return sb.toString();
	}
	public static String changeDir(String path) {
		return null;
	}
	public static String moveFile(String src,String target) {
		return null;
	}
	public static String copy(String from,String destination) {
		return null;
	}
	public static String printWorkDir() {
		return null;
	}
	public static String install(String program) {
		return null;
	}
	public static String uninstall(String program) {
		return null;
	}
	public static String shutdown() {
		return "Closing connection";
	}
	public static String execute(String program) {
		return null;
	}
	public static String help() {
		return null;
	}
	public static String history() {
		return null;
	}
}
