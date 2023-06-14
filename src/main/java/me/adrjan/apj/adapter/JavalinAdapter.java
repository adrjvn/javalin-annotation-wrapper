package me.adrjan.apj.adapter;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import lombok.AllArgsConstructor;
import me.adrjan.apj.annotation.ApiEndpoint;

import me.adrjan.apj.handler.EndPointHandler;
import me.adrjan.apj.rest.EndPointProvider;
import me.adrjan.apj.result.RequestResult;
import me.adrjan.apj.security.SecurityHandler;
import me.adrjan.apj.security.SecurityResponse;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class JavalinAdapter {

    private final Javalin javalin;
    private final Gson gson;
    private final EndPointHandler apiHandler;
    private SecurityHandler securityHandler;

    public void register(HandlerType handlerType, EndPointProvider instance, Method method) {
        handle(handlerType, instance, method);
    }

    protected void handle(HandlerType handlerType, EndPointProvider instance, Method method) {
        ApiEndpoint apiEndpoint = method.getAnnotation(ApiEndpoint.class);
        javalin.addHandler(handlerType, apiEndpoint.path(), context -> context.future(() -> CompletableFuture.supplyAsync(() -> {
            if (this.securityHandler.handle(method, context) != SecurityResponse.ACCEPT) {
                context.status(HttpStatus.UNAUTHORIZED);
                return this.gson.toJson(new RequestResult(HttpStatus.UNAUTHORIZED, null));
            }
            RequestResult processorResult = this.apiHandler.handle(context, instance, method);
            if (processorResult.status() != HttpStatus.OK) {
                context.status(processorResult.status());
                return this.gson.toJson(new RequestResult(processorResult.status(), null));
            }
            context.status(HttpStatus.OK);
            return this.gson.toJson(apiEndpoint.rawResponse() ? processorResult.body() : processorResult);
        }).thenAcceptAsync(context::json)));
    }
}
