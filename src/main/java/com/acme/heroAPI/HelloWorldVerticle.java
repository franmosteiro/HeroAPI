package com.acme.heroAPI;

import io.vertx.core.Vertx;

public class HelloWorldVerticle {

    public static void main(String[] args) {
        // Create an HTTP server which simply returns "Hello World!" to each request.
        Vertx.vertx().createHttpServer().requestHandler(req -> req.response().end("Hello World!")).listen(8080);
    }

}
