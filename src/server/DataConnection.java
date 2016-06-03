package server;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class DataConnection {
	private String fileName;
	private byte[] sha;
	private long size;
	private int clientPort;
	private InetAddress clientAddress;
	private DatagramSocket datagramSocket;
	private ConfigFile configFile;
	private final int identifier;
	public DataConnection(String fileName, byte[] sha, long size, int clientPort, InetAddress clientAddress) {
		this.fileName = fileName;
		this.sha = sha;
		this.size = size;
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
		configFile = new ConfigFile(fileName, sha, size);
		identifier = 252; //TODO Change it to get some random value
	}
	public void handleUpload(){
		long currentOffset;
		long bytesReceived;
		boolean cycleComplete = false;
		long offsetForDataRequest;
		long lengthForDataRequest;
		int [] packetTracker = new int[] {0,0,0,0};

		currentOffset = configFile.getOffset();
		if(size > currentOffset){
			
			while(!cycleComplete){
				offsetForDataRequest = getOffsetForDataRequest();
				lengthForDataRequest = getLengthForDataRequst();
				sendDataRequest(offsetForDataRequest,  lengthForDataRequest,  identifier);
				receiveDataResponse();
				writeDataToFile();
				
				
			}
		}
		
	}
	private long getLengthForDataRequst() {
		// TODO Auto-generated method stub
		return 0;
	}
	private long getOffsetForDataRequest() {
		// TODO Auto-generated method stub
		return 0;
	}
	private void writeDataToFile() {
		// TODO Auto-generated method stub
		
	}
	private void receiveDataResponse() {
		// TODO Auto-generated method stub
		
	}
	private void sendDataRequest(long offsetForDataRequest, long lengthForDataRequest, int identifier) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
