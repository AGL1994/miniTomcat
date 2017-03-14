package org.agl.webContent.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.agl.webContent.exception.InitPropertiesException;

public class ServerInit {
	/**
	 * 请求编码编码方式
	 */
	private static final StringBuffer requestChartSet = new StringBuffer();
	
	/**
	 * controller控制包路径
	 */
	private static final StringBuffer controllrPackage = new StringBuffer();
	
	static{
		Properties p = new Properties();
		InputStream input = Object.class.getResourceAsStream("/AGLWebContent.properties");
		try {
			p.load(input);
			/**
			 * 若没有指定编码格式，默认utf-8
			 */
			try{
				requestChartSet.append(p.getProperty("charset").trim());
			}catch(Exception e1){
				requestChartSet.append("utf-8");
			}
			
			/**
			 * 若没有指定controller的包，则抛出InitPropertiesException异常
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
				throw new InitPropertiesException("没有找到任何controller包的配置");
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
