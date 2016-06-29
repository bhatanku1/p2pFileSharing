package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import server.Server;
import client.Client;
import client.ClientUploadHanlder;
import frames.DataRequest;

public class ServerTemp {
	private final static Logger LOGGER = Logger.getLogger(Application.class.getName()); 

	public static void main(String[] args){

		InetAddress address;
		

		if(args.length < 1) {
			   LOGGER.info("Error in command");
			return;
		}
	   if(args[0].equals("server")){
			//start server
		   LOGGER.info("Starting server");
		   Server server = new Server(25200);
		   server.listenToClients();
		}
	   if(args[0].equals("client")) {
		   LOGGER.info("Starting client");

		   //start client
			try {
				address = InetAddress.getLocalHost();
				   Client client = new Client(address, 25200, "C:\\Users\\ankur\\test.txt");
				   client.pftUpload();
				   

			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		  
		   
	   }
		
		
	}

}
