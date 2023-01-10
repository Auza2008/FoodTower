/*
Author:SuMuGod
Date:2022/7/10 3:11
Project:foam Reborn
*/
package me.dev.foam.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface NMSL {
    public byte priority() default 1;
}
