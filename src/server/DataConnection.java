package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

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
	private final static Logger LOGGER = Logger.getLogger(DataConnection.class.getName()); 

	public DataConnection(String fileName, byte[] sha, long size, int clientPort, InetAddress clientAddress) {
		this.fileName = fileName;
		this.sha = sha;
		this.size = size;
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
		configFile = new ConfigFile(fileName + ".pft", sha, size);
		fileManager = new FileManager(fileName + ".temp");
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
		LOGGER.info("The current offset is: " + currentOffset);
		while(currentOffset < size){
			lengthForDataRequest = getLengthBasedOnSize(currentOffset);
			packetTracker = setPacketTracker(lengthForDataRequest);
			LOGGER.info("PacketTracker set to: " + packetTracker);
			sendDataRequest(currentOffset, lengthForDataRequest, identifier);
			
			listDataResponse = receiveDataResponse(lengthForDataRequest);
			
			packetTracker = writeDataToFile(packetTracker, listDataResponse, currentOffset);
			isCycleOver = CheckPacketTracker(packetTracker);
			while(!isCycleOver){
				LOGGER.info("Some packets missed for offset: " + currentOffset);
				parametersOffsetAndLength = getOffsetAndLength(packetTracker);
				sendDataRequest(parametersOffsetAndLength[0], parametersOffsetAndLength[1], identifier);
				listDataResponse = receiveDataResponse(parametersOffsetAndLength[1]);
				packetTracker = writeDataToFile(packetTracker, listDataResponse, currentOffset);
				isCycleOver = CheckPacketTracker(packetTracker);
			}	
			LOGGER.info("Received all packets for offset:" + currentOffset + " and length: " + lengthForDataRequest);
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
		LOGGER.info("getLengthBasedOnSize: Length based on the size and offset:");
		switch(value){
		case 0: 
				
				LOGGER.info("Length: " + bytesToBeReceived);
				return bytesToBeReceived;
		case 1: 				
				LOGGER.info("Length: " + 4096);
				return 4096;
		case 2:
        		LOGGER.info("Length: " + 8192);
				return 4096*2;
		case 3: 
				LOGGER.info("Length: " + 12288);
				return 4096*3;
		case 4:
		default:
				LOGGER.info("Length: " + 16384);
				return 4096*4;
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
		LOGGER.info("Size of list to be written: " + listDataResponse.size());
		int loopcounter = listDataResponse.size();
		for(int i = 0; i<loopcounter; i++) {
			dataResponse = (DataResponse) deframer.deframer(listDataResponse.remove(0));
			offset = dataResponse.getOffset();
			dataForFile = dataResponse.getData();
			length = dataResponse.getLength();
			if(offset < currentOffset) continue;
			fileManager.writeFromPosition(offset, length, dataForFile);
			LOGGER.info("Offset and currentOffset is: " + offset + " " + currentOffset + " loopcounter is: " + i);
			packetTracker1[(int) ((offset - currentOffset)/4096)] = 1;
			
		}
		return packetTracker1;
		
	}
	private List<byte[]> receiveDataResponse(long lengthForDataRequest) {
		byte[] dataResponse = new byte[4249];
		int loopcounter = (int) lengthForDataRequest/4096;
		if(loopcounter == 0) loopcounter = 1;
		//Test Code
		Deframer deframer = new Deframer();
		DataResponse drTest;
		
		//
		DatagramPacket datagramPacket = new DatagramPacket(dataResponse, dataResponse.length);
		List<byte[]> listDataResponse = new LinkedList<>();
		LOGGER.info("Number of expected packets is : " + loopcounter);
		for (int i = 0; i< loopcounter; i++){
			try {
				LOGGER.info("Waiting for DataResponse....");
				datagramSocket.setSoTimeout(5);
				datagramSocket.receive(datagramPacket);
				//Test Code
				LOGGER.info("Got DataResponse");
				drTest = (DataResponse) deframer.deframer(dataResponse);
				LOGGER.info("DRTEST: " + drTest.getOffset());
				
				//
				listDataResponse.add(dataResponse);
				LOGGER.info("Received packet: " + i);
				LOGGER.info("Size of the list is: " + listDataResponse.size());
			} 
			catch (IOException e) {
				e.printStackTrace();
				return listDataResponse;

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
			LOGGER.info("DataRequest sent: with Offset: " + offsetForDataRequest + " Length: " + lengthForDataRequest);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
	}
	
	
	
	
}
