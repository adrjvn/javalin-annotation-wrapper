package me.adrjan.apj.registry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.adrjan.apj.adapter.JavalinAdapter;
import me.adrjan.apj.annotation.ApiEndpoint;
import me.adrjan.apj.rest.EndPointProvider;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@AllArgsConstructor
public class EndpointRegistry {

    private final JavalinAdapter javalinAdapter;

    public void registerEndpoint(EndPointProvider endPointProvider) {
        for (Method method : Arrays.stream(endPointProvider.getClass().getDeclaredMethods()).filter(it -> it.isAnnotationPresent(ApiEndpoint.class)).toList()) {
            ApiEndpoint endpoint = method.getAnnotation(ApiEndpoint.class);
            this.javalinAdapter.register(endpoint.type(), endPointProvider, method);
            log.info("Registered endpoint " + endpoint.path());
        }
    }
}