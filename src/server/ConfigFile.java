package server;

import java.nio.ByteBuffer;

import fileOperation.FileManager;

public class ConfigFile implements ConfigInterface {
	private String fileName;
	private byte[] sha;
	private long size;
	FileManager fileManager;
	public ConfigFile(String fileName, byte[] sha, long size){
		this.fileName = fileName;
		this.sha = sha;
		this.size = size;
		int offset = 0;
		byte [] sizeInBytes = ByteBuffer.allocate(8).putLong(size).array();
		byte [] offsetInBytes = ByteBuffer.allocate(8).putLong(offset).array();

		fileManager = new FileManager(fileName);
		
		if(!(fileManager.fileExits())){
			fileManager.writeFromPosition(0, 20, sha);
			fileManager.writeFromPosition(20, 8, sizeInBytes);
			fileManager.writeFromPosition(28, 8, offsetInBytes);
		}
		else {
			//TODO: If file exists, compare the sha.
			// TODO: If the sha dont match throw an exception
		}
		
		}
	
	@Override
	public void updataConfigFile(long offset) {
		byte [] offsetInBytes = ByteBuffer.allocate(8).putLong(offset).array();
		fileManager.writeFromPosition(28, 8, offsetInBytes);
	}
	@Override
	public void deleteConfigFile() {
		fileManager.deleteFile();
	}
	@Override
	public long getOffset() {
		// TODO Auto-generated method stub
		ByteBuffer byteBuffer= ByteBuffer.wrap(fileManager.readFromPosition(28, 8));
		return byteBuffer.getLong();
	}
	@Override
	public byte[] getSha() {
		byte [] sha = fileManager.readFromPosition(0, 20);
		return sha;
	}

	@Override
	public long getSize() {
		ByteBuffer byteBuffer= ByteBuffer.wrap(fileManager.readFromPosition(20, 8));
		return byteBuffer.getLong();
	}
	
}
