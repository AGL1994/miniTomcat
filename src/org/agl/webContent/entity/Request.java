package org.agl.webContent.entity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Request {
	/**
	 * ����ʽ
	 * GET or POST
	 */
	private String actionMethod;
	
	/**
	 * ����·��
	 */
	private String url;
	
	/**
	 * �������
	 */
	private Map<String,Object> paramMap = new HashMap<String,Object>();
	
	/**
	 * ����ִ�еķ���
	 */
	private Method method;
	
	/**
	 * ���󷵻�ֵ
	 */
	private Object returnObj;
	
	/**
	 * �����ʽ
	 */
	private String contentCharset;
	
	/**
	 * �������
	 */
	private int errorCode = 0;
	
	/**
	 * ������Ϣ
	 */
	private String errorMessage;
	
	/**
	 * �ͻ��˵�ַ
	 * @return
	 */
	private String clientUrl;
	
	/**
	 * �ͻ���/������汾 ��Ϣ
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
	 * �����/�ͻ��������Ϣ��ʵ��
	 * @return
	 */
	public String getClientUrl() {
		return clientUrl;
	}

	/**
	 * �����/�ͻ��������Ϣ��ʵ��
	 * @return
	 */
	public void setClientUrl(String clientUrl) {
		this.clientUrl = clientUrl;
	}

	/**
	 * �����/�ͻ��������Ϣ��ʵ��
	 * @return
	 */
	public String getClientBeta() {
		return clientBeta;
	}

	/**
	 * �����/�ͻ��������Ϣ��ʵ��
	 * @return
	 */
	public void setClientBeta(String clientBeta) {
		this.clientBeta = clientBeta;
	}
	
	
}
