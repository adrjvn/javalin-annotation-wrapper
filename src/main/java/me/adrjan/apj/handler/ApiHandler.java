package me.adrjan.apj.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import me.adrjan.apj.annotation.RequestBody;
import me.adrjan.apj.rest.EndPointProvider;
import me.adrjan.apj.result.RequestResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

@AllArgsConstructor
public class ApiHandler implements EndPointHandler {

    private final Gson gson;

    @SneakyThrows
    @Override
    public RequestResult handle(Context context, EndPointProvider instance, Method method) {
        if (shouldHandleBody(method)) {
            try {
                if (context.body().isEmpty())
                    return new RequestResult(HttpStatus.UNPROCESSABLE_CONTENT, null);
                return new RequestResult(HttpStatus.OK, method.invoke(instance, this.processParameters(true, context, method)));
            } catch (Exception e) {
                return new RequestResult(HttpStatus.UNPROCESSABLE_CONTENT, null);
            }
        }
        return new RequestResult(HttpStatus.OK, method.invoke(instance, this.processParameters(false, context, method)));
    }

    @SneakyThrows
    protected Object[] processParameters(boolean body, Context context, Method method) {
        int increment = body ? 1 : 0;

        Parameter[] inputs = Arrays.stream(method.getParameters()).filter(it -> it.isAnnotationPresent(me.adrjan.apj.annotation.Parameter.class)).toArray(Parameter[]::new);
        Object[] parameters = new Object[inputs.length + 1 + increment];
        parameters[0] = context;

        if (body) {
            Object requestBody = this.gson.fromJson(context.body(), method.getParameters()[1].getType());
            parameters[increment] = requestBody;
        }
        int i = 1 + increment;

        for (Parameter parameter : inputs) {
            parameters[i] = parameter.getAnnotation(me.adrjan.apj.annotation.Parameter.class).type() == me.adrjan.apj.annotation.Parameter.Type.PATH
                    ? context.pathParamAsClass(getParameterName(parameter), parameter.getType()).get() :
                    context.queryParamAsClass(getParameterName(parameter), parameter.getType()).get();
            i++;
        }
        return parameters;
    }

    protected boolean shouldHandleBody(Method method) {
        return method.getParameters()[1].isAnnotationPresent(RequestBody.class);
    }

    protected String getParameterName(Parameter parameter) {
        return parameter.getAnnotation(me.adrjan.apj.annotation.Parameter.class).value();
    }
}