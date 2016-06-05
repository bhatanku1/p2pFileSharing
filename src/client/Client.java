package client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

import marshallDemarshall.Deframer;
import marshallDemarshall.Framer;
import fileOperation.FileManager;
import frames.DataRequest;

import frames.UploadRequest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;


/**
 * 
 * @author ankur bhatia
 * The following class takes care of the client part for the progressive File Transfer
 * It initiates the upload and the download from the client side
 *
 */
public class Client {
	private final InetAddress SERVERADDRESS; // the server address
	private final int SERVERPORT;            // the server port for the control connection
	private int dataPort;					 // the server port for data connection
	private final String fileName;           // file to be uploaded or downloaded
	private DatagramSocket datagramSocket;   // datagram socket in the client for the UDP connection
	private DatagramPacket datagramPacket;   // datagram packet to be send and received at the client
	private final static Logger LOGGER = Logger.getLogger(Client.class.getName()); 

	/**
	 * Client Constructor
	 * @param serverAddress 
	 * @param serverPort
	 * @param fileName
	 * @throws Nullpointer Exception incase the filename, serverAddress or serverPort is null
	 *         IllegalArgumentException in case the port < 0 or length of the filename > 256 
	 */
	public Client(InetAddress serverAddress, int serverPort, String fileName){
		this.SERVERADDRESS = checkNotNull(serverAddress);
		this.SERVERPORT = checkNotNull(serverPort);
		checkArgument(serverPort > 0);
		this.fileName = checkNotNull(fileName);
		checkArgument(fileName.length() < 256);
		
	}
	
	/**
	 * 
	 * @throws IOException
	 * The function initiates the progressive upload
	 */
	public void pftUpload() throws IOException{
		byte [] uploadRequestPayload;
		DataRequest dataRequest;
		uploadRequestPayload = generateUploadRequest();
		checkNotNull(uploadRequestPayload);
		
		sendPacketToServer(uploadRequestPayload);
		dataRequest = checkResponseFromServer();
		checkNotNull(dataRequest);
		
		
	}
	/**
	 * 
	 * @return null if dataRequest is not received from the server
	 *         DataRequest object
	 * @throws IOException
	 */
	private DataRequest checkResponseFromServer() throws IOException {
		LOGGER.info("Waiting for DataRequest from the server");

		Object o;
		byte[] dataRequest = new byte[280];
		Deframer deframer = new Deframer();
		datagramPacket = new DatagramPacket(dataRequest, dataRequest.length);
		datagramSocket.receive(datagramPacket);
		dataPort = datagramPacket.getPort();
		o = deframer.deframer(dataRequest);
		if (o instanceof DataRequest){
			return (DataRequest) o;	
		}
		
		return null;
		
	}
	
	/**
	 * 
	 * @param payload that is to be sent to the server
	 * @throws IOException
	 */
	private void sendPacketToServer(byte[] payload) throws IOException {
		LOGGER.info("Sending UploadRequest to the server");

		datagramSocket = new DatagramSocket();
		datagramPacket = new DatagramPacket(payload, payload.length, SERVERADDRESS, SERVERPORT);
		datagramSocket.send(datagramPacket);
		LOGGER.info(" UploadRequest sent to the server");

	}
	
	/**
	 * The function creates a byte buffer to be sent through the socket
	 * @author ankur bhatia
	 * @return null if the file doesnot exist
	 *         uploadRequestPayload(a serialized payload) that can be sent to the socket 
	 *        
	 */
	private byte[] generateUploadRequest() {
		LOGGER.info("Generating Upload Request for:" + fileName);
		long size;
		byte [] sha1;
		byte [] uploadRequestPayload;
		FileManager fileManager = new FileManager(fileName);
		if(fileManager.fileExits() == false) {
			LOGGER.info("File not found");
			return null;
		}
		size = fileManager.getSize();
		LOGGER.info("Size of the file: " + fileName + ": " + size);
		sha1 = fileManager.getHash("SHA-1", 0, size);
		fileManager.fileClose(); // todo : implement the fileClose
		UploadRequest uploadRequest = new  UploadRequest(fileName, sha1, size);
		Framer framer = new Framer();
		uploadRequestPayload = framer.framer(uploadRequest);
		return uploadRequestPayload;
		
		
		
	}
}
