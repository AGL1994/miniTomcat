package org.agl.webContent.entity;

import java.lang.reflect.Method;

public class InitMessage {
	/**
	 * 存储扫描包的路径
	 */
	private String classPathPackage;
	/**
	 * 存储扫描文件的文件名
	 */
	private String fileName;
	
	/**
	 * 存放方法
	 * @return
	 */
	private Method method;
	
	/**
	 * 保存类型class
	 * @return
	 */
	private Class<?> claszz;
	
	
	
	public Class<?> getClaszz() {
		return claszz;
	}
	public void setClaszz(Class<?> claszz) {
		this.claszz = claszz;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String getClassPathPackage() {
		return classPathPackage;
	}
	public void setClassPathPackage(String classPathPackage) {
		this.classPathPackage = classPathPackage;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
