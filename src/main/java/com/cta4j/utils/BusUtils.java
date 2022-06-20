package com.cta4j.utils;

import com.cta4j.ChicagoTransitAuthority;
import com.cta4j.model.adapters.bus.RouteTypeAdapter;
import com.cta4j.model.bus.Route;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class BusUtils {
    /**
     * The {@link Logger} of the {@link ChicagoTransitAuthority} class.
     */
    private static final Logger LOGGER;

    private static final String BUS_API_KEY;

    static {
        LOGGER = LogManager.getLogger();

        BUS_API_KEY = BusUtils.getBusApiKey();
    } //static

    private static String getBusApiKey() {
        Properties properties = new Properties();

        String pathString = "src/main/resources/api-key.properties";

        Path path = Path.of(pathString);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        } catch (IOException e) {
            String message = "Error in reading the API key file";

            LOGGER.atError()
                  .withThrowable(e)
                  .log(message);

            throw new IllegalStateException(message);
        } //end try catch

        String busApiKey = properties.getProperty("bus_key");

        if (busApiKey == null) {
            String message = "Error in reading the bus API key";

            LOGGER.atError()
                  .log(message);

            throw new IllegalStateException(message);
        } //end if

        return busApiKey;
    } //getBusApiKey

    private BusUtils() throws InstantiationException {
        throw new InstantiationException("instances of type BusUtils cannot be created");
    } //BusUtils

    public static Set<Route> getBusRoutes() {
        String uriStringFormat = "http://www.ctabustracker.com/bustime/api/v2/getroutes?key=%s&format=json";

        String uriString = uriStringFormat.formatted(BUS_API_KEY);

        URI uri;

        try {
            uri = URI.create(uriString);
        } catch (IllegalArgumentException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in constructing the API URI");

            return Set.of();
        } //end try catch

        HttpRequest request;

        try {
            request = HttpRequest.newBuilder(uri)
                                 .GET()
                                 .build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in constructing the API request");

            return Set.of();
        } //end try catch

        HttpClient client;

        try {
            client = HttpClient.newHttpClient();
        } catch (UncheckedIOException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in constructing the HTTP client");

            return Set.of();
        } //end try catch

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response;

        try {
            response = client.send(request, bodyHandler);
        } catch (IOException | InterruptedException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in sending the API request");

            return Set.of();
        } //end try catch

        String json = response.body();

        GsonBuilder gsonBuilder = new GsonBuilder();

        RouteTypeAdapter typeAdapter = new RouteTypeAdapter();

        gsonBuilder.registerTypeAdapter(Route.class, typeAdapter);

        Gson gson = gsonBuilder.create();

        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in parsing the response from the API");

            return Set.of();
        } //end try catch

        if (!jsonObject.has("bustime-response")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"bustime-response\" is missing");

            return Set.of();
        } //end if

        JsonElement bustimeResponseElement = jsonObject.get("bustime-response");

        if (!bustimeResponseElement.isJsonObject()) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"bustime-response\" is not an object");

            return Set.of();
        } //end if

        JsonObject bustimeResponseObject = bustimeResponseElement.getAsJsonObject();

        if (!bustimeResponseObject.has("routes")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"routes\" is missing");

            return Set.of();
        } //end if

        JsonElement routesElement = bustimeResponseObject.get("routes");

        if (!routesElement.isJsonArray()) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"routes\" is not an array");

            return Set.of();
        } //end if

        JsonArray routesArray = routesElement.getAsJsonArray();

        Set<Route> routes = new HashSet<>();

        for (JsonElement jsonElement : routesArray) {
            Route route;

            try {
                route = gson.fromJson(jsonElement, Route.class);
            } catch (JsonSyntaxException e) {
                LOGGER.atError()
                      .withThrowable(e)
                      .log("Error in parsing a route in the response from the API");

                continue;
            } //end try catch

            routes.add(route);
        } //end for

        return routes;
    } //getBusRoutes
}
