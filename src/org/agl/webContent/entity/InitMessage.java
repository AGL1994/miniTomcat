package org.agl.webContent.entity;

import java.lang.reflect.Method;

public class InitMessage {
	/**
	 * �洢ɨ�����·��
	 */
	private String classPathPackage;
	/**
	 * �洢ɨ���ļ����ļ���
	 */
	private String fileName;
	
	/**
	 * ��ŷ���
	 * @return
	 */
	private Method method;
	
	/**
	 * ��������class
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
