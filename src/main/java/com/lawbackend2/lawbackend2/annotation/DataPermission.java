package com.lawbackend2.lawbackend2.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    String value() default "createUserId";

    String moduleType() default "";

    String permissionType() default "";
}
