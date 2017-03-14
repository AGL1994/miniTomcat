package org.agl.webContent.entity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Request {
	/**
	 * 请求方式
	 * GET or POST
	 */
	private String actionMethod;
	
	/**
	 * 请求路径
	 */
	private String url;
	
	/**
	 * 请求参数
	 */
	private Map<String,Object> paramMap = new HashMap<String,Object>();
	
	/**
	 * 请求执行的方法
	 */
	private Method method;
	
	/**
	 * 请求返回值
	 */
	private Object returnObj;
	
	/**
	 * 编码格式
	 */
	private String contentCharset;
	
	/**
	 * 错误代码
	 */
	private int errorCode = 0;
	
	/**
	 * 错误信息
	 */
	private String errorMessage;
	
	/**
	 * 客户端地址
	 * @return
	 */
	private String clientUrl;
	
	/**
	 * 客户端/浏览器版本 信息
	 * @return
	 */
	private String clientBeta;
	
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getActionMethod() {
		return actionMethod;
	}

	public void setActionMethod(String actionMethod) {
		this.actionMethod = actionMethod;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getReturnObj() {
		return returnObj;
	}

	public void setReturnObj(Object returnObj) {
		this.returnObj = returnObj;
	}
	
	public void setContentCharset(String contentCharset){
		this.contentCharset = contentCharset;
	}
	
	public String getContentCharset(){
		return contentCharset;
	}

	/**
	 * 浏览器/客户端相关信息待实现
	 * @return
	 */
	public String getClientUrl() {
		return clientUrl;
	}

	/**
	 * 浏览器/客户端相关信息待实现
	 * @return
	 */
	public void setClientUrl(String clientUrl) {
		this.clientUrl = clientUrl;
	}

	/**
	 * 浏览器/客户端相关信息待实现
	 * @return
	 */
	public String getClientBeta() {
		return clientBeta;
	}

	/**
	 * 浏览器/客户端相关信息待实现
	 * @return
	 */
	public void setClientBeta(String clientBeta) {
		this.clientBeta = clientBeta;
	}
	
	
}
