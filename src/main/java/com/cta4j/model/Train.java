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
 * @version December 11, 2021
 * @param run the run of this train
 * @param destination the name of the final stop of this train
 * @param nextStop the name of the next stop of this train
 * @param estimatedArrival the estimated arrival of this train
 * @param approaching the approaching flag of this train
 * @param delayed the delayed flag of this train
 * @param latitude the latitude of this train
 * @param longitude the longitude of this train
 */
public record Train(int run, String destination, String nextStop, LocalDateTime estimatedArrival, boolean approaching,
                    boolean delayed, double latitude, double longitude) {
    /**
     * Constructs an instance of the {@link Train} class.
     *
     * @param run the run to be used in construction
     * @param destination the destination to be used in construction
     * @param nextStop the next stop to be used in construction
     * @param estimatedArrival the estimated arrival to be used in construction
     * @param approaching the approaching flag to be used in construction
     * @param delayed the delayed flag to be used in construction
     * @param latitude the latitude to be used in construction
     * @param longitude the longitude to be used in construction
     * @throws NullPointerException if the specified destination, next stop, or estimated arrival is {@code null}
     */
    public Train {
        Objects.requireNonNull(destination, "the specified destination is null");

        Objects.requireNonNull(nextStop, "the specified next stop is null");

        Objects.requireNonNull(estimatedArrival, "the specified estimated arrival is null");
    } //Train
}