package me.adrjan.apj.security;

import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import me.adrjan.apj.annotation.ApiEndpoint;

import java.lang.reflect.Method;

@AllArgsConstructor
public class EndpointSecurityHandler implements SecurityHandler {

    private final SecurityFactory securityFactory;

    @Override
    public SecurityResponse handle(Method method, Context context) {
        if (!isEndpoindAuthenticable(method)) return SecurityResponse.ACCEPT;
        return this.securityFactory.get(method).provide(context);
    }

    protected boolean isEndpoindAuthenticable(Method method) {
        return method.getAnnotation(ApiEndpoint.class).requireAuthentication();
    }
}