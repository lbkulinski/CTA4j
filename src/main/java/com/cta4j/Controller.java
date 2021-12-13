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
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Set;
import com.cta4j.model.Route;
import java.util.Map;
import com.cta4j.model.Color;
import com.cta4j.model.Train;
import java.util.HashMap;
import java.util.HashSet;
import com.google.gson.GsonBuilder;
import com.cta4j.model.adapters.TrainTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * A controller of the CTA4j application.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 13, 2021
 */
@RestController
public final class Controller {
    /**
     * Returns the distance, in miles, between the specified latitudes and longitudes.
     *
     * @param latitude0 the first latitude to be used in the operation
     * @param longitude0 the first longitude to be used in the operation
     * @param latitude1 the second latitude to be used in the operation
     * @param longitude1 the second longitude to be used in the operation
     * @return the distance, in miles, between the specified latitudes and longitudes
     */
    private static double getDistance(double latitude0, double longitude0, double latitude1, double longitude1) {
        double deltaLatitude = Math.toRadians(latitude1 - latitude0);

        double deltaLongitude = Math.toRadians(longitude1 - longitude0);

        latitude0 = Math.toRadians(latitude0);

        latitude1 = Math.toRadians(latitude1);

        double a = Math.pow(Math.sin(deltaLatitude / 2.0), 2.0);

        a += (Math.cos(latitude0) * Math.cos(latitude1));

        a *= Math.pow(Math.sin(deltaLongitude / 2.0), 2.0);

        double c = Math.asin(Math.sqrt(a)) * 2.0;

        final double r = 6371.0;

        double kilometers = r * c;

        double mileConversion = 0.6213712;

        return kilometers * mileConversion;
    } //getDistance

    /**
     * Returns a JSON response containing information about active trains.
     *
     * @return a JSON response containing information about active trains
     */
    @GetMapping
    public String getActiveTrains() {
        Set<Route> routes = ChicagoTransitAuthority.getRoutes();

        Map<Color, Set<Train>> colorToNearbyTrains = new HashMap<>();

        for (Route route : routes) {
            for (Train train : route.trains()) {
                Set<Train> nearbyTrains = colorToNearbyTrains.computeIfAbsent(route.color(), key -> new HashSet<>());

                nearbyTrains.add(train);
            } //end if
        } //end for

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Train.class, new TrainTypeAdapter());

        Gson gson = gsonBuilder.setPrettyPrinting()
                               .create();

        JsonObject response = new JsonObject();

        colorToNearbyTrains.forEach((color, nearbyTrains) -> {
            JsonArray nearbyTrainArray = new JsonArray();

            nearbyTrains.forEach(train -> {
                JsonElement trainElement = gson.toJsonTree(train, Train.class);

                nearbyTrainArray.add(trainElement);
            });

            String colorString = color.toString();

            response.add(colorString, nearbyTrainArray);
        });

        return gson.toJson(response);
    } //getActiveTrains
}