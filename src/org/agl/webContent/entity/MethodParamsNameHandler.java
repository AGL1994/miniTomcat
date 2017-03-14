package org.agl.webContent.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import org.agl.webContent.utils.ServerInit;
import org.agl.webContent.utils.StringUtils;

/**
 * ��ȡ�����Ĳ�����
 * ͨ�� io ��ȡ�����Ĳ�����
 * @author AgL19
 *
 */
public class MethodParamsNameHandler {
	/**
	 * ���ڴ洢�����Ĳ�����
	 */
	private static List<String> methodParamsList = null;

	
	public static List<String> getMethodParamsName(InitMessage initMessage,Request request){
		/**
		 * ��� methodParamsMap �����ڣ��� initMessageΪ�գ��򷵻�null
		 */
		if(initMessage == null){
			return null;
		}
		/**
		 * ��� methodParamsMap���ڣ���ֱ�ӷ���methodParamsMap
		 */
		if(methodParamsList != null){
			return methodParamsList;
		}
		
		/**
		 * ��ȡ�ļ��������ļ���
		 */
		try {
			return getMethodParamsNameHandler(initMessage,request);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ��ȡ����������
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	private static List<String> getMethodParamsNameHandler(InitMessage initMessage,Request request) throws IOException{
//		System.out.println(initMessage.getClaszz().getResource(initMessage.getFileName().replaceAll(".java", ".class")).getPath());
//		BufferedReader bb = new BufferedReader(new InputStreamReader(MethodParamsNameHandler.class.getResourceAsStream("")));
//		while(bb.readLine() != null){
//			System.out.println(bb.readLine());
//		}System.exit(0);
//		/**
//		 * �ļ�·��
//		 */
//		StringBuffer filePath = new StringBuffer();
//		filePath.append(initMessage.getClassPathPackage()).append("/").append(initMessage.getFileName());
//		/**
//		 * ��ȡ�ļ�
//		 */
//		File methodFile = new File(filePath.toString());
//		if(!methodFile.exists()){ //�ж��ļ��Ƿ����
//			return null;
//		}
//		System.out.println(initMessage.getClaszz().getResource(initMessage.getFileName().replaceAll(".java", ".class")).getPath());System.exit(0);
//		initMessage.getClaszz().getResourceAsStream("")
		String filePath = initMessage.getClaszz().getResource(initMessage.getFileName().replaceAll(".java", ".class")).toString().substring(6).replaceAll("\\\\", "/");
		File file = new File(filePath);
		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		byte[] bytes = new byte[99999];
		String oo = initMessage.getClaszz().getClassLoader().getResource(initMessage.getFileName().replaceAll(".java", ".class")).getPath().toString();
		System.out.println(oo);System.exit(0);
		InputStream input = initMessage.getClaszz().getClassLoader().getResourceAsStream(initMessage.getFileName().replaceAll(".java", ".class"));
		input.read(bytes);
		String requestStr = new String(bytes,"gbk");
		System.out.println(requestStr);System.exit(0);
		/**
		 * ����ƥ�䷽����
		 */
		String checkStr = "";
		boolean passCheck = false;
		while((checkStr = bf.readLine()) != null){
			System.out.println(checkStr);
//			/**
//			 * ���ҳ�ƥ��ķ�����
//			 */
//			if(checkStr.indexOf(initMessage.getMethod().getName()) != -1){
//				passCheck = true;
//			}
//			
//			/**
//			 * Ȼ��ƥ�䷵��ֵ����
//			 */
//			if(passCheck){
//				passCheck = checkReturnType(checkStr,initMessage.getMethod());
//			}
//			
//			/**
//			 * ƥ�����
//			 */
//			if(passCheck){
//				methodParamsList = checkParams(checkStr,initMessage.getMethod());
//			}
		}
		/**
		 * ���췽������
		 * ��ȡ�����Ĺؼ���
		 * ��ȡ����ֵ����
		 * ��ȡ������
		 */
		
//		StringBuffer methodDefine = new StringBuffer();
//		System.out.println(initMessage.getMethod());
//		System.out.println(initMessage.getMethod().getName());
//		System.out.println(initMessage.getMethod().getReturnType().getSimpleName());
//		System.out.println(initMessage.getMethod().getGenericReturnType());
//		String keyWord = initMessage.getMethod().get
		
		return null;
	}
	
	/**
	 * ƥ�䷵��ֵ
	 * @param checkStr
	 * @param method
	 * @return
	 */
	private static boolean checkReturnType(String checkStr, Method method){
		/**
		 * ����ƥ���д����
		 */
		if(checkStr.indexOf(method.getReturnType().getSimpleName()) != -1){
			return true;
		}
		
		/**
		 * Ȼ��ƥ��ȫд����
		 */
		if(checkStr.indexOf(method.getReturnType().getName()) != -1){
			return true;
		}
		return false;
	}
	
	private static List<String> checkParams(String checkStr, Method method){
		/**
		 * ��ȡ�����ַ���
		 */
		return null;
	}
}
