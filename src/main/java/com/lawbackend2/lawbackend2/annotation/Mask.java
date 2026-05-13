package com.lawbackend2.lawbackend2.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mask {

    MaskType value() default MaskType.DEFAULT;
}