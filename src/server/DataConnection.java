package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import marshallDemarshall.Deframer;
import marshallDemarshall.Framer;
import fileOperation.FileManager;
import frames.DataRequest;
import frames.DataResponse;

public class DataConnection {
	private String fileName;
	private byte[] sha;
	private long size;
	private int clientPort;
	private InetAddress clientAddress;
	private DatagramSocket datagramSocket;
	private ConfigFile configFile;
	private final int identifier;
	FileManager fileManager;
	
	public DataConnection(String fileName, byte[] sha, long size, int clientPort, InetAddress clientAddress) {
		this.fileName = fileName;
		this.sha = sha;
		this.size = size;
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
		configFile = new ConfigFile(fileName, sha, size);
		fileManager = new FileManager(fileName);
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		identifier = 252; //TODO Change it to get some random value
	}
	
	public void handleUpload(){
		long currentOffset;
		long bytesReceived;
		boolean cycleComplete = false;
		long offsetForDataRequest;
		long lengthForDataRequest;
		boolean isCycleOver;
		List<byte[]> listDataResponse;
		long[] parametersOffsetAndLength = new long[2];
		int [] packetTracker = new int[] {1,1,1,1};
		currentOffset = configFile.getOffset();
		while(currentOffset < size){
			lengthForDataRequest = getLengthBasedOnSize(currentOffset);
			packetTracker = setPacketTracker(lengthForDataRequest);
			sendDataRequest(currentOffset, lengthForDataRequest, identifier);
			listDataResponse = receiveDataResponse(lengthForDataRequest);
			packetTracker = writeDataToFile(packetTracker, listDataResponse, currentOffset);
			isCycleOver = CheckPacketTracker(packetTracker);
			while(!isCycleOver){
				parametersOffsetAndLength = getOffsetAndLength(packetTracker);
				sendDataRequest(parametersOffsetAndLength[0], parametersOffsetAndLength[1], identifier);
				listDataResponse = receiveDataResponse(parametersOffsetAndLength[1]);
				packetTracker = writeDataToFile(packetTracker, listDataResponse, currentOffset);
				isCycleOver = CheckPacketTracker(packetTracker);
			}	
			configFile.updataConfigFile(currentOffset + lengthForDataRequest);
			currentOffset = configFile.getOffset();

		}

		
		
	}
	private boolean CheckPacketTracker(int[] packetTracker) {
		for(int i = 0; i< packetTracker.length; i++){
			if(packetTracker[i] == 0)
				return false;
		}
		return true;
	}

	
	private int[] setPacketTracker(long lengthForDataRequest) {
		int[] packetTracker;
		int value = (int) (lengthForDataRequest/4096);
		switch(value){
		case 0: 
		case 1: packetTracker = new int[] {0,1,1,1};
				return packetTracker;
		case 2: packetTracker = new int[] {0,0,1,1};
				return packetTracker;
		case 3: packetTracker = new int[] {0,0,0,1};
				return packetTracker;
		default: packetTracker = new int[] {0,0,0,0};
				return packetTracker;
		}
		
	}

	private long getLengthBasedOnSize(long currentOffset) {
		long bytesToBeReceived = size - currentOffset;
		int value = (int) (bytesToBeReceived/4096);
		switch(value){
		case 0: return bytesToBeReceived;
		case 1: return 4096;
		case 2: return 4096*2;
		case 3: return 4096*3;
		case 4:
		default: return 4096*4;
		}
		// TODO Auto-generated method stub
	}

	private long[] getOffsetAndLength(int[] packetTracker) {
		long [] offsetAndLength = new long[2];
		int startOffset = -1;
		int endOffset = -1;
		int loopLength = packetTracker.length;
		for (int i = 0; i< loopLength; i++){
			if(packetTracker[i] == 0){
				startOffset = i;
				break;
			}
		}
		for (int j = startOffset + 1; j< loopLength; j++){
			if(packetTracker[j] == 1){
				endOffset = j;
				break;
			}
		}
		
		
		offsetAndLength[0] = configFile.getOffset() + (4096 * startOffset);
		if(endOffset == -1) {
			offsetAndLength[1] = (4-startOffset) * 4096;
		}
		else {
			offsetAndLength[1] = (endOffset - startOffset) * 4096;
		}
		
		
		return offsetAndLength;
	}
	
	private int[] writeDataToFile(int[] packetTracker, List<byte[]> listDataResponse, long currentOffset) {
		byte[] dataForFile;
		DataResponse dataResponse;
		Object o;
		long offset;
		long length;
		int[] packetTracker1 = packetTracker;
		Deframer deframer = new Deframer();
		for(int i = 0; i<listDataResponse.size(); i++) {
			dataResponse = (DataResponse) deframer.deframer(listDataResponse.remove(0));
			offset = dataResponse.getOffset();
			dataForFile = dataResponse.getData();
			length = dataResponse.getLength();
			fileManager.writeFromPosition(offset, length, dataForFile);
			packetTracker1[(int) ((offset - currentOffset)/4096)] = 1;
			
		}
		return packetTracker1;
		
	}
	private List<byte[]> receiveDataResponse(long lengthForDataRequest) {
		byte[] dataResponse = new byte[4249];
		int loopcounter = (int) lengthForDataRequest/4096;
		if(loopcounter == 0) loopcounter = 1;
		DatagramPacket datagramPacket = new DatagramPacket(dataResponse, dataResponse.length);
		List<byte[]> listDataResponse = new LinkedList<>();
		for (int i = 0; i< loopcounter; i++){
			try {
				datagramSocket.receive(datagramPacket);
				listDataResponse.add(dataResponse);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return listDataResponse;
		
	}
	private void sendDataRequest(long offsetForDataRequest, long lengthForDataRequest, int identifier) {
		DataRequest dataRequest = new DataRequest(offsetForDataRequest, lengthForDataRequest, identifier);
		Framer framer = new Framer();
		byte [] dataRequestPayload;
		dataRequestPayload = framer.framer(dataRequest);
		DatagramPacket datagramPacket = new DatagramPacket(dataRequestPayload, dataRequestPayload.length, clientAddress, clientPort);
		try {
			datagramSocket.send(datagramPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
	}
	
	
	
	
}
