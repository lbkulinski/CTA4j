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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.cta4j.model.Train;
import com.cta4j.model.Route;
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
import com.google.gson.GsonBuilder;
import com.cta4j.model.adapters.TrainTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.util.HashSet;

/**
 * A set of utility methods used to interact with the CTA's API.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 27, 2021
 */
public final class ChicagoTransitAuthority {
    /**
     * The {@link Logger} of the {@link ChicagoTransitAuthority} class.
     */
    private static final Logger LOGGER;

    static {
        LOGGER = LogManager.getLogger();
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
                                                           "a route name is the specified array is null"));

        Properties properties = new Properties();

        String fileName = "src/main/resources/api-key.properties";

        Path path = Path.of(fileName);

        BufferedReader bufferedReader;

        try {
            bufferedReader = Files.newBufferedReader(path);

            properties.load(bufferedReader);
        } catch (IOException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in reading the API key file");
        } //end try catch

        String apiKey = properties.getProperty("key");

        if (apiKey == null) {
            LOGGER.atError()
                  .log("\"key\" could not be found in \"api-key.properties\"");

            return Set.of();
        } //end if

        String uriString;

        if (routeNames.length == 0) {
            uriString = """
                        https://lapi.transitchicago.com/api/1.0/ttarrivals.aspx\
                        ?key=%s&mapid=%s&outputType=JSON""".formatted(apiKey, mapId);
        } else {
            String routeNamesString = Arrays.stream(routeNames)
                                            .map(String::toLowerCase)
                                            .map("rt=%s"::formatted)
                                            .reduce("%s&%s"::formatted)
                                            .get();

            uriString = """
                        https://www.lapi.transitchicago.com/api/1.0/ttpositions.aspx\
                        ?key=%s&mapid=%s&%s&outputType=JSON""".formatted(apiKey, mapId, routeNamesString);
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
}