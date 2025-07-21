package com.easyspi.meta;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface TemplateExt {

    String bizCode();


    String scenario() default "";

}
