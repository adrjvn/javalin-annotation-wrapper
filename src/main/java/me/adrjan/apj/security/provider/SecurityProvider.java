package me.adrjan.apj.security.provider;

import io.javalin.http.Context;
import me.adrjan.apj.security.SecurityResponse;

public interface SecurityProvider {

    SecurityResponse provide(Context context);
}
