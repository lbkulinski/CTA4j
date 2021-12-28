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
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Set;
import com.cta4j.model.Train;
import com.google.gson.GsonBuilder;
import com.cta4j.model.adapters.TrainTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * A controller of the CTA4j application.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 28, 2021
 */
@RestController
public final class Controller {
    /**
     * Returns a JSON response containing information about trains using the specified map ID and route names.
     * 
     * @param mapId the map ID to be used in the operation
     * @param routeNames the route names to be used in the operation
     * @return a JSON response containing information about trains using the specified map ID and route names
     */
    @GetMapping("get-trains")
    public String getTrains(@RequestParam(value = "map_id") int mapId,
                            @RequestParam(value = "route[]", required = false) String[] routeNames) {
        if (routeNames == null) {
            routeNames = new String[0];
        } //end if

        Set<Train> trains = ChicagoTransitAuthority.getTrains(mapId, routeNames);

        GsonBuilder gsonBuilder = new GsonBuilder();

        TrainTypeAdapter typeAdapter = new TrainTypeAdapter();

        gsonBuilder.registerTypeAdapter(Train.class, typeAdapter);

        Gson gson = gsonBuilder.setPrettyPrinting()
                               .create();

        JsonArray response = new JsonArray();

        for (Train train : trains) {
            JsonElement element = gson.toJsonTree(train, Train.class);

            response.add(element);
        } //end for

        return gson.toJson(response);
    } //getRoutes
}