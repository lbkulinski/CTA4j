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

import com.cta4j.model.Color;
import com.cta4j.model.Route;
import com.cta4j.model.Train;
import com.cta4j.model.adapters.TrainTypeAdapter;
import com.google.gson.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
public final class Controller {
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

    @GetMapping
    public String getNearbyTrains(@RequestParam double latitude, @RequestParam double longitude,
                                  @RequestParam(name = "max_distance", defaultValue = "0.1") double maximumDistance) {
        Set<Route> routes = ChicagoTransitAuthority.getRoutes();

        Map<Color, Set<Train>> colorToNearbyTrains = new HashMap<>();

        for (Route route : routes) {
            for (Train train : route.trains()) {
                double trainLatitude = train.latitude();

                double trainLongitude = train.longitude();

                double distance = Controller.getDistance(latitude, longitude, trainLatitude, trainLongitude);

                if (Double.compare(distance, maximumDistance) > 0) {
                    continue;
                } //end if

                Set<Train> nearbyTrains = colorToNearbyTrains.computeIfAbsent(route.color(), key -> new HashSet<>());

                nearbyTrains.add(train);
            } //end if
        } //end for

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Train.class, new TrainTypeAdapter());

        Gson gson = gsonBuilder.create();

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
    } //getNearbyTrains
}