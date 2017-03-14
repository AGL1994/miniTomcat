package org.agl.webContent.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servers {

	public static void main(String[] args) {
		try {
			initServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void initServer() throws IOException{
		ServerSocket serverSocket = new ServerSocket(8001);
		while(true){
			Socket reqSocket = serverSocket.accept();
			Thread reqThread = new RequestThread(reqSocket);
			reqThread.start();
		}
	}
	
}
