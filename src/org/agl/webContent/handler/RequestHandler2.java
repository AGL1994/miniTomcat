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
	 * 获取请求路径
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
			//此处应有异常
		}
		/**
		 * /test/Controller/testController?type=12
		 */
		String reqPath = urlInfoStr.split(" ")[1];
		/**
		 * 判断url中是否存在	数据
		 * 判断是否为GET/POST请求
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
			return null;//此处应有异常  请求方式不正确
		}
		/**
		 * 本机项目路径
		 */
		StringBuffer classPath = new StringBuffer(System.getProperty("user.dir"));
		classPath.append("\\src").append("\\").append(reqPackage);
		String formatPath = classPath.toString().replaceAll("\\\\", "/");
		/**
		 * 判断请求地址路径是否存在
		 */
		File file = new File(formatPath);
		if(!file.exists()){
			System.out.println("找不到想要的package");
			return null;
			//此处应有异常
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
		 * 扫描包
		 */
		for(String name : className){
			Class<?> claszz =  Class.forName(name);
			/**
			 * 先判断是否有类上的 @RequestUrl 注解
			 * 使用了类级注解的，先匹配类注解，不匹配则执行下一次循环
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
				 * 判断是否有类级过滤
				 * 如果有  则过滤条件 = 类级+ / + 方法级
				 */
				if(classAnno != null){
					checkUrl.insert(0, classAnno.value() + "/");
				}
				
				if(checkUrl.toString().equals(mapping)){
					/**
					 * 首先获取请求的参数
					 * 通过参数过滤筛选条件
					 */
					Map<String,Object> reqParamMap = getUrlParam(paramInfoStr);
					int reqParamCount = reqParamMap == null ? 0 : reqParamMap.size();
//					
					/**
					 * 判断是否有参数
					 * 得到方法参数
					 */
					int methodParamCount = method.getParameterCount();
					/**
					 * 首先判断请求参数与方法参数是否一致 且等于0
					 */
					if(reqParamCount == 0 && methodParamCount > 0){
						continue;
					}
//					/**
//					 * 如果方法参数个数 大于 请求参数个数+2 返回
//					 * 
//					 */
//					if(methodParamCount > reqParamCount){
//						continue;
//					}
					/**
					 * 存放参数
					 */
					Map<String,Object> methodParamMap = new HashMap<String,Object>();
					
					/**
					 * 存放实际调用方法的参数
					 */
					Object [] params = null;

					if(methodParamCount > 0){
						/**
						 * 获取参数对应请求的值
						 */
						Parameter [] ps = method.getParameters(); 
						
						/**
						 * 检查 并 格式化请求参数类型
						 */
						params = checkAndFormatParam(reqParamMap,methodParamMap,ps);
						
						if(params == null || params.length < 1){
							System.out.println("没有找到想应的路径"); //此处应有异常
							return null;
						}
					}
					
					/**
					 * 判断返回值
					 */
					Class<?> returnType = null;
					if(!"viod".equals(method.getReturnType())){
						returnType = method.getReturnType();
					}
					/**
					 * 执行方法
					 */
					try {
						/**
						 * 无参 无返回
						 */
						if(methodParamCount <= 0 && returnType == null){
							method.invoke(claszz.newInstance());
						}
						
						/**
						 * 无参 有返回
						 */
						if(methodParamCount <= 0 && returnType != null){
							returnObj[0] = method.invoke(claszz.newInstance());
						}
						
						/**
						 * 有参 无返回
						 */
						if(methodParamCount > 0 && returnType == null){
							method.invoke(claszz.newInstance(), params);
						}
						/**
						 * 有参 有返回
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
		//此处应有异常
		return null;
	}
	
	/**
	 * 检查 和 格式化 请求参数
	 * @param params
	 * @param reqParamMap
	 * @param methodParamMap
	 * @param ps
	 * @return
	 */
	private Object [] checkAndFormatParam(Map<String,Object> reqParamMap,Map<String,Object> methodParamMap, Parameter [] ps){
		/**
		 * 临时存放参数
		 */
		List<Object> paramList = new ArrayList<Object>();
		/**
		 * 检查 格式化参数
		 */
		for(Parameter p : ps){
			Param [] annoParams = p.getAnnotationsByType(Param.class);
//			if(annoParams == null || annoParams.length < 1){
//				System.out.println("此处应该有异常"); // throw new ParamAnnotationException
//			}
			/**
			 * 判断参数 是否和请求参数一致
			 * 将参数名 类型存入参数map
			 */
			String reqParam = reqParamMap.get(annoParams[0].value()).toString();
			if(reqParam != null){ //判断注解配置的参数名 是否在请求参数中存在
				if(reqParam.getClass() == p.getType()){ // 若 实参 与 请求参数类型 一致，保存参数值
					paramList.add(reqParam);
				}else{// 若 实参 与 请求参数类型 不一致，进行类型转换，转换成功 则保存  失败 返回false
					/**
					 * 将请求参数 类型 转成实际参入类型
					 * String to anyDataType except String Date byte short Boolean
					 */
					Object param = null; //保存临时转换后的参数
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
		 * 循环结束，参数类型转换完成
		 */
		if(paramList == null || paramList.size() < 1){
			return null;
		}
		/**
		 * 将临时参数放入参数数组
		 */
		Object [] params = new Object[paramList.size()];
		paramList.toArray(params);
		return params;
	}
	/**
	 * 获取请求的参数
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
	 * 获取连接方式
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
	 * Post请求 获取请求参数
	 * 参数字符串  urlParam
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
