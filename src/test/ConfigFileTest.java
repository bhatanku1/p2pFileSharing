package test;

import fileOperation.FileManager;
import server.ConfigFile;

public class ConfigFileTest {
	public static void main(String[] args){
		String fileName = "C:\\Users\\ankur\\test.txt";
		FileManager fileManager = new FileManager(fileName);
		long fileSize = fileManager.getSize();
		byte[] sha = fileManager.getHash("SHA-1", 0, (int)fileSize );

		ConfigFile configFile = new ConfigFile("C:\\Users\\ankur\\test.pft", sha,fileSize);
		if(fileSize == configFile.getSize()){
			System.out.println("Size matched: passed");
		}
		if(configFile.getOffset() == 0) {
			System.out.println("offset matched: passed");

		}
		long newOffset = 2525;
		configFile.updataConfigFile(newOffset);
		if(configFile.getOffset() == newOffset){
			System.out.println("offset update: passed");
		}
		else {
			System.out.println("offset match: fail");

		}
		
		configFile.deleteConfigFile();
			
		
	}
}
