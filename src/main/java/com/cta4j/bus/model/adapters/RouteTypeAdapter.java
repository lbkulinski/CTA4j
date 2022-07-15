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

package com.cta4j.bus.model.adapters;

import com.cta4j.bus.model.Route;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

/**
 * A type adapter for the {@link Route} class.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @version July 15, 2022
 */
public final class RouteTypeAdapter extends TypeAdapter<Route> {
    /**
     * Serializes the specified {@link Route} using the specified {@link JsonWriter}.
     *
     * @param jsonWriter the {@link JsonWriter} to be used in the operation
     * @param route the {@link Route} to be used in the operation
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonWriter} or {@link Route} is {@code null}
     */
    public static void writeRoute(JsonWriter jsonWriter, Route route) throws IOException {
        Objects.requireNonNull(jsonWriter, "the specified JSON writer is null");

        Objects.requireNonNull(route, "the specified route is null");

        jsonWriter.beginObject();

        jsonWriter.name("id");

        String id = route.id();

        jsonWriter.value(id);

        jsonWriter.name("name");

        String name = route.name();

        jsonWriter.value(name);

        jsonWriter.endObject();
    } //writeRoute

    /**
     * Deserializes a {@link Route} object using the specified {@link JsonReader}.
     *
     * @param jsonReader the {@link JsonReader} to be used in the operation
     * @return the deserialized {@link Route} object
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonReader} is {@code null}
     */
    public static Route readRoute(JsonReader jsonReader) throws IOException {
        Objects.requireNonNull(jsonReader, "the specified JSON reader is null");

        jsonReader.beginObject();

        String routeId = null;

        String routeName = null;

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();

                continue;
            } //end if

            switch (name) {
                case "rt" -> routeId = jsonReader.nextString();
                case "rtnm" -> routeName = jsonReader.nextString();
                default -> jsonReader.nextString();
            } //end switch
        } //end while

        jsonReader.endObject();

        return new Route(routeId, routeName);
    } //readRoute

    /**
     * Serializes the specified {@link Route} using the specified {@link JsonWriter}.
     *
     * @param jsonWriter the {@link JsonWriter} to be used in the operation
     * @param route the {@link Route} to be used in the operation
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonWriter} or {@link Route} is {@code null}
     */
    @Override
    public void write(JsonWriter jsonWriter, Route route) throws IOException {
        RouteTypeAdapter.writeRoute(jsonWriter, route);
    } //write

    /**
     * Deserializes a {@link Route} object using the specified {@link JsonReader}.
     *
     * @param jsonReader the {@link JsonReader} to be used in the operation
     * @return the deserialized {@link Route} object
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonReader} is {@code null}
     */
    @Override
    public Route read(JsonReader jsonReader) throws IOException {
        return RouteTypeAdapter.readRoute(jsonReader);
    } //read
}