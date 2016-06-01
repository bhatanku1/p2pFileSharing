package test;

import fileOperation.FileManager;

public class FileOperationTest {
public static void main(String[] args) {
	String PATH = "C:\\Users\\ankur\\test.txt";
	boolean result;
	FileManager fileManager = new FileManager(PATH);
	result = fileManager.fileExits();
	System.out.println(result);
}
}
