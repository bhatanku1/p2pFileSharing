package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import marshallDemarshall.Deframer;
import marshallDemarshall.Framer;
import client.Client;
import frames.UploadRequest;

public class Test {
	public static void main(String [] args){
		byte[] sha1 = new byte[20];
		long size = 2566;
		Framer f = new Framer();
		Deframer df = new Deframer();
		byte[] payload;
		UploadRequest uploadRequestAfterDeserialize;
		UploadRequest uploadRequest = new UploadRequest("ankur", sha1, size);
		payload = f.framer(uploadRequest);
		uploadRequestAfterDeserialize = (UploadRequest) df.deframer(payload);
		if(uploadRequestAfterDeserialize.getFileName().equals("ankur")) {
			System.out.println("Test Passed");
		}
		else {
			System.out.println("Test Failed");
		}
	}
}
