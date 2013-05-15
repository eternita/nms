package org.neuro4j.logic.swf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterDefinition {
	public String name();

	public String type() default "java.lang.String";

	public boolean isOptional() default true;
}
