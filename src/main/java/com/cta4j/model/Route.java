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

import java.util.Set;
import java.util.Objects;
import java.util.HashSet;
import java.util.Collections;

/**
 * A route of the Chicago Transit Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 11, 2021
 * @param color the color of this route
 * @param trains the {@link Set} of {@link Train}s of this route
 */
public record Route(Color color, Set<Train> trains) {
    /**
     * Constructs an instance of the {@link Route} class.
     *
     * @param color the color to be used in construction
     * @param trains the {@link Set} of {@link Train}s to be used in construction
     * @throws NullPointerException if the specified color or {@link Set} of {@link Train}s is {@code null}
     */
    public Route {
        Objects.requireNonNull(color, "the specified color is null");

        Objects.requireNonNull(trains, "the specified Set of Trains is null");

        trains.forEach(train -> Objects.requireNonNull(train, "a Train in the specified Set is null"));

        trains = new HashSet<>(trains);

        trains = Collections.unmodifiableSet(trains);
    } //Route
}