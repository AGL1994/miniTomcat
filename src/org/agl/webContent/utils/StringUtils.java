package org.agl.webContent.utils;

/**
 * Stringπ§æﬂ¿‡
 * @author AgL19
 *
 */
public class StringUtils {
	
	/**
	 * if(null || "" || length<1)
	 * return true
	 * else 
	 * return false 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str){
		if(str == null){
			return true;
		}
		if("".equals(str)){
			return true;
		}
		if(str.length() <= 0){
			return true;
		}
		return false;
	}
}
