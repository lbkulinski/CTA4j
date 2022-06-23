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

package com.cta4j.train.controller;

import com.cta4j.bus.utils.BusUtils;
import com.cta4j.train.utils.TrainUtils;
import com.cta4j.utils.Body;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Set;
import com.cta4j.train.model.Train;

/**
 * A controller of the CTA4j application.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version June 23, 2022
 */
@RestController
@RequestMapping("/api/train")
public final class TrainController {
    /**
     * Returns a JSON response containing information about trains using the specified map ID and routes.
     * 
     * @param mapId the map ID to be used in the operation
     * @param routes the routes to be used in the operation
     * @return a JSON response containing information about trains using the specified map ID and routes
     */
    @GetMapping
    public ResponseEntity<Body<Set<Train>>> read(@RequestParam int mapId,
                                                 @RequestParam(value = "route[]", required = false) String[] routes) {
        if (routes == null) {
            routes = new String[0];
        } //end if

        Set<Train> trains = TrainUtils.getTrains(mapId, routes);

        Body<Set<Train>> body = Body.success(trains);

        return new ResponseEntity<>(body, HttpStatus.OK);
    } //read
}