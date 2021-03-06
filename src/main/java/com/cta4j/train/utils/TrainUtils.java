package com.cta4j.train.utils;

import com.cta4j.train.model.Route;
import com.cta4j.train.model.Train;
import com.cta4j.train.model.adapters.TrainTypeAdapter;
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

public final class TrainUtils {
    /**
     * The {@link Logger} of the {@link TrainUtils} class.
     */
    private static final Logger LOGGER;

    private static final String TRAIN_API_KEY;

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
    } //static

    private TrainUtils() throws InstantiationException {
        throw new InstantiationException("instances of type TrainUtils cannot be created");
    } //BusUtils

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
}