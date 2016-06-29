package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import marshallDemarshall.Deframer;
import marshallDemarshall.Framer;
import fileOperation.FileManager;
import frames.DataRequest;
import frames.DataResponse;
/**
 * 
 * @author ankur bhatia
 * 
 *
 */
public class ClientUploadHanlder {
	private long offset;
	private long length;
	private final String fileName;
	private final int port;
	private final int identifier;
	private final FileManager fileManager;
	private  DataRequest dataRequest;
	private DatagramSocket datagramSocket;
	private DatagramPacket datagramPacket;
	private final InetAddress inetAddress;
	
	/**
	 * ClientUploadHanlder Constructor
	 * @param offset
	 * @param length
	 * @param fileName
	 * @param port
	 * @param dataRequest
	 * @param inetAddress
	 * @param datagramSocket
	 */
	public ClientUploadHanlder(long offset, long length, String fileName,	int port, DataRequest dataRequest, InetAddress inetAddress, DatagramSocket datagramSocket) {
		this.offset = offset;
		this.length = length;
		this.fileName = fileName;
		this.dataRequest = dataRequest;
		this.port = port;
		this.inetAddress = inetAddress;
		this.fileManager = new FileManager(fileName);
		this.datagramSocket = datagramSocket;
		this.identifier = dataRequest.getIdentifier();
	}
	
	/**
	 * 
	 */
	public void uploadProcess(){
		int readBytesFromFile;
		int numberOfPacketsToSend;
		boolean isDataRequest;
		byte [] dataToSend;
		byte [] payloadToSendToServer;
		while(true) {
			/**
			 * The length signifies the number of bytes that server wants the client to send from the offset. 
			 * In 1 packet a maximum of 4096 bytes is to be sent
			 * The server sends the length as a multiple of 4096
			 * Only the last packet could be of byte length which is less than 4096 and hence not a multiple of 4096
			 * So if we have for eg; 8192 bytes to be sent, it will be sent in 2 packets
			 * And if we have 8095 bytes to be sent, the server will first send a request for 8192 bytes, offset 0, length 8192 
			 * followed by another request of offset 8192, length 3 
			 */
			readBytesFromFile = 4096;
			numberOfPacketsToSend = (int) (length/4096);
			if(numberOfPacketsToSend == 0) {
				numberOfPacketsToSend = 1;
				readBytesFromFile = (int) length;
			}
			
			for(int i = 0; i< numberOfPacketsToSend; i++){
				/**
				 * 1. Read data from the file (offset to length)
				 * 2. Generate the dataResponsePacket
				 * 3. send the packet to the server
				 * 4. increase the offset by the number of bytes read
				 */
				dataToSend = fileManager.readFromPosition((int) offset, readBytesFromFile);
				payloadToSendToServer = generateDataResponsePacket(dataToSend);
				try {
					sendPacketToServer(payloadToSendToServer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				offset += readBytesFromFile;
			}
			/**
			 * 1. Receive the Packet
			 * 		i. If it is a termination request, stop the uploadProcess
			 *      ii. If it is a dataRequest, update the length and the offset
			 */
			try {
				isDataRequest = receiveDataRequestAndUpdateLengthOffset();
				if (isDataRequest == false){
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * The function receives a packet from the server and checks if it is a dataRequest or a Termination Request
	 *@return true if it is a datarequest and update the length and offset
	 *@return false if it is a termination request 
	 *@return false if the server sends any other packet
	 *@return false if the identifier of the session does not match
	 *  @throws IOException
	 */
	private boolean receiveDataRequestAndUpdateLengthOffset() throws IOException {
		byte [] packetReceivedFromServer = new byte[280];
		Deframer deframer = new Deframer();
		Object object;
		datagramPacket = new DatagramPacket(packetReceivedFromServer, packetReceivedFromServer.length);
		datagramSocket.receive(datagramPacket);
		object = deframer.deframer(packetReceivedFromServer);
		if(! (object instanceof DataRequest)) {
			return false;
		}
		else if(object instanceof DataRequest) {
			dataRequest = (DataRequest) object;
			if(!(identifier == dataRequest.getIdentifier())) {
				return false;
			}
			length = (int) dataRequest.getLength();
			offset = (int)dataRequest.getOffset();
			return true;
		}
		else
			//object is an instance of other class;
			// Throw a new error
			return false;
	}

	/**
	 * The function sends the data from the client to the server
	 * @see DatagramSocket
	 * @see DatagramPacket
	 * @param payloadToSendToServer
	 * @throws IOException
	 */
	private void sendPacketToServer(byte[] payloadToSendToServer) throws IOException {
		datagramPacket = new DatagramPacket(payloadToSendToServer, payloadToSendToServer.length, inetAddress, port);
		datagramSocket.send(datagramPacket);
	}
	/**
	 * The function generates a payload to be sent to the server
	 * This is done by serialization acheived through the Framer 
	 * @see Framer
	 * @param dataToSend
	 * @return serialized packet that can be sent to the server
	 */
	private byte[] generateDataResponsePacket(byte[] dataToSend) {
		byte[] payloadToSend = new byte[4106];
		Framer framer = new Framer();
		DataResponse dataResponse = new DataResponse(offset, length, dataToSend, identifier);
		payloadToSend = framer.framer(dataResponse);
		return payloadToSend;
		
	}
	
	

}
