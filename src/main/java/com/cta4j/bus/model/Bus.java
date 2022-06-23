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

package com.cta4j.bus.model;

import java.time.LocalDateTime;

/**
 * A bus run by the Chicago Transit Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version June 23, 2022
 * @param id the ID of this bus
 * @param route the route of this bus
 * @param destination the destination of this bus
 * @param stop the stop of this bus
 * @param type the type of this bus
 * @param predictionTime the prediction time of this bus
 * @param typeTime the type time of this bus
 * @param delayed the delayed flag of this bus
 */
public record Bus(Integer id, Stop stop, Route route, String direction, String destination, Type type,
                  LocalDateTime predictionTime, LocalDateTime typeTime, Boolean delayed) {
}