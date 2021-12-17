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

import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import com.cta4j.model.Color;
import java.util.Set;
import com.cta4j.model.Train;
import java.util.Objects;
import com.google.gson.GsonBuilder;
import com.cta4j.model.adapters.TrainTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cta4j.model.Route;
import java.util.TreeMap;
import java.util.HashSet;

/**
 * A controller of the CTA4j application.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 16, 2021
 */
@RestController
public final class Controller {
    /**
     * Returns the raw JSON response for the specified {@link Map} of {@link Color} to {@link Train}s
     *
     * @param colorToTrains the {@link Map} of {@link Color} to {@link Train}s to be used in the operation
     * @return the raw JSON response for the specified {@link Map} of {@link Color} to {@link Train}s
     * @throws NullPointerException if the specified {@link Map} or any of its elements are {@code null}
     */
    private static String getRawResponse(Map<Color, Set<Train>> colorToTrains) {
        Objects.requireNonNull(colorToTrains, "the specified Map of Color to Trains is null");

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Train.class, new TrainTypeAdapter());

        Gson gson = gsonBuilder.setPrettyPrinting()
                               .create();

        JsonObject response = new JsonObject();

        colorToTrains.forEach((color, trains) -> {
            Objects.requireNonNull(color, "a Color in the specified Map is null");

            Objects.requireNonNull(trains, "a Set in the specified Map is null");

            JsonArray trainArray = new JsonArray();

            trains.forEach(train -> {
                Objects.requireNonNull(train, "a Train in a Set in the specified Map is null");

                JsonElement trainElement = gson.toJsonTree(train, Train.class);

                trainArray.add(trainElement);
            });

            String colorString = color.toString();

            response.add(colorString, trainArray);
        });

        return gson.toJson(response);
    } //getRawResponse

    /**
     * Returns the formatted {@link String} for the specified {@link Train}.
     *
     * @param train the {@link Train} to be used in the operation
     * @return the formatted {@link String} for the specified {@link Train}
     * @throws NullPointerException if the specified {@link Train} is {@code null}
     */
    private static String getTrainString(Train train) {
        Objects.requireNonNull(train, "the specified train is null");

        String format = """
                            %d -- Run
                            %s -- Destination
                            %s -- Next Stop
                            %s -- Arrival
                        """;

        int run = train.run();

        String destination = train.destination();

        String nextStop = train.nextStop();

        LocalDateTime arrival = train.estimatedArrival();

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        String arrivalString = arrival.format(formatter);

        String trainString = format.formatted(run, destination, nextStop, arrivalString);

        boolean approaching = train.approaching();

        if (approaching) {
            String approachingString = "Approaching\n";

            trainString += approachingString;
        } //end if

        boolean delayed = train.delayed();

        if (delayed) {
            String delayedString = "Delayed\n";

            trainString += delayedString;
        } //end if

        String newlines = "\n";

        trainString += newlines;

        return trainString;
    } //getTrainString

    /**
     * Returns the formatted JSON response for the specified {@link Map} of {@link Color} to {@link Train}s
     *
     * @param colorToTrains the {@link Map} of {@link Color} to {@link Train}s to be used in the operation
     * @return the formatted JSON response for the specified {@link Map} of {@link Color} to {@link Train}s
     * @throws NullPointerException if the specified {@link Map} or any of its elements are {@code null}
     */
    private static String getFormattedResponse(Map<Color, Set<Train>> colorToTrains) {
        Objects.requireNonNull(colorToTrains, "the specified Map of Color to Trains is null");

        StringBuilder stringBuilder = new StringBuilder();

        colorToTrains.forEach((color, trains) -> {
            Objects.requireNonNull(color, "a Color in the specified Map is null");

            Objects.requireNonNull(trains, "a Set in the specified Map is null");

            stringBuilder.append(color);

            String newline = "\n";

            stringBuilder.append(newline);

            trains.forEach(train -> {
                Objects.requireNonNull(train, "a Train in a Set in the specified Map is null");

                String trainString = Controller.getTrainString(train);

                stringBuilder.append(trainString);
            });
        });

        String formattedString = stringBuilder.toString();

        formattedString = formattedString.strip();

        JsonObject jsonObject = new JsonObject();

        String property = "formatted_string";

        jsonObject.addProperty(property, formattedString);

        Gson gson = new Gson();

        return gson.toJson(jsonObject);
    } //getFormattedResponse

    /**
     * Returns a JSON response containing information about routes using the specified route names and raw flag. If
     * {@code raw} is {@code true}, the raw JSON data will be returned. Otherwise, a formatted version of the data will
     * be returned. The data is returned raw by default.
     *
     * @param routeNames the route names to be used in the operation
     * @param raw the raw flag to be used in the operation
     * @return a JSON response containing information about routes using the specified route names and raw flag
     */
    @GetMapping("get-routes")
    public String getRoutes(@RequestParam(name = "route", required = false) String[] routeNames,
                            @RequestParam(name = "raw", required = false, defaultValue = "true") boolean raw) {
        if (routeNames == null) {
            routeNames = new String[0];
        } //end if

        Set<Route> routes = ChicagoTransitAuthority.getRoutes(routeNames);

        Map<Color, Set<Train>> colorToTrains = new TreeMap<>();

        for (Route route : routes) {
            for (Train train : route.trains()) {
                Set<Train> trains = colorToTrains.computeIfAbsent(route.color(), key -> new HashSet<>());

                trains.add(train);
            } //end if
        } //end for

        String response;

        if (raw) {
            response = Controller.getRawResponse(colorToTrains);
        } else {
            response = Controller.getFormattedResponse(colorToTrains);
        } //end if

        return response;
    } //getRoutes
}