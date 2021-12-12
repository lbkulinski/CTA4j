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
    public String getNearbyTrains(@RequestParam double latitude, double longitude) {
        System.out.printf("%f, %f%n", latitude, longitude);

        return """
               {
                   "success": true
               }""";
    } //getNearbyTrains
}