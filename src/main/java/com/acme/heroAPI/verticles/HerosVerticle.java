package com.acme.heroAPI.verticles;

import com.acme.heroAPI.entities.Hero;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class HerosVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(HerosVerticle.class);

    public static final String ASSETS = "/assets/*";
    public static final String API_GET = "/api/heros/:id";
    public static final String API_LIST_ALL = "/api/heros";
    public static final String API_CREATE= "/api/heros";
    public static final String API_UPDATE = "/api/heros/:id";
    public static final String API_DELETE = "/api/heros/:id";
    public static final String API_CONTEXT = "/api/heros*";

    private HttpServer server;
    private int port;
    private Map<Integer, Hero> heros = new LinkedHashMap<>();

    private void loadInitialData() throws IOException {
        port = config().getInteger("http.port", 8080);
        heros = createSomeData();
    }

    @Override
    public void start(Future<Void> fut) throws  IOException {

        loadInitialData();
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

        // CORS support
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));

        // routes
        router.route(ASSETS).handler(StaticHandler.create("assets"));
        router.route(API_CONTEXT).handler(BodyHandler.create());//enables the reading of the request body for all routes under “/api/whiskies”

        router.get(API_LIST_ALL).handler(this::getAll);
        router.post(API_CREATE).handler(this::addOne);
        router.get(API_GET).handler(this::getOne);
        router.put(API_UPDATE).handler(this::updateOne);
        router.delete(API_DELETE).handler(this::deleteOne);

        return router;
    }

    /*   API   */
    private void addOne(RoutingContext routingContext) {
        // Read the request's content and create an instance of Hero.
        final Hero hero = Json.decodeValue(routingContext.getBodyAsString(),
                Hero.class);
        // Add it to the backend map
        heros.put(hero.getId(), hero);

        // Return the created hero as JSON
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(hero));
    }

    private void getOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Hero hero = heros.get(idAsInteger);
            if (hero == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(hero));
            }
        }
    }

    private void updateOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Hero hero = heros.get(idAsInteger);
            if (hero == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                hero.setName(json.getString("name"));
                hero.setOrigin(json.getString("origin"));
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(hero));
            }
        }
    }

    private void deleteOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            heros.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void getAll(RoutingContext routingContext) {
        // Write the HTTP response
        // The response is in JSON using the utf-8 encoding
        // We returns the list of bottles
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(heros.values()));
    }

    private Map<Integer, Hero> createSomeData() {

        Map<Integer, Hero> heroes = new HashMap<Integer, Hero>(0);
        Hero h1 = new Hero("Captain America", "Marvel");
        heroes.put(h1.getId(), h1);
        Hero h2 = new Hero("Hulk", "Marvel");
        heroes.put(h2.getId(), h2);

        return heroes;
    }

    @Override
    public void stop() throws Exception {
        logger.info("Stopping ... =_( ");
        server.close();
    }
}
