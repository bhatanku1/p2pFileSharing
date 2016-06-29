package server;

import java.awt.Frame;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

import frames.DownloadRequest;
import frames.UploadRequest;
import marshallDemarshall.Deframer;

public class Server {
	private final static Logger LOGGER = Logger.getLogger(Server.class.getName()); 

	private  DatagramSocket datagramSocket;
	private DatagramPacket datagramPacket;
	private Object object;
	private byte[] request;
	private boolean isValid;
	private UploadRequest uploadRequest;
	private String command; // needs to be replaced with enum
	
	
	public Server(int port){
		try {
			datagramSocket = new DatagramSocket(port);
			request = new byte[280];
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public void listenToClients(){
		while(true){
			   LOGGER.info("Waiting for clients...");

			datagramPacket = new DatagramPacket(request, request.length);
			try {
				datagramSocket.receive(datagramPacket);
				LOGGER.info("Recieved reques from client");
				uploadRequest = verifyPacket(request);
				if(uploadRequest != null) {
					// Initiate a new dataConnection for the client and send the response
					LOGGER.info("Request type: " + uploadRequest.type());
					newDataConnection(uploadRequest);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void newDataConnection(UploadRequest uploadRequest) {
		LOGGER.info("Client request received from port: " + datagramPacket.getPort() + "A new data connection started for client");
		DataConnection dataConnection = new DataConnection(uploadRequest.getFileName(), uploadRequest.getSha1(), uploadRequest.getSize(), datagramPacket.getPort(), datagramPacket.getAddress());
		
	}
	private UploadRequest verifyPacket(byte[] request) {
		Deframer deframer = new Deframer();
		object = deframer.deframer(request);
		if (object instanceof DownloadRequest) {
			command = "downloadRequest"; // to be replaced with enum
			return null;
		}
		else if (object instanceof UploadRequest) {
			command = "uploadRequest";
			return (UploadRequest) object;
		}
		else return null;
	}
}
