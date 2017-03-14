package org.agl.webContent.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @description 确定返回值类型
 * String default 返回字符串 | 
 * json 返回json 支持Map list自动转json  |
 * view 返回某个视图页面  |
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
