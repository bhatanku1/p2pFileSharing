package server;

public interface ConfigInterface {
	public void updataConfigFile(long offset, byte[] sha1);
	public void deleteConfigFile(String fileName);
	public long getOffset(String fileName);
	public byte[] getSha(String fileName);
}
