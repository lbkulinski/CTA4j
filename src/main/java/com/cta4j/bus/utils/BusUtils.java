package com.cta4j.bus.utils;

import com.cta4j.bus.model.adapters.BusTypeAdapter;
import com.cta4j.bus.model.adapters.RouteTypeAdapter;
import com.cta4j.bus.model.adapters.StopTypeAdapter;
import com.cta4j.bus.model.Bus;
import com.cta4j.bus.model.Route;
import com.cta4j.bus.model.Stop;
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
     * The {@link Logger} of the {@link BusUtils} class.
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

    public static Set<Route> getRoutes() {
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
    } //getRoutes

    public static Set<String> getDirections(String route) {
        Objects.requireNonNull(route, "the specified route is null");

        String uriStringFormat = "http://www.ctabustracker.com/bustime/api/v2/getdirections?key=%s&rt=%s&format=json";

        String uriString = uriStringFormat.formatted(BUS_API_KEY, route);

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

        Gson gson = new Gson();

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

        if (!bustimeResponseObject.has("directions")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"directions\" is missing");

            return Set.of();
        } //end if

        JsonElement directionsElement = bustimeResponseObject.get("directions");

        if (!directionsElement.isJsonArray()) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"directions\" is not an array");

            return Set.of();
        } //end if

        JsonArray directionsArray = directionsElement.getAsJsonArray();

        Set<String> directions = new HashSet<>();

        for (JsonElement jsonElement : directionsArray) {
            if (!jsonElement.isJsonObject()) {
                continue;
            } //end if

            JsonObject directionObject = jsonElement.getAsJsonObject();

            if (!directionObject.has("dir")) {
                continue;
            } //end if

            JsonElement dirElement = directionObject.get("dir");

            if (!dirElement.isJsonPrimitive()) {
                continue;
            } //end if

            JsonPrimitive dirPrimitive = dirElement.getAsJsonPrimitive();

            if (!dirPrimitive.isString()) {
                continue;
            } //end if

            String direction = dirPrimitive.getAsString();

            directions.add(direction);
        } //end for

        return directions;
    } //getDirections

    public static Set<Stop> getStops(String route, String direction) {
        Objects.requireNonNull(route, "the specified route is null");

        Objects.requireNonNull(direction, "the specified direction is null");

        Objects.requireNonNull(route, "the specified route is null");

        String uriStringFormat = """
                                 http://www.ctabustracker.com/bustime/api/v2/getstops?key=%s&rt=%s&dir=%s\
                                 &format=json""";

        String uriString = uriStringFormat.formatted(BUS_API_KEY, route, direction);

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

        GsonBuilder builder = new GsonBuilder();

        StopTypeAdapter typeAdapter = new StopTypeAdapter();

        builder.registerTypeAdapter(Stop.class, typeAdapter);

        Gson gson = builder.create();

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

        if (!bustimeResponseObject.has("stops")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"stops\" is missing");

            return Set.of();
        } //end if

        JsonElement stopsElement = bustimeResponseObject.get("stops");

        if (!stopsElement.isJsonArray()) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"stops\" is not an array");

            return Set.of();
        } //end if

        JsonArray stopsArray = stopsElement.getAsJsonArray();

        Set<Stop> stops = new HashSet<>();

        for (JsonElement stopElement : stopsArray) {
            Stop stop;

            try {
                stop = gson.fromJson(stopElement, Stop.class);
            } catch (JsonSyntaxException e) {
                LOGGER.atError()
                      .withThrowable(e)
                      .log("Error in parsing a stop in the response from the API");

                continue;
            } //end try catch

            stops.add(stop);
        } //end for

        return stops;
    } //getStops

    /**
     * Returns the {@link Bus}es using the specified stop ID and routes of the Chicago Transit Authority. If no routes
     * are provided, all routes are returned.
     *
     * @param stopId the stop ID to be used in the operation
     * @param routes the routes to be used in the operation
     * @return the {@link Bus}es using the specified stop ID and routes of the Chicago Transit Authority
     * @throws NullPointerException if the specified array of routes or a route in the specified array is {@code null}
     */
    public static Set<Bus> getBuses(int stopId, String... routes) {
        Objects.requireNonNull(routes, "the specified array of routes is null");

        Arrays.stream(routes)
              .forEach(routeName -> Objects.requireNonNull(routeName,
                                                           "a route in the specified array is null"));

        String uriString;

        if (routes.length == 0) {
            uriString = """
                        http://www.ctabustracker.com/bustime/api/v2/getpredictions\
                        ?key=%s&stpid=%s&format=json""".formatted(BUS_API_KEY, stopId);
        } else {
            String routesString = Arrays.stream(routes)
                                        .map(String::toLowerCase)
                                        .map("rt=%s"::formatted)
                                        .reduce("%s&%s"::formatted)
                                        .get();

            uriString = """
                        http://www.ctabustracker.com/bustime/api/v2/getpredictions\
                        ?key=%s&stpid=%s&%s&format=json""".formatted(BUS_API_KEY, stopId, routesString);
        } //end if

        URI uri;

        try {
            uri = URI.create(uriString);
        } catch (IllegalArgumentException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in constructing the API URI");

            return Set.of();
        }//end try catch

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

        HttpClient httpClient;

        try {
            httpClient = HttpClient.newHttpClient();
        } catch (UncheckedIOException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in constructing the HTTP client");

            return Set.of();
        } //end try catch

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response;

        try {
            response = httpClient.send(request, bodyHandler);
        } catch (IOException | InterruptedException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in sending the API request");

            return Set.of();
        } //end try catch

        String json = response.body();

        GsonBuilder gsonBuilder = new GsonBuilder();

        BusTypeAdapter typeAdapter = new BusTypeAdapter();

        gsonBuilder.registerTypeAdapter(Bus.class, typeAdapter);

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

        if ((bustimeResponseElement == null) || !bustimeResponseElement.isJsonObject()) {
            LOGGER.atError()
                  .log("""
                       Error in parsing the response from the API. The member "bustime-response" is null or not an \
                       object""");

            return Set.of();
        } //end if

        JsonObject bustimeResponseObject = bustimeResponseElement.getAsJsonObject();

        if (!bustimeResponseObject.has("prd")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"prd\" is missing");

            return Set.of();
        } //end if

        JsonElement etaElement = bustimeResponseObject.get("prd");

        if ((etaElement == null) || !etaElement.isJsonArray()) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"prd\" is null or not an array");

            return Set.of();
        } //end if

        JsonArray prdArray = etaElement.getAsJsonArray();

        Set<Bus> buses = new HashSet<>();

        for (JsonElement jsonElement : prdArray) {
            Bus bus;

            try {
                bus = gson.fromJson(jsonElement, Bus.class);
            } catch (JsonSyntaxException e) {
                LOGGER.atError()
                      .withThrowable(e)
                      .log("Error in parsing a train in the response from the API");

                continue;
            } //end try catch

            buses.add(bus);
        } //end for

        return buses;
    } //getBuses
}