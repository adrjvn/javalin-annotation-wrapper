package me.adrjan.apj.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.json.JavalinJackson;
import me.adrjan.apj.Apj;
import me.adrjan.apj.annotation.ApiEndpoint;
import me.adrjan.apj.annotation.Parameter;
import me.adrjan.apj.annotation.RequestBody;
import me.adrjan.apj.rest.EndPointProvider;

public class BootstrapTest {

    public static void main(String[] args) {
        Javalin javalin = Javalin.create(config -> {
            JavalinJackson javalinJackson = new JavalinJackson();
            javalinJackson.getMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            config.jsonMapper(javalinJackson);
        }).start(2137);
        Apj apj = new Apj(new Gson(), javalin);
        apj.getEndpointRegistry().registerEndpoint(new TestEndpoint());
    }

    public static class TestObject {
        private int i;
        private String cos;

        public TestObject(int i, String cos) {
            this.i = i;
            this.cos = cos;
        }
    }

    public static class TestEndpoint implements EndPointProvider {

        @ApiEndpoint(
                path = "/test",
                type = HandlerType.GET
        )
        public TestObject testEndpoint(Context context, @RequestBody TestObject testObject) {
            return testObject;
        }

        @ApiEndpoint(
                path = "/cos/{cos}",
                type = HandlerType.GET,
                rawResponse = true
        )
        public String cosEndpoint(Context context, @Parameter("cos") String cos, @Parameter(value = "aa", type = Parameter.Type.QUERY) int aa) {
            System.out.println("cos " + cos);
            System.out.println("aa " + aa);
            return cos;
        }

        @ApiEndpoint(
                path = "/complex/{cos}",
                type = HandlerType.GET,
                rawResponse = true
        )
        public TestObject cosEndpoint(Context context, @RequestBody TestObject testObject, @Parameter("cos") String cos, @Parameter(value = "aa", type = Parameter.Type.QUERY) int aa) {
            System.out.println("aa  xd " + aa + " bb " + cos);
            return testObject;
        }
    }
}