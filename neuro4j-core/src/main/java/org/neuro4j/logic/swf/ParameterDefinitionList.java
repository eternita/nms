package org.neuro4j.logic.swf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterDefinitionList {
	ParameterDefinition[] input() default {};
	ParameterDefinition[] output() default {};
}
