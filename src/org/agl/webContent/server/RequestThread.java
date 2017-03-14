package org.agl.webContent.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.agl.webContent.entity.Request;
import org.agl.webContent.entity.Response;
import org.agl.webContent.exception.FoundPathException;
import org.agl.webContent.handler.RequestHandler;
import org.agl.webContent.handler.ResponseHandler;
import org.agl.webContent.utils.RequestUtil;
import org.agl.webContent.utils.ServerInit;
import org.agl.webContent.utils.StringUtils;

public class RequestThread extends Thread {
	private volatile Socket reqSocket = null;
	public RequestThread(Socket reqSocket) {
		this.reqSocket = reqSocket;
	}

	@Override
	public void run(){
		try {
			/**
			 * 读取请求携带的信息
			 */
			InputStream input =  reqSocket.getInputStream();
			byte[] bytes = new byte[1024];
			input.read(bytes);
			String requestStr = new String(bytes,ServerInit.getRequestChartSet());
			/**
			 * 解析请求 获得Resquest对象
			 */
			if(StringUtils.isNull(requestStr)){
				////////////////////
			}
			System.out.println(requestStr);
			RequestUtil req = new RequestUtil(requestStr);
			
			Request request = req.getRequest();
			System.out.println(reqSocket.getInetAddress());
//			request.setClientUrl(reqSocket.getInetAddress().toString());
			Response response = new Response();
			
			RequestHandler requestHandler = new RequestHandler(request,response);
			try {
				requestHandler.doRequest();
			} catch (FoundPathException e) {
				e.printStackTrace();
			}
			if(request.getErrorCode() != 0){
				System.out.println(request.getErrorMessage());
			}else{
				System.out.println(request.getReturnObj());
			}
			
			/**
			 * 响应
			 */
			ResponseHandler responseHandler = new ResponseHandler();
			
//			OutputStream output = reqSocket.getOutputStream();
//			output.write(responseContent.toString().getBytes());
//			output.flush();
//			input.close();
//			output.close();
//			reqSocket.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
