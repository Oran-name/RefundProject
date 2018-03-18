package com.ccservice.bus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *   解释字段的名字
 * @author Administrator
 *
 */
@Target({ElementType.FIELD})
@Retention(value = RetentionPolicy.CLASS) 
@Documented
public @interface FieldName {
	String value() ;
}
