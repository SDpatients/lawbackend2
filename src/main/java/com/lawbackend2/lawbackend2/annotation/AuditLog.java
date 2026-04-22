package com.lawbackend2.lawbackend2.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    String module() default "";

    String moduleName() default "";

    String operationType() default "";

    String operationName() default "";

    String businessType() default "";

    boolean recordParams() default true;

    boolean recordData() default false;

    String description() default "";
}
