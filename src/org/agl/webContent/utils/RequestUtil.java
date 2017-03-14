package org.agl.webContent.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.agl.webContent.entity.Request;

/**
 * @description 将请求信息解析为Resquest对象
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
	 * 获取请求方式
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
	 * 获取 请求参数
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
			return null;//此处应有异常  请求方式不正确
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
				 * 获取字符编码格式
				 * 编码优先级
				 * 
				 * request.setContentCharser
				 *     V
				 * 配置文件中配置了编码格式 
				 *     V
				 * 默认  utf-8   
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
	 * 获取 请求路径
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
		return reqPath.substring(1); //  去掉url前面的 /
	}
}
