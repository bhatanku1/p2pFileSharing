package server;

public interface ConfigInterface {
	public void updataConfigFile(long offset);
	public void deleteConfigFile();
	public long getOffset();
	public byte[] getSha();
	public long getSize();
}
