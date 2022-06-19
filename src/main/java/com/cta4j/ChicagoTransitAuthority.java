/*
 * MIT License
 *
 * Copyright (c) 2021 Logan Kulinski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cta4j;

import com.google.gson.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.cta4j.model.train.Train;
import com.cta4j.model.train.Route;
import java.util.Set;
import java.util.Objects;
import java.util.Arrays;
import java.util.Properties;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;

import com.cta4j.model.adapters.TrainTypeAdapter;

import java.util.HashSet;
import com.cta4j.model.bus.Bus;
import com.cta4j.model.adapters.BusTypeAdapter;

/**
 * A set of utility methods used to interact with the CTA's API.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version January 1, 2022
 */
public final class ChicagoTransitAuthority {
    /**
     * The {@link Logger} of the {@link ChicagoTransitAuthority} class.
     */
    private static final Logger LOGGER;

    private static final String TRAIN_API_KEY;

    private static final String BUS_API_KEY;

    static {
        LOGGER = LogManager.getLogger();

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

        TRAIN_API_KEY = properties.getProperty("train_key");

        if (TRAIN_API_KEY == null) {
            String message = "Error in reading the train API key";

            LOGGER.atError()
                  .log(message);

            throw new IllegalStateException(message);
        } //end if

        BUS_API_KEY = properties.getProperty("bus_key");

        if (BUS_API_KEY == null) {
            String message = "Error in reading the bus API key";

            LOGGER.atError()
                  .log(message);

            throw new IllegalStateException(message);
        } //end if
    } //static

    /**
     * Throws an {@link InstantiationException}, as instance of type {@link ChicagoTransitAuthority} cannot be created.
     *
     * @throws InstantiationException if this constructor is invoked
     */
    private ChicagoTransitAuthority() throws InstantiationException {
        throw new InstantiationException("instances of type ChicagoTransitAuthority cannot be created");
    } //ChicagoTransitAuthority

    /**
     * Returns the {@link Train}s using the specified map ID and route names of the Chicago Transit Authority. If no
     * route names are provided, all routes are returned.
     *
     * @param mapId the map ID to be used in the operation
     * @param routeNames the {@link Route} names to be used in the operation
     * @return the {@link Train}s using the specified map ID and route names of the Chicago Transit Authority
     * @throws NullPointerException if the specified array of route names or a route name in the specified array is
     * {@code null}
     */
    public static Set<Train> getTrains(int mapId, String... routeNames) {
        Objects.requireNonNull(routeNames, "the specified array of route names is null");

        Arrays.stream(routeNames)
              .forEach(routeName -> Objects.requireNonNull(routeName,
                                                           "a route name in the specified array is null"));

        String uriString;

        if (routeNames.length == 0) {
            uriString = """
                        https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx\
                        ?key=%s&mapid=%s&outputType=JSON""".formatted(TRAIN_API_KEY, mapId);
        } else {
            String routeNamesString = Arrays.stream(routeNames)
                                            .map(String::toLowerCase)
                                            .map("rt=%s"::formatted)
                                            .reduce("%s&%s"::formatted)
                                            .get();

            uriString = """
                        https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx\
                        ?key=%s&mapid=%s&%s&outputType=JSON""".formatted(TRAIN_API_KEY, mapId, routeNamesString);
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

        TrainTypeAdapter typeAdapter = new TrainTypeAdapter();

        gsonBuilder.registerTypeAdapter(Train.class, typeAdapter);

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

        if (!jsonObject.has("ctatt")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"ctatt\" is missing");

            return Set.of();
        } //end if

        JsonElement ctattElement = jsonObject.get("ctatt");

        if ((ctattElement == null) || !ctattElement.isJsonObject()) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"ctatt\" is null or not an object");

            return Set.of();
        } //end if

        JsonObject ctattObject = ctattElement.getAsJsonObject();

        if (!ctattObject.has("eta")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"eta\" is missing");

            return Set.of();
        } //end if

        JsonElement etaElement = ctattObject.get("eta");

        if ((etaElement == null) || !etaElement.isJsonArray()) {
            LOGGER.atError()
                  .log("Error in parsing the response from the API. The member \"eta\" is null or not an array");

            return Set.of();
        } //end if

        JsonArray etaArray = etaElement.getAsJsonArray();

        Set<Train> trains = new HashSet<>();

        for (JsonElement jsonElement : etaArray) {
            Train train;

            try {
                train = gson.fromJson(jsonElement, Train.class);
            } catch (JsonSyntaxException e) {
                LOGGER.atError()
                      .withThrowable(e)
                      .log("Error in parsing a train in the response from the API");

                continue;
            } //end try catch

            trains.add(train);
        } //end for

        return trains;
    } //getTrains

    public static Set<String> getBusRoutes() {
        //http://www.ctabustracker.com/bustime/api/v2/getroutes

        String uriString = "http://www.ctabustracker.com/bustime/api/v2/getroutes?key=%s".formatted(BUS_API_KEY);

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

        /*
        Set<String> routes = new HashSet<>();

        for (JsonElement jsonElement : jsonArray) {
            if (!jsonElement.isJsonPrimitive()) {
                continue;
            } //end if

            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

            if (!jsonPrimitive.isString()) {
                continue;
            } //end if

            String route = jsonPrimitive.getAsString();

            routes.add(route);
        } //end for

        return routes;
         */

        return null;
    } //getBusRoutes

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