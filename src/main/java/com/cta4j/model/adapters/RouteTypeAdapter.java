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

package com.cta4j.model.adapters;

import com.google.gson.TypeAdapter;
import com.cta4j.model.Route;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Objects;
import com.cta4j.model.Train;
import com.google.gson.stream.JsonReader;
import com.cta4j.model.Color;
import java.util.Set;
import com.google.gson.stream.JsonToken;
import java.util.HashSet;

/**
 * A type adapter for the {@link Route} class.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 11, 2021
 */
public final class RouteTypeAdapter extends TypeAdapter<Route> {
    /**
     * Constructs an instance of the {@link RouteTypeAdapter} class.
     */
    public RouteTypeAdapter() {
    } //RouteTypeAdapter

    /**
     * Serializes the specified {@link Route} using the specified {@link JsonWriter}.
     *
     * @param jsonWriter the {@link JsonWriter} to be used in the operation
     * @param route the {@link Route} to be used in the operation
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonWriter} or {@link Route} is {@code null}
     */
    public static void writeRoute(JsonWriter jsonWriter, Route route) throws IOException {
        Objects.requireNonNull(jsonWriter, "the specified JsonWriter is null");

        Objects.requireNonNull(route, "the specified Route is null");

        jsonWriter.beginObject();

        jsonWriter.name("@name");

        String name = switch (route.color()) {
            case RED -> "red";
            case BLUE -> "blue";
            case BROWN -> "brn";
            case GREEN -> "g";
            case ORANGE -> "org";
            case PURPLE -> "p";
            case PINK -> "pink";
            case YELLOW -> "y";
        };

        jsonWriter.value(name);

        jsonWriter.name("train");

        jsonWriter.beginArray();

        for (Train train : route.trains()) {
            TrainTypeAdapter.writeTrain(jsonWriter, train);
        } //end for

        jsonWriter.endArray();

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
        Objects.requireNonNull(jsonReader, "the specified JsonReader is null");

        Color color = null;

        Set<Train> trains = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String nameKey = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                String errorMessage = "the member \"%s\" has a value of null".formatted(nameKey);

                throw new IOException(errorMessage);
            } //end if

            switch (nameKey) {
                case "@name" -> {
                    String name = jsonReader.nextString();

                    name = name.toLowerCase();

                    color = switch (name) {
                        case "red" -> Color.RED;
                        case "blue" -> Color.BLUE;
                        case "brn" -> Color.BROWN;
                        case "g" -> Color.GREEN;
                        case "org" -> Color.ORANGE;
                        case "p", "pexp" -> Color.PURPLE;
                        case "pink" -> Color.PINK;
                        case "y" -> Color.YELLOW;
                        default -> {
                            throw new IOException("the member \"@name\" has a novel value");
                        } //default
                    };
                } //case "@name"
                case "train" -> {
                    trains = new HashSet<>();

                    if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                        Train train = TrainTypeAdapter.readTrain(jsonReader);

                        trains.add(train);
                    } else {
                        jsonReader.beginArray();

                        while (jsonReader.hasNext()) {
                            if (jsonReader.peek() == JsonToken.NULL) {
                                throw new IOException("the member \"train\" has an element that is null");
                            } //end if

                            Train train = TrainTypeAdapter.readTrain(jsonReader);

                            trains.add(train);
                        } //end while

                        jsonReader.endArray();
                    } //end if
                } //case "train"
            } //end switch
        } //end while

        jsonReader.endObject();

        if (color == null) {
            throw new IOException("the member \"@name\" has a value of null");
        } //end if

        if (trains == null) {
            trains = Set.of();
        } //end if

        return new Route(color, trains);
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