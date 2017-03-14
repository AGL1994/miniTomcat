package org.agl.webContent.handler;

public class ResponseHandler {
	private static final StringBuilder responseContent = new StringBuilder();
	static {
		responseContent.append("HTTP/1.1 200 OK");
		responseContent.append("Content-Type: text/html; charset=utf-8");
		responseContent.append("Content-Length:%s");
		responseContent.append("Date:%s");
		responseContent.append("Server:This is a simulation web container");
	}
	
	public void returnView(){
		
	}
}
