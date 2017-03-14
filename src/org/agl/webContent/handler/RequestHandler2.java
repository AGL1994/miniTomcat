package org.agl.webContent.handler;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agl.webContent.annotation.Param;
import org.agl.webContent.annotation.RequestUrl;
import org.agl.webContent.entity.Request;
import org.agl.webContent.utils.StringUtils;

public class RequestHandler2 {
	private String reqPackage = "controller";
	private Request request = null;
	
	public RequestHandler2(Request request){
		this.request = request;
	}
	/**
	 * ��ȡ����·��
	 * GET /test/Controller/testController HTTP/1.1
	 * @return Object[]
	 * Object[0]  methodReturnParam
	 * Object[1] method 
	 */
	public Object [] getUrl(String urlInfoStr){
		if(StringUtils.isNull(urlInfoStr)){
			return null;
		}
		if(StringUtils.isNull(reqPackage)){
			//�˴�Ӧ���쳣
		}
		/**
		 * /test/Controller/testController?type=12
		 */
		String reqPath = urlInfoStr.split(" ")[1];
		/**
		 * �ж�url���Ƿ����	����
		 * �ж��Ƿ�ΪGET/POST����
		 */
		String action = getMethod(urlInfoStr);
		String paramInfoStr = "";
		if("GET".equals(action)){
			if(reqPath.indexOf("?") != -1){
				paramInfoStr = reqPath.substring(reqPath.indexOf("?")+1);
				reqPath = reqPath.substring(0, reqPath.indexOf("?"));
			}
		}else if("POST".equals(action)){
			String paramStr []  = urlInfoStr.split("\n");
			paramInfoStr = paramStr[paramStr.length-1];
		}else{
			return null;//�˴�Ӧ���쳣  ����ʽ����ȷ
		}
		/**
		 * ������Ŀ·��
		 */
		StringBuffer classPath = new StringBuffer(System.getProperty("user.dir"));
		classPath.append("\\src").append("\\").append(reqPackage);
		String formatPath = classPath.toString().replaceAll("\\\\", "/");
		/**
		 * �ж������ַ·���Ƿ����
		 */
		File file = new File(formatPath);
		if(!file.exists()){
			System.out.println("�Ҳ�����Ҫ��package");
			return null;
			//�˴�Ӧ���쳣
		}
		try {
			return getControllerByAnnotation(file,reqPath.substring(1),urlInfoStr,paramInfoStr);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws ClassNotFoundException 
	 */
	private Object[] getControllerByAnnotation(File file, String mapping, String urlInfoStr, String paramInfoStr) throws ClassNotFoundException{
		Object [] returnObj = new Object[2];
		String filesName[] = file.list();
		List<String> className = new ArrayList<String>();
		for(String fname : filesName){
			StringBuffer name = new StringBuffer();
			name.append(reqPackage).append(".").append(fname.substring(0, fname.indexOf(".")));
			className.add(name.toString());
		}
		/**
		 * ɨ���
		 */
		for(String name : className){
			Class<?> claszz =  Class.forName(name);
			/**
			 * ���ж��Ƿ������ϵ� @RequestUrl ע��
			 * ʹ�����༶ע��ģ���ƥ����ע�⣬��ƥ����ִ����һ��ѭ��
			 */
			RequestUrl classAnno = claszz.getAnnotation(RequestUrl.class);
			if(classAnno != null){
				String checkUrl = classAnno.value() + "/";
				if(mapping.indexOf(checkUrl) == -1){
					continue;
				}
			}
			Method [] methods = claszz.getDeclaredMethods();
			for(Method method : methods){
				RequestUrl annotation = method.getAnnotation(RequestUrl.class);
				
				StringBuffer checkUrl = new StringBuffer(annotation.value());
				/**
				 * �ж��Ƿ����༶����
				 * �����  ��������� = �༶+ / + ������
				 */
				if(classAnno != null){
					checkUrl.insert(0, classAnno.value() + "/");
				}
				
				if(checkUrl.toString().equals(mapping)){
					/**
					 * ���Ȼ�ȡ����Ĳ���
					 * ͨ����������ɸѡ����
					 */
					Map<String,Object> reqParamMap = getUrlParam(paramInfoStr);
					int reqParamCount = reqParamMap == null ? 0 : reqParamMap.size();
//					
					/**
					 * �ж��Ƿ��в���
					 * �õ���������
					 */
					int methodParamCount = method.getParameterCount();
					/**
					 * �����ж���������뷽�������Ƿ�һ�� �ҵ���0
					 */
					if(reqParamCount == 0 && methodParamCount > 0){
						continue;
					}
//					/**
//					 * ��������������� ���� �����������+2 ����
//					 * 
//					 */
//					if(methodParamCount > reqParamCount){
//						continue;
//					}
					/**
					 * ��Ų���
					 */
					Map<String,Object> methodParamMap = new HashMap<String,Object>();
					
					/**
					 * ���ʵ�ʵ��÷����Ĳ���
					 */
					Object [] params = null;

					if(methodParamCount > 0){
						/**
						 * ��ȡ������Ӧ�����ֵ
						 */
						Parameter [] ps = method.getParameters(); 
						
						/**
						 * ��� �� ��ʽ�������������
						 */
						params = checkAndFormatParam(reqParamMap,methodParamMap,ps);
						
						if(params == null || params.length < 1){
							System.out.println("û���ҵ���Ӧ��·��"); //�˴�Ӧ���쳣
							return null;
						}
					}
					
					/**
					 * �жϷ���ֵ
					 */
					Class<?> returnType = null;
					if(!"viod".equals(method.getReturnType())){
						returnType = method.getReturnType();
					}
					/**
					 * ִ�з���
					 */
					try {
						/**
						 * �޲� �޷���
						 */
						if(methodParamCount <= 0 && returnType == null){
							method.invoke(claszz.newInstance());
						}
						
						/**
						 * �޲� �з���
						 */
						if(methodParamCount <= 0 && returnType != null){
							returnObj[0] = method.invoke(claszz.newInstance());
						}
						
						/**
						 * �в� �޷���
						 */
						if(methodParamCount > 0 && returnType == null){
							method.invoke(claszz.newInstance(), params);
						}
						/**
						 * �в� �з���
						 */
						if(methodParamCount > 0 && returnType != null){
							returnObj[0] = method.invoke(claszz.newInstance(), params);
						}
						returnObj[1] = method;
						return returnObj;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		//�˴�Ӧ���쳣
		return null;
	}
	
	/**
	 * ��� �� ��ʽ�� �������
	 * @param params
	 * @param reqParamMap
	 * @param methodParamMap
	 * @param ps
	 * @return
	 */
	private Object [] checkAndFormatParam(Map<String,Object> reqParamMap,Map<String,Object> methodParamMap, Parameter [] ps){
		/**
		 * ��ʱ��Ų���
		 */
		List<Object> paramList = new ArrayList<Object>();
		/**
		 * ��� ��ʽ������
		 */
		for(Parameter p : ps){
			Param [] annoParams = p.getAnnotationsByType(Param.class);
//			if(annoParams == null || annoParams.length < 1){
//				System.out.println("�˴�Ӧ�����쳣"); // throw new ParamAnnotationException
//			}
			/**
			 * �жϲ��� �Ƿ���������һ��
			 * �������� ���ʹ������map
			 */
			String reqParam = reqParamMap.get(annoParams[0].value()).toString();
			if(reqParam != null){ //�ж�ע�����õĲ����� �Ƿ�����������д���
				if(reqParam.getClass() == p.getType()){ // �� ʵ�� �� ����������� һ�£��������ֵ
					paramList.add(reqParam);
				}else{// �� ʵ�� �� ����������� ��һ�£���������ת����ת���ɹ� �򱣴�  ʧ�� ����false
					/**
					 * ��������� ���� ת��ʵ�ʲ�������
					 * String to anyDataType except String Date byte short Boolean
					 */
					Object param = null; //������ʱת����Ĳ���
					try{
						switch(p.getType().toString()){
							// String to int
							case "int" :
								param = Integer.parseInt(reqParam);
							break;
							
							//String char
							case "char" :
								if(reqParam.length() == 1){
									param = reqParam.charAt(0);
								}else{
									return null;
								}
							break;
							
							// String to long
							case "long" :
								param = Long.parseLong(reqParam);
							break;
							
							// String to boolean
							case "boolean" :
								if("true".equals(reqParam)){
									param = true;
								}else if ("false".equals(reqParam)){
									param = false;
								}else{
									return null;
								}
							break;
							
							// String to float
							case "float" :
								param = Float.parseFloat(reqParam);
							break;
							
							// String to double
							case "double" :
								param = Double.parseDouble(reqParam);
							break;
							
							// String to Integer
							case "java.lang.Integer" :
								param = Integer.valueOf(reqParam);
							break;
							
							// String to Character
							case "java.lang.Character" :
								if(reqParam.length() == 1){
									param = Character.valueOf(reqParam.charAt(0));
								}else{
									return null;
								}
							break;
							
							// String to Long
							case "java.lang.Long" :
								param = Long.valueOf(reqParam);
							break;
							
							// String to Float
							case "java.lang.Float" :
								param = Float.valueOf(reqParam);
							break;
							
							// String to Double
							case "java.lang.Double" :
								param = Double.valueOf(reqParam);
							break;
							
							//default
							default : return null;
						}
					}catch(Exception e){
						e.printStackTrace();
						return null;
					}
					paramList.add(param);
				}
			}else{
				return null;
			}
		}

		/**
		 * ѭ����������������ת�����
		 */
		if(paramList == null || paramList.size() < 1){
			return null;
		}
		/**
		 * ����ʱ���������������
		 */
		Object [] params = new Object[paramList.size()];
		paramList.toArray(params);
		return params;
	}
	/**
	 * ��ȡ����Ĳ���
	 * @param urlInfoStr
	 * @param paramInfoStr
	 * @return
	 */
	private Map<String, Object> getUrlParam(String paramInfoStr){
		Map<String,Object> paramMap = null;
		paramMap = getParamsMap(paramInfoStr);
		return paramMap;
	}
	
	/**
	 * ��ȡ���ӷ�ʽ
	 * GET or POST
	 * @param urlInfo
	 * @return
	 */
	private String getMethod(String urlInfoStr){
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
	 * Post���� ��ȡ�������
	 * �����ַ���  urlParam
	 * name=%91%E8&aex=%e5%f3
	 * @param paramInfoStr
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private Map<String,Object> getParamsMap(String paramInfoStr){
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
				paramsMap.put(param[0], java.net.URLDecoder.decode(param[1],"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return paramsMap;
	}
	
}
