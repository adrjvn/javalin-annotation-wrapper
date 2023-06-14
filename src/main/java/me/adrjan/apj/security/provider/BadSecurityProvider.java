package me.adrjan.apj.security.provider;

import io.javalin.http.Context;
import me.adrjan.apj.security.SecurityResponse;

public class BadSecurityProvider implements SecurityProvider {

    @Override
    public SecurityResponse provide(Context context) {
        return context.header("authentication") != null ? SecurityResponse.ACCEPT : SecurityResponse.DENY;
    }
}