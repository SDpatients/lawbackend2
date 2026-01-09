package com.lawbackend2.lawbackend2.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    int limit() default 100;
    
    long timeout() default 60;
    
    String key() default "";
}
