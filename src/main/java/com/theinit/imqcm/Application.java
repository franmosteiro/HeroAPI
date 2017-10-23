package com.theinit.imqcm;

import com.theinit.imqcm.verticles.HerosVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(HerosVerticle.class);

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        Verticle marvelHeroesVerticle = new HerosVerticle();

        final int port = Integer.getInteger("http.port", 8082);

        vertx.deployVerticle(marvelHeroesVerticle, new DeploymentOptions().setConfig(loadConfig()), res -> {
            if (res.succeeded())
                logger.info("Main service is running at " + port + " port...");
            else
                res.cause().printStackTrace();
        });
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
}
