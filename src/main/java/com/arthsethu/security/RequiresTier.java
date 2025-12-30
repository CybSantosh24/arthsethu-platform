package com.arthsethu.security;

import com.arthsethu.model.SubscriptionTier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the minimum subscription tier required to access a method or class
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresTier {
    SubscriptionTier value();
    String message() default "Upgrade your subscription to access this feature";
}