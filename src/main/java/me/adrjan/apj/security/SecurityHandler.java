package me.adrjan.apj.security;

import io.javalin.http.Context;

import java.lang.reflect.Method;

public interface SecurityHandler {

    SecurityResponse handle(Method method, Context context);
}