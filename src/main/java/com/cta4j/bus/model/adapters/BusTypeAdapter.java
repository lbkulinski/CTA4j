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
import com.cta4j.bus.model.Stop;
import com.google.gson.TypeAdapter;
import com.cta4j.bus.model.Bus;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Objects;
import com.cta4j.bus.model.Type;
import java.time.LocalDateTime;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A type adapter for the {@link Bus} class.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @version July 15, 2022
 */
public final class BusTypeAdapter extends TypeAdapter<Bus> {
    /**
     * The {@link Logger} of this type adapter.
     */
    private static final Logger LOGGER;

    static {
        LOGGER = LogManager.getLogger(BusTypeAdapter.class);
    } //static

    /**
     * Constructs an instance of the {@link BusTypeAdapter} class.
     */
    public BusTypeAdapter() {
    } //BusTypeAdapter

    /**
     * Serializes the specified {@link Bus} using the specified {@link JsonWriter}.
     *
     * @param jsonWriter the {@link JsonWriter} to be used in the operation
     * @param bus the {@link Bus} to be used in the operation
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonWriter} or {@link Bus} is {@code null}
     */
    public static void writeBus(JsonWriter jsonWriter, Bus bus) throws IOException {
        Objects.requireNonNull(jsonWriter, "the specified JsonWriter is null");

        Objects.requireNonNull(bus, "the specified Bus is null");

        jsonWriter.beginObject();

        jsonWriter.name("id");

        Integer id = bus.id();

        jsonWriter.value(id);

        jsonWriter.name("route");

        Route route = bus.route();

        String routeId = route.id();

        jsonWriter.value(routeId);

        jsonWriter.name("destination");

        String destination = bus.destination();

        jsonWriter.value(destination);

        jsonWriter.name("direction");

        String direction = bus.direction();

        jsonWriter.value(direction);

        jsonWriter.name("stop");

        Stop stop = bus.stop();

        String stopName = stop.name();

        jsonWriter.value(stopName);

        jsonWriter.name("type");

        Type type = bus.type();

        String typeString;

        if (type == null) {
            typeString = null;
        } else {
            typeString = type.toString();
        } //end if

        jsonWriter.value(typeString);

        jsonWriter.name("predictionTime");

        LocalDateTime predictionTime = bus.predictionTime();

        String predictionTimeString;

        if (predictionTime == null) {
            predictionTimeString = null;
        } else {
            predictionTimeString = predictionTime.toString();
        } //end if

        jsonWriter.value(predictionTimeString);

        jsonWriter.name("typeTime");

        LocalDateTime typeTime = bus.typeTime();

        String typeTimeString;

        if (typeTime == null) {
            typeTimeString = null;
        } else {
            typeTimeString = typeTime.toString();
        } //end if

        jsonWriter.value(typeTimeString);

        jsonWriter.name("delayed");

        Boolean delayed = bus.delayed();

        jsonWriter.value(delayed);

        jsonWriter.endObject();
    } //writeBus

    /**
     * Deserializes a {@link Bus} object using the specified {@link JsonReader}.
     *
     * @param jsonReader the {@link JsonReader} to be used in the operation
     * @return the deserialized {@link Bus} object
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonReader} is {@code null}
     */
    public static Bus readBus(JsonReader jsonReader) throws IOException {
        Integer id = null;

        Integer stopId = null;

        String stopName = null;

        String routeId = null;

        String direction = null;

        String destination = null;

        Type type = null;

        LocalDateTime predictionTime = null;

        LocalDateTime typeTime = null;

        Boolean delayed = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String nameKey = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();

                continue;
            } //end if

            switch (nameKey) {
                case "vid" -> {
                    String idString = jsonReader.nextString();

                    try {
                        id = Integer.parseInt(idString);
                    } catch (NumberFormatException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed ID");
                    } //end try catch
                } //case "vid"
                case "stpid" -> {
                    String stopIdString = jsonReader.nextString();

                    try {
                        stopId = Integer.parseInt(stopIdString);
                    } catch (NumberFormatException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed stop ID");
                    } //end try catch
                } //case "stp"
                case "stpnm" -> stopName = jsonReader.nextString();
                case "rt" -> routeId = jsonReader.nextString();
                case "rtdir" -> direction = jsonReader.nextString();
                case "des" -> destination = jsonReader.nextString();
                case "typ" -> {
                    String typeString = jsonReader.nextString();

                    type = switch (typeString) {
                        case "A" -> Type.ARRIVAL;
                        case "D" -> Type.DEPARTURE;
                        default -> {
                            String errorMessage = "the response includes a novel type: %s".formatted(typeString);

                            LOGGER.atError()
                                  .log(errorMessage);

                            yield null;
                        } //default
                    };
                } //case "typ"
                case "tmstmp" -> {
                    String predictionTimeString = jsonReader.nextString();

                    String formatterPattern = "yyyyMMdd HH:mm";

                    DateTimeFormatter formatter;

                    try {
                        formatter = DateTimeFormatter.ofPattern(formatterPattern);
                    } catch (IllegalArgumentException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the prediction time formatter pattern is malformed");

                        continue;
                    } //end try catch

                    try {
                        predictionTime = LocalDateTime.parse(predictionTimeString, formatter);
                    } catch (DateTimeParseException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed prediction time");
                    } //end try catch
                } //case "prdtm"
                case "prdtm" -> {
                    String arrivalTimeString = jsonReader.nextString();

                    String formatterPattern = "yyyyMMdd HH:mm";

                    DateTimeFormatter formatter;

                    try {
                        formatter = DateTimeFormatter.ofPattern(formatterPattern);
                    } catch (IllegalArgumentException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the arrival time formatter pattern is malformed");

                        continue;
                    } //end try catch

                    try {
                        typeTime = LocalDateTime.parse(arrivalTimeString, formatter);
                    } catch (DateTimeParseException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed arrival time");
                    } //end try catch
                } //case "prdtm"
                case "dly" -> delayed = jsonReader.nextBoolean();
                default -> jsonReader.nextString();
            } //end switch
        } //end while

        jsonReader.endObject();

        Stop stop = new Stop(stopId, stopName);

        Route route = new Route(routeId, null);

        return new Bus(id, stop, route, direction, destination, type, predictionTime, typeTime, delayed);
    } //readBus

    /**
     * Serializes the specified {@link Bus} using the specified {@link JsonWriter}.
     *
     * @param jsonWriter the {@link JsonWriter} to be used in the operation
     * @param bus the {@link Bus} to be used in the operation
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonWriter} or {@link Bus} is {@code null}
     */
    @Override
    public void write(JsonWriter jsonWriter, Bus bus) throws IOException {
        BusTypeAdapter.writeBus(jsonWriter, bus);
    } //write

    /**
     * Deserializes a {@link Bus} object using the specified {@link JsonReader}.
     *
     * @param jsonReader the {@link JsonReader} to be used in the operation
     * @return the deserialized {@link Bus} object
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonReader} is {@code null}
     */
    @Override
    public Bus read(JsonReader jsonReader) throws IOException {
        return BusTypeAdapter.readBus(jsonReader);
    } //read
}