package com.theinit.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class HelloVerticle extends AbstractVerticle {
    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.get("/hello").handler(rc -> {
            rc.response()
                    .putHeader("content-type", "application/json")
                    .end(new JsonObject().put("greeting", "Hello World!").encode());
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
