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
import com.cta4j.model.bus.Bus;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Objects;
import com.google.gson.stream.JsonReader;
import com.cta4j.model.bus.Type;
import com.google.gson.stream.JsonToken;

/**
 * A type adapter for the {@link Bus} class.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 31, 2021
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

        String route = bus.route();

        jsonWriter.value(route);

        jsonWriter.name("destination");

        String destination = bus.destination();

        jsonWriter.value(destination);

        jsonWriter.name("direction");

        String direction = bus.direction();

        jsonWriter.value(direction);

        jsonWriter.name("stop");

        String stop = bus.stop();

        jsonWriter.value(stop);

        jsonWriter.name("type");

        String typeString = bus.type()
                               .toString();

        jsonWriter.value(typeString);

        jsonWriter.name("delayed");

        Boolean delayed = bus.delayed();

        jsonWriter.value(delayed);

        jsonWriter.name("eta_minutes");

        Integer etaMinutes = bus.etaMinutes();

        jsonWriter.value(etaMinutes);

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

        String route = null;

        String destination = null;

        String direction = null;

        String stop = null;

        Type type = null;

        Boolean delayed = null;

        Integer etaMinutes = null;

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
                case "rt" -> route = jsonReader.nextString();
                case "des" -> destination = jsonReader.nextString();
                case "rtdir" -> direction = jsonReader.nextString();
                case "stpnm" -> stop = jsonReader.nextString();
                case "typ" -> {
                    String typeString = jsonReader.nextString();

                    type = switch (typeString) {
                        case "A" -> Type.ARRIVAL;
                        case "D" -> Type.DEPARTURE;
                        default -> {
                            String errorMessage = "the response includes a novel type: %s".formatted(type);

                            LOGGER.atError()
                                  .log(errorMessage);

                            yield null;
                        } //default
                    };
                } //case "typ"
                case "dly" -> delayed = jsonReader.nextBoolean();
                case "prdctdn" -> {
                    String etaMinutesString = jsonReader.nextString();

                    String due = "DUE";

                    if (etaMinutesString.equalsIgnoreCase(due)) {
                        etaMinutesString = "0";
                    } //end if

                    try {
                        etaMinutes = Integer.parseInt(etaMinutesString);
                    } catch (NumberFormatException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed ETA");
                    } //end try catch
                } //case "prdctdn"
                default -> jsonReader.nextString();
            } //end switch
        } //end while

        jsonReader.endObject();

        return new Bus(id, route, destination, direction, stop, type, delayed, etaMinutes);
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