package com.theinit.verticles;

import io.vertx.core.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class App extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private HttpServer server;

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(App.class.getCanonicalName(), new DeploymentOptions().setConfig(loadConfig()));
    }

    private static JsonObject loadConfig() throws IOException {
        JsonObject config;
        try (InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/imqcm-config.json")) {
            try (Scanner scanner = new Scanner(resourceAsStream)) {
                config = new JsonObject(scanner.useDelimiter("\\A").next());
            }
        }
        return config;
    }

    @Override
    public void start(Future<Void> fut) {
        int port = config().getInteger("http.port", 8080);

        logger.info("Starting server on port {}", port);
        Router router = createRoutes(vertx);

        this.server = vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(port, result -> {
                        if (result.succeeded()) {
                            fut.complete();
                        } else {
                            fut.fail(result.cause());
                        }
                    }
                );
    }

    private Router createRoutes(Vertx vertx) {
        Router router = Router.router(vertx);

        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("Content-Type", "text/plain");
            response.write("Hi, I'm imqcm and i'm your worst nightmare }=) !!!!");
            response.end();
            routingContext.vertx().setTimer(1000, tid ->  routingContext.response().end());
        });

        router.route("/conf").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("Content-Type", "application/json");
            response.write(config().toString());
            response.end();
            routingContext.vertx().setTimer(1000, tid ->  routingContext.response().end());
        });

        return router;
    }

    @Override
    public void stop() throws Exception {
        logger.info("stopping");
        //appController.close();
        server.close();
    }
}
