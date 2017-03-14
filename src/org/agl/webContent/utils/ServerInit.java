package org.agl.webContent.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.agl.webContent.exception.InitPropertiesException;

public class ServerInit {
	/**
	 * ���������뷽ʽ
	 */
	private static final StringBuffer requestChartSet = new StringBuffer();
	
	/**
	 * controller���ư�·��
	 */
	private static final StringBuffer controllrPackage = new StringBuffer();
	
	static{
		Properties p = new Properties();
		InputStream input = Object.class.getResourceAsStream("/AGLWebContent.properties");
		try {
			p.load(input);
			/**
			 * ��û��ָ�������ʽ��Ĭ��utf-8
			 */
			try{
				requestChartSet.append(p.getProperty("charset").trim());
			}catch(Exception e1){
				requestChartSet.append("utf-8");
			}
			
			/**
			 * ��û��ָ��controller�İ������׳�InitPropertiesException�쳣
			 */
			try{
				controllrPackage.append(p.getProperty("controller").trim());
			}catch(Exception e1){
			}
			
		} catch (IOException  e) {
			e.printStackTrace();
		}
	}
	
	public static String getController(){
		if(StringUtils.isNull(controllrPackage.toString())){
			try {
				throw new InitPropertiesException("û���ҵ��κ�controller��������");
			} catch (InitPropertiesException e) {
				e.printStackTrace();
			}
		}
		return controllrPackage.toString();
	}
	
	public static String getRequestChartSet(){
		return requestChartSet.toString();
	}
}
