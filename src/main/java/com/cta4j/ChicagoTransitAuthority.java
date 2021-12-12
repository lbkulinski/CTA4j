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
import java.util.Set;
import com.cta4j.model.Route;
import java.util.Properties;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.cta4j.model.Train;
import com.cta4j.model.adapters.TrainTypeAdapter;
import com.cta4j.model.adapters.RouteTypeAdapter;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.util.HashSet;

/**
 * A set of utility methods used to interact with the CTA's API.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 11, 2021
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
     * Returns the current {@link Route}s of the Chicago Transit Authority.
     *
     * @return the current {@link Route}s of the Chicago Transit Authority
     */
    public static Set<Route> getRoutes() {
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

        String uriTemplate = """
                             http://www.lapi.transitchicago.com/api/1.0/ttpositions.aspx\
                             ?key=%s&rt=red&rt=blue&rt=brn&rt=g&rt=org&rt=p&rt=pink&rt=y&outputType=JSON""";

        String uriString = uriTemplate.formatted(apiKey);

        URI uri = URI.create(uriString);

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response;

        try {
            response = httpClient.send(request, bodyHandler);
        } catch (IOException | InterruptedException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in requesting the CTA's train data");

            return Set.of();
        } //end try catch

        String json = response.body();

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Train.class, new TrainTypeAdapter());

        gsonBuilder.registerTypeAdapter(Route.class, new RouteTypeAdapter());

        Gson gson = gsonBuilder.create();

        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            LOGGER.atError()
                  .withThrowable(e)
                  .log("Error in parsing the response from the CTA API");

            return Set.of();
        } //end try catch

        if (!jsonObject.has("ctatt")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the CTA API. The member \"ctatt\" is missing");

            return Set.of();
        } //end if

        JsonElement ctattElement = jsonObject.get("ctatt");

        if ((ctattElement == null) || !ctattElement.isJsonObject()) {
            LOGGER.atError()
                  .log("""
                       Error in parsing the response from the CTA API. The member "ctatt" is null or not an object""");

            return Set.of();
        } //end if

        JsonObject ctattObject = ctattElement.getAsJsonObject();

        if (!ctattObject.has("route")) {
            LOGGER.atError()
                  .log("Error in parsing the response from the CTA API. The member \"route\" is missing");

            return Set.of();
        } //end if

        JsonElement routeElement = ctattObject.get("route");

        if ((routeElement == null) || !routeElement.isJsonArray()) {
            LOGGER.atError()
                  .log("""
                       Error in parsing the response from the CTA API. The member "route" is null or not an array""");

            return Set.of();
        } //end if

        JsonArray routeArray = routeElement.getAsJsonArray();

        Set<Route> routes = new HashSet<>();

        for (JsonElement jsonElement : routeArray) {
            Route route;

            try {
                route = gson.fromJson(jsonElement, Route.class);
            } catch (JsonSyntaxException e) {
                LOGGER.atError()
                      .withThrowable(e)
                      .log("Error in parsing the response from the CTA API. A route could not be parsed");

                return Set.of();
            } //end try catch

            routes.add(route);
        } //end for

        return routes;
    } //getRoutes
}