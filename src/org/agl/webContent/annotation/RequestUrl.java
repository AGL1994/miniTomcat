package org.agl.webContent.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RequestUrl {
	String value();
	String method() default "";
}
