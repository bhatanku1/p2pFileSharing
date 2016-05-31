package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import client.Client;

public class Test {
	public static void main(String [] args){
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			Client c = new Client(inetAddress, 7000, "filename");
			c.pftUpload();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
