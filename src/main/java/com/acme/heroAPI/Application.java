package com.acme.heroAPI;

import com.acme.heroAPI.verticles.HerosVerticle;
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

        vertx.deployVerticle(marvelHeroesVerticle, new DeploymentOptions().setConfig(new JsonObject().put("http.port", 8082)), res -> {
            if (res.succeeded())
                logger.info("Main service is running ...");
            else
                res.cause().printStackTrace();
        });
    }
}
