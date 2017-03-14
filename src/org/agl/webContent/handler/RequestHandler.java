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
	 * ��ȡ����·�� GET /test/Controller/testController HTTP/1.1
	 * 
	 * @return Object[] Object[0] methodReturnParam Object[1] method
	 * @throws FoundPathException
	 */
	public void doRequest() throws FoundPathException {
		/**
		 * ������Ŀ·��
		 */
		StringBuffer classPath = new StringBuffer(System.getProperty("user.dir"));
		classPath.append("\\src").append("\\").append(ServerInit.getController());
		String formatPath = classPath.toString().replaceAll("\\\\", "/");
		/**
		 * �ж�controller ���Ƿ����
		 */
		File file = new File(formatPath);
		if (!file.exists()) {
			throw new FoundPathException("û���ҵ� : " + formatPath);
		}
		/**
		 * �洢����·��
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
		 * ɨ���
		 */
		Object[] params = null; // ʵ�ʵ��÷����Ĳ���
		Method invokeMethod = null; // ʵ�ʵ��õķ���
		Class<?> claszz = null; // ʵ�ʵ��÷����Ķ���
		for (int i = 0 ; i < className.size() ; i++) {
			/**
			 * �Ƚ�����ɨ����ļ���
			 */
			initMessage.setFileName(filesName[i]);
			/**
			 * �����жϷ����Ƿ��Ѿ��ҵ�
			 */
			if (invokeMethod != null && claszz != null) {
				break;
			}
			claszz = Class.forName(className.get(i));
			initMessage.setClaszz(claszz);
			/**
			 * ���ж��Ƿ������ϵ� @RequestUrl ע�� ʹ�����༶ע��ģ���ƥ����ע�⣬��ƥ����ִ����һ��ѭ��
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
				 * �ж��Ƿ����༶���� ����� ��������� = �༶+ / + ������
				 */
				if (classAnno != null) {
					checkUrl.insert(0, classAnno.value() + "/");
				}

				if (checkUrl.toString().equals(request.getUrl())) {
					/**
					 * ��� method û�в�����urlƥ����ֱ�ӵ���
					 */
					int methodParamCount = method.getParameterCount();
					if (methodParamCount == 0) {
						invokeMethod = method;
						break; // �˳�ѭ��
					}

					/**
					 * ���Ȼ�ȡ����Ĳ��� ͨ����������ɸѡ����
					 */
					Map<String, Object> reqParamMap = request.getParamMap();
					int reqParamCount = reqParamMap == null ? 0 : reqParamMap.size();
					//

					/**
					 * �����ж���������뷽�������Ƿ�һ�� �ҵ���0
					 */
					if (reqParamCount == 0 && methodParamCount > 0) {
						continue;
					}
					/**
					 * ��Ų���
					 */
					Map<String, Object> methodParamMap = new HashMap<String, Object>();

					/**
					 * ���ʵ�ʵ��÷����Ĳ���
					 */

					if (methodParamCount > 0) {
						/**
						 * ��ȡ������Ӧ�����ֵ
						 */
						Parameter[] ps = method.getParameters();

						/**
						 * ���뷽��
						 */
						initMessage.setMethod(method);
						/**
						 * ��� �� ��ʽ�������������
						 */
						params = checkAndFormatParam(reqParamMap, methodParamMap, ps);

						/**
						 * ��������
						 */
						if (params == null || params.length < 1) { // δ�ҵ�������������һ��ѭ��
							continue;
						} else { // �ҵ����������Ƴ�ѭ����ִ�з���
							invokeMethod = method;
							break;
						}
					}
				}
			}
		}

		/**
		 * ѭ�������� methodΪ�գ�û���ҵ���Ӧ������·���� �򷵻�404
		 */
		if (invokeMethod == null || claszz == null) {
			request.setErrorCode(404);
			request.setErrorMessage("����·��������");
			return;
		}
		/**
		 * �жϷ���ֵ
		 */
		Class<?> returnType = null;
		if (!"viod".equals(invokeMethod.getReturnType())) {
			returnType = invokeMethod.getReturnType();
		}
		/**
		 * ִ�з���
		 */
		try {
			/**
			 * �޲� �޷���
			 */
			if (invokeMethod.getParameterCount() <= 0 && returnType == null) {
				invokeMethod.invoke(claszz.newInstance());
			}

			/**
			 * �޲� �з��� ������ֵ����request������
			 */
			if (invokeMethod.getParameterCount() <= 0 && returnType != null) {
				request.setReturnObj(invokeMethod.invoke(claszz.newInstance()));
			}

			/**
			 * �в� �޷���
			 */
			if (invokeMethod.getParameterCount() > 0 && returnType == null) {
				invokeMethod.invoke(claszz.newInstance(), params);
			}
			/**
			 * �в� �з���
			 */
			if (invokeMethod.getParameterCount() > 0 && returnType != null) {
				request.setReturnObj(invokeMethod.invoke(claszz.newInstance(), params));
			}
			request.setMethod(invokeMethod); // ����������request����
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
	 * ��� �� ��ʽ�� �������
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
		 * ��ʱ��Ų���
		 */
		List<Object> paramList = new ArrayList<Object>();
		/**
		 * ������¼ ͨ��ʵ�ʷ����Ĳ����� ��ƥ���������
		 */
		int findTimes = 0;
		/**
		 * ��� ��ʽ������
		 */
		
		for (Parameter p : ps) {
			Param[] annoParams = p.getAnnotationsByType(Param.class);
			String reqParam = "";
			/**
			 * �����жϲ����Ƿ���� param ע�� ����ƥ�� paramע��Ĳ��� �����Щ����ûʹ��ע�⣬���жϲ������Ƿ������������һ��
			 * ������϶�û���ҵ������жϲ����Ƿ�ΪRequest ���� Response ���� ���������� �򷵻�false
			 */
			if (annoParams != null && annoParams.length > 0) { // ����ƥ��ע��
				reqParam = (String) reqParamMap.get(annoParams[0].value());
			} else {
				/**
				 * ���Ҳ�����
				 * ���� java���� p.getName ��ı� ���������ĵĲ�����
				 * ���Բ����÷����ȡ�������Ĳ����� can not user  reqParam = (String) reqParamMap.get(p.getName());
				 * methodParamsName ����û��ע��Ĳ�����˳�򱣴������
				 */
				List<String> methodParamsName = MethodParamsNameHandler.getMethodParamsName(initMessage,request);
				if(methodParamsName != null && methodParamsName.size() > 0){
					reqParam = (String)reqParamMap.get(methodParamsName.get(findTimes++));
				}
				
				if (StringUtils.isNull(reqParam)) {
					if (p.getType() == Request.class) { // ������ƥ�䲻�ɹ� ���жϲ����Ƿ�Ϊ
														// Request���� �ҵ��������һ��ѭ��
						paramList.add(request);
						continue;
					} else if (p.getType() == Response.class) { // δ�ҵ����ж��Ƿ�Ϊ
																// Response ����
																// �ҵ��������һ��ѭ��
						paramList.add(response);
						continue;
					} else { // ��δ�ҵ����򷵻� null
						return null;
					}
				}
			}

			/**
			 * �����ж�֮ǰƥ��Ĳ��� �Ƿ����
			 */
			if (StringUtils.isNull(reqParam)) {
				return null;
			}
			/**
			 * �жϲ��� �Ƿ�������������һ�� һ�������list ��һ�����������ת�� ת��ʧ�� ���� null �������� ���ʹ������map
			 */
			if (reqParam.getClass() == p.getType()) { // �� ʵ�� �� ����������� һ�£��������ֵ
				paramList.add(reqParam);
			} else {// �� ʵ�� �� ����������� ��һ�£���������ת����ת���ɹ� �򱣴� ʧ�� ����false
				/**
				 * ��������� ���� ת��ʵ�ʲ������� String to anyDataType except String Date
				 * byte short Boolean
				 */
				Object param = null; // ������ʱת����Ĳ���
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
		 * ѭ����������������ת�����
		 */
		if (paramList == null || paramList.size() < 1) {
			return null;
		}
		/**
		 * ����ʱ���������������
		 */
		Object[] params = new Object[paramList.size()];
		paramList.toArray(params);
		return params;
	}

}
