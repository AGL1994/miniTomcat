package org.agl.webContent.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.agl.webContent.entity.Request;

/**
 * @description ��������Ϣ����ΪResquest����
 * @author AgL
 *
 */
public class RequestUtil {
	
	private String urlInfoStr;
	
	private Request request = new Request();
	
	public RequestUtil(String urlInfoStr){
		this.urlInfoStr = urlInfoStr;
	}
	
	/**
	 * 
	 * @return
	 */
	public Request getRequest(){
		request.setActionMethod(getActionMethod());
		request.setUrl(getReqPath());
		request.setParamMap(getParamMap());
		return request;
	}
	
	/**
	 * ��ȡ����ʽ
	 * GET or POST
	 * @return
	 */
	private String getActionMethod(){
		if(StringUtils.isNull(urlInfoStr)){
			return null;
		}
		String upperUrl = urlInfoStr.toUpperCase();
		
		if(upperUrl.indexOf("GET") != -1){
			return "GET";
		}else if(upperUrl.indexOf("POST") != -1){
			return "POST";
		}else{
			return null;
		}
	}
	
	/**
	 * ��ȡ �������
	 * @return
	 */
	private Map<String,Object> getParamMap(){
		String paramInfoStr = "";
		
		if("GET".equals(request.getActionMethod())){
			String reqPath = urlInfoStr.split(" ")[1];
			if(reqPath.indexOf("?") != -1){
				paramInfoStr = reqPath.substring(reqPath.indexOf("?")+1);
				reqPath = reqPath.substring(0, reqPath.indexOf("?"));
			}
		}else if("POST".equals(request.getMethod())){
			String paramStr []  = urlInfoStr.split("\n");
			paramInfoStr = paramStr[paramStr.length-1];
		}else{
			return null;//�˴�Ӧ���쳣  ����ʽ����ȷ
		}
		
		if(StringUtils.isNull(paramInfoStr)){
			return null;
		}
		/**
		 * pramCount [ name=%91%E8,aex=%e5%f3 ]
		 */
		String pramCount [] = paramInfoStr.trim().split("&");
		
		/**
		 * paramsMap
		 * key:name
		 * value:%91%E8
		 */
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		
		for(String par : pramCount){
			String param [] = par.split("=");
			try {
				/**
				 * ��ȡ�ַ������ʽ
				 * �������ȼ�
				 * 
				 * request.setContentCharser
				 *     V
				 * �����ļ��������˱����ʽ 
				 *     V
				 * Ĭ��  utf-8   
				 */
				String chatset = "utf-8";
				if(!StringUtils.isNull(request.getContentCharset())){
					chatset = request.getContentCharset();
				}else if(!StringUtils.isNull(ServerInit.getRequestChartSet())){
					chatset = ServerInit.getRequestChartSet();
				}
				paramsMap.put(param[0], java.net.URLDecoder.decode(param[1],chatset));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return paramsMap;
		
	}
	
	/**
	 * ��ȡ ����·��
	 */
	private String getReqPath(){
		String method = getActionMethod();
		String reqPath = urlInfoStr.split(" ")[1];
		if("GET".equals(method)){
			if(reqPath.indexOf("?") != -1){
				reqPath = reqPath.substring(0, reqPath.indexOf("?"));
			}
		}else if("POST".equals(method)){
			return reqPath;
		}
		return reqPath.substring(1); //  ȥ��urlǰ��� /
	}
}
