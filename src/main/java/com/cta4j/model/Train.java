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

/**
 * A train run by the Chicago Transit Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 29, 2021
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
public record Train(Integer run, Route route, String destination, String station, String description,
                    LocalDateTime predictionTime, LocalDateTime arrivalTime, Boolean due, Boolean scheduled,
                    Boolean fault, Boolean delayed, Double latitude, Double longitude, Integer heading) {
}