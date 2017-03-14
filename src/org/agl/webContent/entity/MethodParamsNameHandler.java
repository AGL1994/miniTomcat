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
 * 获取方法的参数名
 * 通过 io 读取方法的参数名
 * @author AgL19
 *
 */
public class MethodParamsNameHandler {
	/**
	 * 用于存储参数的参数名
	 */
	private static List<String> methodParamsList = null;

	
	public static List<String> getMethodParamsName(InitMessage initMessage,Request request){
		/**
		 * 如果 methodParamsMap 不存在，切 initMessage为空，则返回null
		 */
		if(initMessage == null){
			return null;
		}
		/**
		 * 如果 methodParamsMap存在，在直接返回methodParamsMap
		 */
		if(methodParamsList != null){
			return methodParamsList;
		}
		
		/**
		 * 读取文件，保存文件名
		 */
		try {
			return getMethodParamsNameHandler(initMessage,request);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 读取方法参数名
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
//		 * 文件路径
//		 */
//		StringBuffer filePath = new StringBuffer();
//		filePath.append(initMessage.getClassPathPackage()).append("/").append(initMessage.getFileName());
//		/**
//		 * 读取文件
//		 */
//		File methodFile = new File(filePath.toString());
//		if(!methodFile.exists()){ //判断文件是否存在
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
		 * 首先匹配方法名
		 */
		String checkStr = "";
		boolean passCheck = false;
		while((checkStr = bf.readLine()) != null){
			System.out.println(checkStr);
//			/**
//			 * 先找出匹配的方法名
//			 */
//			if(checkStr.indexOf(initMessage.getMethod().getName()) != -1){
//				passCheck = true;
//			}
//			
//			/**
//			 * 然后匹配返回值类型
//			 */
//			if(passCheck){
//				passCheck = checkReturnType(checkStr,initMessage.getMethod());
//			}
//			
//			/**
//			 * 匹配参数
//			 */
//			if(passCheck){
//				methodParamsList = checkParams(checkStr,initMessage.getMethod());
//			}
		}
		/**
		 * 构造方法定义
		 * 获取方法的关键字
		 * 获取返回值类型
		 * 获取方法名
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
	 * 匹配返回值
	 * @param checkStr
	 * @param method
	 * @return
	 */
	private static boolean checkReturnType(String checkStr, Method method){
		/**
		 * 首先匹配简写类型
		 */
		if(checkStr.indexOf(method.getReturnType().getSimpleName()) != -1){
			return true;
		}
		
		/**
		 * 然后匹配全写类型
		 */
		if(checkStr.indexOf(method.getReturnType().getName()) != -1){
			return true;
		}
		return false;
	}
	
	private static List<String> checkParams(String checkStr, Method method){
		/**
		 * 截取参数字符串
		 */
		return null;
	}
}
