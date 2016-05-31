package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import fileOperation.FileManager;
import frames.DataRequest;
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
	private final FileManager fileManager;
	private final DataRequest dataRequest;
	private DatagramSocket datagramSocket;
	private DatagramPacket datagramPacket;
	private final InetAddress inetAddress;
	
	/**
	 * 
	 * @param offset
	 * @param length
	 * @param fileName
	 * @param port
	 * @param dataRequest
	 * @param inetAddress
	 */
	public ClientUploadHanlder(long offset, long length, String fileName,	int port, DataRequest dataRequest, InetAddress inetAddress) {
		this.offset = offset;
		this.length = length;
		this.fileName = fileName;
		this.dataRequest = dataRequest;
		this.datagramSocket = datagramSocket;
		this.datagramPacket = datagramPacket;
		this.port = port;
		this.inetAddress = inetAddress;
		this.fileManager = new FileManager(fileName);
	}
	
	public void uploadProcess(){
		
		
		
		
		
	}
	
	

}
