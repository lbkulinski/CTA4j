/*
 * MIT License
 *
 * Copyright (c) 2022 Logan Kulinski
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

package com.cta4j.follow.model;

import java.time.LocalDateTime;

/**
 * A train of the Chicago Transit Authority.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @version November 26, 2022
 * @param route The route of this {@link Train}
 * @param run the run of this {@link Train}
 * @param station the station of this {@link Train}
 * @param destination the destination of this {@link Train}
 * @param predictionTime the prediction time of this {@link Train}
 * @param arrivalTime the arrival time of this {@link Train}
 * @param due the due flag of this {@link Train}
 * @param scheduled the scheduled flag of this {@link Train}
 * @param fault the fault flag of this {@link Train}
 * @param delayed the delayed flag of this {@link Train}
 */
public record Train(String route, int run, String station, String destination, LocalDateTime predictionTime,
    LocalDateTime arrivalTime, Boolean due, Boolean scheduled, Boolean fault, Boolean delayed) {
}