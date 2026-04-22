package com.lawbackend2.lawbackend2.license.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireLicense {

    String[] modules() default {};

    String message() default "该功能需要授权才能使用";
}
