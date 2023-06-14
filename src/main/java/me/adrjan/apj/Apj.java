package me.adrjan.apj;

import com.google.gson.Gson;
import io.javalin.Javalin;
import lombok.Getter;
import me.adrjan.apj.adapter.JavalinAdapter;
import me.adrjan.apj.handler.ApiHandler;
import me.adrjan.apj.registry.EndpointRegistry;
import me.adrjan.apj.security.EndpointSecurityHandler;
import me.adrjan.apj.security.SecurityFactory;

public class Apj {

    protected final Gson gson;
    @Getter
    private final SecurityFactory securityFactory;
    protected final EndpointSecurityHandler endpointSecurityHandler;
    @Getter
    private final EndpointRegistry endpointRegistry;

    public Apj(Gson gson, Javalin javalin) {
        this.gson = gson;
        this.securityFactory = new SecurityFactory();
        this.endpointSecurityHandler = new EndpointSecurityHandler(new SecurityFactory());
        this.endpointRegistry = new EndpointRegistry(new JavalinAdapter(javalin, this.gson
                , new ApiHandler(this.gson)
                , new EndpointSecurityHandler(new SecurityFactory())));
    }
}