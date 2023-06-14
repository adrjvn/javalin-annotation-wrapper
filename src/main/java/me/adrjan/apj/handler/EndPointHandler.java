package me.adrjan.apj.handler;

import io.javalin.http.Context;
import me.adrjan.apj.rest.EndPointProvider;
import me.adrjan.apj.result.RequestResult;

import java.lang.reflect.Method;

public interface EndPointHandler {

    RequestResult handle(Context context, EndPointProvider instance, Method method);
}