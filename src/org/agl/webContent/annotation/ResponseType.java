package org.agl.webContent.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @description ȷ������ֵ����
 * String default �����ַ��� | 
 * json ����json ֧��Map list�Զ�תjson  |
 * view ����ĳ����ͼҳ��  |
 * @author AgL19
 *
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface ResponseType {
	String value();
}
