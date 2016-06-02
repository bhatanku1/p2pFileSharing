package server;

import fileOperation.FileManager;

public class ConfigFile implements ConfigInterface {
	private String fileName;
	private byte[] sha;
	FileManager fileManager;
	public ConfigFile(String fileName, byte[] sha){
		this.fileName = fileName;
		this.sha = sha;
		fileManager = new FileManager(fileName);
	}
	
	@Override
	public void updataConfigFile(long offset, byte[] sha1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteConfigFile(String fileName) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public long getOffset(String fileName) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public byte[] getSha(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
