package org.agl.webContent.handler;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.agl.webContent.annotation.Param;
import org.agl.webContent.annotation.RequestUrl;
import org.agl.webContent.entity.InitMessage;
import org.agl.webContent.entity.MethodParamsNameHandler;
import org.agl.webContent.entity.Request;
import org.agl.webContent.entity.Response;
import org.agl.webContent.exception.FoundPathException;
import org.agl.webContent.exception.InitPropertiesException;
import org.agl.webContent.utils.ServerInit;
import org.agl.webContent.utils.StringUtils;

public class RequestHandler {
	private Request request = null;
	private Response response;
	private InitMessage initMessage = new InitMessage();

	public RequestHandler(Request request, Response response) {
		this.request = request;
		this.response = response;
	}

	/**
	 * 获取请求路径 GET /test/Controller/testController HTTP/1.1
	 * 
	 * @return Object[] Object[0] methodReturnParam Object[1] method
	 * @throws FoundPathException
	 */
	public void doRequest() throws FoundPathException {
		/**
		 * 本机项目路径
		 */
		StringBuffer classPath = new StringBuffer(System.getProperty("user.dir"));
		classPath.append("\\src").append("\\").append(ServerInit.getController());
		String formatPath = classPath.toString().replaceAll("\\\\", "/");
		/**
		 * 判断controller 包是否存在
		 */
		File file = new File(formatPath);
		if (!file.exists()) {
			throw new FoundPathException("没有找到 : " + formatPath);
		}
		/**
		 * 存储包的路径
		 */
		initMessage.setClassPathPackage(formatPath);
		try {
			getControllerByAnnotation(file);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws ClassNotFoundException
	 */
	private void getControllerByAnnotation(File file) throws ClassNotFoundException {
		String filesName[] = file.list();
		List<String> className = new ArrayList<String>();
		for (String fname : filesName) {
			StringBuffer name = new StringBuffer();
			name.append(ServerInit.getController()).append(".").append(fname.substring(0, fname.indexOf(".")));
			className.add(name.toString());
		}
		
		/**
		 * 扫描包
		 */
		Object[] params = null; // 实际调用方法的参数
		Method invokeMethod = null; // 实际调用的方法
		Class<?> claszz = null; // 实际调用方法的对象
		for (int i = 0 ; i < className.size() ; i++) {
			/**
			 * 先将保存扫描的文件名
			 */
			initMessage.setFileName(filesName[i]);
			/**
			 * 首先判断方法是否已经找到
			 */
			if (invokeMethod != null && claszz != null) {
				break;
			}
			claszz = Class.forName(className.get(i));
			initMessage.setClaszz(claszz);
			/**
			 * 先判断是否有类上的 @RequestUrl 注解 使用了类级注解的，先匹配类注解，不匹配则执行下一次循环
			 */
			RequestUrl classAnno = claszz.getAnnotation(RequestUrl.class);
			if (classAnno != null) {
				String checkUrl = classAnno.value() + "/";
				if (request.getUrl().indexOf(checkUrl) == -1) {
					continue;
				}
			}
			Method[] methods = claszz.getDeclaredMethods();
			for (Method method : methods) {
				RequestUrl annotation = method.getAnnotation(RequestUrl.class);

				StringBuffer checkUrl = new StringBuffer(annotation.value());
				/**
				 * 判断是否有类级过滤 如果有 则过滤条件 = 类级+ / + 方法级
				 */
				if (classAnno != null) {
					checkUrl.insert(0, classAnno.value() + "/");
				}

				if (checkUrl.toString().equals(request.getUrl())) {
					/**
					 * 如果 method 没有参数，url匹配则直接调用
					 */
					int methodParamCount = method.getParameterCount();
					if (methodParamCount == 0) {
						invokeMethod = method;
						break; // 退出循环
					}

					/**
					 * 首先获取请求的参数 通过参数过滤筛选条件
					 */
					Map<String, Object> reqParamMap = request.getParamMap();
					int reqParamCount = reqParamMap == null ? 0 : reqParamMap.size();
					//

					/**
					 * 首先判断请求参数与方法参数是否一致 且等于0
					 */
					if (reqParamCount == 0 && methodParamCount > 0) {
						continue;
					}
					/**
					 * 存放参数
					 */
					Map<String, Object> methodParamMap = new HashMap<String, Object>();

					/**
					 * 存放实际调用方法的参数
					 */

					if (methodParamCount > 0) {
						/**
						 * 获取参数对应请求的值
						 */
						Parameter[] ps = method.getParameters();

						/**
						 * 存入方法
						 */
						initMessage.setMethod(method);
						/**
						 * 检查 并 格式化请求参数类型
						 */
						params = checkAndFormatParam(reqParamMap, methodParamMap, ps);

						/**
						 * 参数返回
						 */
						if (params == null || params.length < 1) { // 未找到参数，进行下一次循环
							continue;
						} else { // 找到参数，则推出循环，执行方法
							invokeMethod = method;
							break;
						}
					}
				}
			}
		}

		/**
		 * 循环结束后 method为空（没有找到对应的请求路径） 则返回404
		 */
		if (invokeMethod == null || claszz == null) {
			request.setErrorCode(404);
			request.setErrorMessage("请求路径不存在");
			return;
		}
		/**
		 * 判断返回值
		 */
		Class<?> returnType = null;
		if (!"viod".equals(invokeMethod.getReturnType())) {
			returnType = invokeMethod.getReturnType();
		}
		/**
		 * 执行方法
		 */
		try {
			/**
			 * 无参 无返回
			 */
			if (invokeMethod.getParameterCount() <= 0 && returnType == null) {
				invokeMethod.invoke(claszz.newInstance());
			}

			/**
			 * 无参 有返回 将返回值存入request对象中
			 */
			if (invokeMethod.getParameterCount() <= 0 && returnType != null) {
				request.setReturnObj(invokeMethod.invoke(claszz.newInstance()));
			}

			/**
			 * 有参 无返回
			 */
			if (invokeMethod.getParameterCount() > 0 && returnType == null) {
				invokeMethod.invoke(claszz.newInstance(), params);
			}
			/**
			 * 有参 有返回
			 */
			if (invokeMethod.getParameterCount() > 0 && returnType != null) {
				request.setReturnObj(invokeMethod.invoke(claszz.newInstance(), params));
			}
			request.setMethod(invokeMethod); // 将方法存入request对象
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

	/**
	 * 检查 和 格式化 请求参数
	 * 
	 * @param params
	 * @param reqParamMap
	 * @param methodParamMap
	 * @param ps
	 * @return
	 */
	private Object[] checkAndFormatParam(Map<String, Object> reqParamMap, Map<String, Object> methodParamMap,
			Parameter[] ps) {
		/**
		 * 临时存放参数
		 */
		List<Object> paramList = new ArrayList<Object>();
		/**
		 * 用来记录 通过实际方法的参数名 来匹配请求参数
		 */
		int findTimes = 0;
		/**
		 * 检查 格式化参数
		 */
		
		for (Parameter p : ps) {
			Param[] annoParams = p.getAnnotationsByType(Param.class);
			String reqParam = "";
			/**
			 * 首先判断参数是否加了 param 注解 优先匹配 param注解的参数 如果有些参数没使用注解，则判断参数名是否与请求参数名一致
			 * 如果以上都没有找到，则判断参数是否为Request 或则 Response 对象 若都不存在 则返回false
			 */
			if (annoParams != null && annoParams.length > 0) { // 首先匹配注解
				reqParam = (String) reqParamMap.get(annoParams[0].value());
			} else {
				/**
				 * 查找参数名
				 * 由于 java反射 p.getName 会改变 方法参数的的参数名
				 * 所以不能用反射获取到方法的参数名 can not user  reqParam = (String) reqParamMap.get(p.getName());
				 * methodParamsName 按照没有注解的参数的顺序保存参数名
				 */
				List<String> methodParamsName = MethodParamsNameHandler.getMethodParamsName(initMessage,request);
				if(methodParamsName != null && methodParamsName.size() > 0){
					reqParam = (String)reqParamMap.get(methodParamsName.get(findTimes++));
				}
				
				if (StringUtils.isNull(reqParam)) {
					if (p.getType() == Request.class) { // 参数名匹配不成功 怎判断参数是否为
														// Request对象 找到则进行下一次循环
						paramList.add(request);
						continue;
					} else if (p.getType() == Response.class) { // 未找到怎判断是否为
																// Response 对象
																// 找到则进行下一次循环
						paramList.add(response);
						continue;
					} else { // 都未找到，则返回 null
						return null;
					}
				}
			}

			/**
			 * 首先判断之前匹配的参数 是否存在
			 */
			if (StringUtils.isNull(reqParam)) {
				return null;
			}
			/**
			 * 判断参数 是否和请求参数类型一致 一致则存入list 不一致则进行类型转化 转换失败 返回 null 将参数名 类型存入参数map
			 */
			if (reqParam.getClass() == p.getType()) { // 若 实参 与 请求参数类型 一致，保存参数值
				paramList.add(reqParam);
			} else {// 若 实参 与 请求参数类型 不一致，进行类型转换，转换成功 则保存 失败 返回false
				/**
				 * 将请求参数 类型 转成实际参入类型 String to anyDataType except String Date
				 * byte short Boolean
				 */
				Object param = null; // 保存临时转换后的参数
				try {
					switch (p.getType().toString()) {
					// String to int
					case "int":
						param = Integer.parseInt(reqParam);
						break;

					// String char
					case "char":
						if (reqParam.length() == 1) {
							param = reqParam.charAt(0);
						} else {
							return null;
						}
						break;

					// String to long
					case "long":
						param = Long.parseLong(reqParam);
						break;

					// String to boolean
					case "boolean":
						if ("true".equals(reqParam)) {
							param = true;
						} else if ("false".equals(reqParam)) {
							param = false;
						} else {
							return null;
						}
						break;

					// String to float
					case "float":
						param = Float.parseFloat(reqParam);
						break;

					// String to double
					case "double":
						param = Double.parseDouble(reqParam);
						break;

					// String to Integer
					case "java.lang.Integer":
						param = Integer.valueOf(reqParam);
						break;

					// String to Character
					case "java.lang.Character":
						if (reqParam.length() == 1) {
							param = Character.valueOf(reqParam.charAt(0));
						} else {
							return null;
						}
						break;

					// String to Long
					case "java.lang.Long":
						param = Long.valueOf(reqParam);
						break;

					// String to Float
					case "java.lang.Float":
						param = Float.valueOf(reqParam);
						break;

					// String to Double
					case "java.lang.Double":
						param = Double.valueOf(reqParam);
						break;

					// default
					default:
						return null;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				paramList.add(param);
			}
		}

		/**
		 * 循环结束，参数类型转换完成
		 */
		if (paramList == null || paramList.size() < 1) {
			return null;
		}
		/**
		 * 将临时参数放入参数数组
		 */
		Object[] params = new Object[paramList.size()];
		paramList.toArray(params);
		return params;
	}

}
