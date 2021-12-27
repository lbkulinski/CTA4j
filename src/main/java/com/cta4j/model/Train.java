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

package com.cta4j.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A train run by the Chicago Transit Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 27, 2021
 * @param run the run of this train
 * @param route the route of this train
 * @param destination the destination of this train
 * @param station the station of this train
 * @param description the description of this train
 * @param predictionTime the prediction time of this train
 * @param arrivalTime the arrival time of this train
 * @param due the due flag of this train
 * @param scheduled the scheduled flag of this train
 * @param fault the fault flag of this train
 * @param delayed the delayed flag of this train
 * @param latitude the latitude of this train
 * @param longitude the longitude of this train
 * @param heading the heading of this train
 */
public record Train(int run, Route route, String destination, String station, String description,
                    LocalDateTime predictionTime, LocalDateTime arrivalTime, boolean due, boolean scheduled,
                    boolean fault, boolean delayed, double latitude, double longitude, int heading) {
    /**
     * Constructs an instance of the {@link Train} class.
     *
     * @param run the run to be used in construction
     * @param route the route to be used in construction
     * @param destination the destination to be used in construction
     * @param station the station to be used in construction
     * @param description the description to be used in construction
     * @param predictionTime the prediction time to be used in construction
     * @param arrivalTime the arrival time to be used in construction
     * @param due the due flag to be used in construction
     * @param scheduled the scheduled flag to be used in construction
     * @param fault the fault flag to be used in construction
     * @param delayed the delayed flag to be used in construction
     * @param latitude the latitude to be used in construction
     * @param longitude the longitude to be used in construction
     * @param heading the heading to be used in construction
     */
    public Train {
        Objects.requireNonNull(route, "the specified route is null");

        Objects.requireNonNull(destination, "the specified destination is null");

        Objects.requireNonNull(station, "the specified station is null");

        Objects.requireNonNull(description, "the specified description is null");

        Objects.requireNonNull(predictionTime, "the specified prediction time is null");

        Objects.requireNonNull(arrivalTime, "the specified arrival time is null");
    } //Train
}