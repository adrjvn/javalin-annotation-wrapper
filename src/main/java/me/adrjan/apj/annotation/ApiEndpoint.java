package me.adrjan.apj.annotation;

import io.javalin.http.HandlerType;
import me.adrjan.apj.security.provider.BadSecurityProvider;
import me.adrjan.apj.security.provider.SecurityProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiEndpoint {
    String path();

    HandlerType type();

    boolean rawResponse() default false;

    boolean requireAuthentication() default false;

    Class<? extends SecurityProvider> securityProvider() default BadSecurityProvider.class;
}
