package me.adrjan.apj.security;

import me.adrjan.apj.annotation.ApiEndpoint;
import me.adrjan.apj.security.provider.BadSecurityProvider;
import me.adrjan.apj.security.provider.SecurityProvider;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityFactory {

    private final Map<Class<? extends SecurityProvider>, SecurityProvider> providers = new ConcurrentHashMap<>();

    public SecurityFactory() {
        this.register(new BadSecurityProvider());
    }

    public void register(SecurityProvider securityProvider) {
        this.providers.put(securityProvider.getClass(), securityProvider);
    }

    public SecurityProvider get(Class<? extends SecurityProvider> clazz) {
        return this.providers.get(clazz);
    }

    public SecurityProvider get(Method method) {
        return this.get(method.getAnnotation(ApiEndpoint.class).securityProvider());
    }
}