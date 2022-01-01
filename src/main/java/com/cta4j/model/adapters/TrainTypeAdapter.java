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
import com.cta4j.model.train.Train;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Objects;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.stream.JsonReader;
import com.cta4j.model.train.Route;
import com.google.gson.stream.JsonToken;
import java.time.format.DateTimeParseException;

/**
 * A type adapter for the {@link Train} class.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 31, 2021
 */
public final class TrainTypeAdapter extends TypeAdapter<Train> {
    /**
     * The {@link Logger} of this type adapter.
     */
    private static final Logger LOGGER;

    static {
        LOGGER = LogManager.getLogger(TrainTypeAdapter.class);
    } //static

    /**
     * Constructs an instance of the {@link TrainTypeAdapter} class.
     */
    public TrainTypeAdapter() {
    } //TrainTypeAdapter

    /**
     * Serializes the specified {@link Train} using the specified {@link JsonWriter}.
     *
     * @param jsonWriter the {@link JsonWriter} to be used in the operation
     * @param train the {@link Train} to be used in the operation
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonWriter} or {@link Train} is {@code null}
     */
    public static void writeTrain(JsonWriter jsonWriter, Train train) throws IOException {
        Objects.requireNonNull(jsonWriter, "the specified JsonWriter is null");

        Objects.requireNonNull(train, "the specified Train is null");

        jsonWriter.beginObject();

        jsonWriter.name("run");

        Integer run = train.run();

        jsonWriter.value(run);

        jsonWriter.name("route");

        String route = train.route()
                            .toString();

        jsonWriter.value(route);

        jsonWriter.name("destination");

        String destination = train.destination();

        jsonWriter.value(destination);

        jsonWriter.name("station");

        String station = train.station();

        jsonWriter.value(station);

        jsonWriter.name("description");

        String description = train.description();

        jsonWriter.value(description);

        jsonWriter.name("prediction_time");

        LocalDateTime predictionTime = train.predictionTime();

        String predictionTimeString = predictionTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        jsonWriter.value(predictionTimeString);

        jsonWriter.name("arrival_time");

        LocalDateTime arrivalTime = train.arrivalTime();

        String arrivalTimeString = arrivalTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        jsonWriter.value(arrivalTimeString);

        jsonWriter.name("due");

        Boolean due = train.due();

        jsonWriter.value(due);

        jsonWriter.name("scheduled");

        Boolean scheduled = train.scheduled();

        jsonWriter.value(scheduled);

        jsonWriter.name("fault");

        Boolean fault = train.fault();

        jsonWriter.value(fault);

        jsonWriter.name("delayed");

        Boolean delayed = train.delayed();

        jsonWriter.value(delayed);

        jsonWriter.name("latitude");

        Double latitude = train.latitude();

        jsonWriter.value(latitude);

        jsonWriter.name("longitude");

        Double longitude = train.longitude();

        jsonWriter.value(longitude);

        jsonWriter.name("heading");

        Integer heading = train.heading();

        jsonWriter.value(heading);

        jsonWriter.endObject();
    } //writeTrain

    /**
     * Deserializes a {@link Train} object using the specified {@link JsonReader}.
     *
     * @param jsonReader the {@link JsonReader} to be used in the operation
     * @return the deserialized {@link Train} object
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonReader} is {@code null}
     */
    public static Train readTrain(JsonReader jsonReader) throws IOException {
        Objects.requireNonNull(jsonReader, "the specified JsonReader is null");

        Integer run = null;

        Route route = null;

        String destination = null;

        String station = null;

        String description = null;

        LocalDateTime predictionTime = null;

        LocalDateTime arrivalTime = null;

        Boolean due = null;

        Boolean scheduled = null;

        Boolean fault = null;

        Boolean delayed = null;

        Double latitude = null;

        Double longitude = null;

        Integer heading = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String nameKey = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();

                continue;
            } //end if

            switch (nameKey) {
                case "rn" -> {
                    String runString = jsonReader.nextString();

                    try {
                        run = Integer.parseInt(runString);
                    } catch (NumberFormatException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed run");
                    } //end try catch
                } //case "rn"
                case "rt" -> {
                    String routeString = jsonReader.nextString();

                    routeString = routeString.toLowerCase();

                    route = switch (routeString) {
                        case "red" -> Route.RED;
                        case "blue" -> Route.BLUE;
                        case "brn" -> Route.BROWN;
                        case "g" -> Route.GREEN;
                        case "org" -> Route.ORANGE;
                        case "p", "pexp" -> Route.PURPLE;
                        case "pink" -> Route.PINK;
                        case "y" -> Route.YELLOW;
                        default -> {
                            String errorMessage = "the response includes a novel route: %s".formatted(routeString);

                            LOGGER.atError()
                                  .log(errorMessage);

                            yield null;
                        } //default
                    };
                } //case "rt"
                case "destNm" -> destination = jsonReader.nextString();
                case "staNm" -> station = jsonReader.nextString();
                case "stpDe" -> description = jsonReader.nextString();
                case "prdt" -> {
                    String predictionTimeString = jsonReader.nextString();

                    try {
                        predictionTime = LocalDateTime.parse(predictionTimeString);
                    } catch (DateTimeParseException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed prediction time");
                    } //end try catch
                } //case "prdt"
                case "arrT" -> {
                    String arrivalTimeString = jsonReader.nextString();

                    try {
                        arrivalTime = LocalDateTime.parse(arrivalTimeString);
                    } catch (DateTimeParseException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed arrival time");
                    } //end try catch
                } //case "arrT"
                case "isApp" -> {
                    String dueString = jsonReader.nextString();

                    String dueTrue = "1";

                    due = Objects.equals(dueString, dueTrue);
                } //case "isApp"
                case "isSch" -> {
                    String scheduledString = jsonReader.nextString();

                    String scheduledTrue = "1";

                    scheduled = Objects.equals(scheduledString, scheduledTrue);
                } //case "isSch"
                case "isFlt" -> {
                    String faultString = jsonReader.nextString();

                    String faultTrue = "1";

                    fault = Objects.equals(faultString, faultTrue);
                } //case "isFlt"
                case "isDly" -> {
                    String delayedString = jsonReader.nextString();

                    String delayedTrue = "1";

                    delayed = Objects.equals(delayedString, delayedTrue);
                } //case "isDly"
                case "lat" -> {
                    String latitudeString = jsonReader.nextString();

                    try {
                        latitude = Double.parseDouble(latitudeString);
                    } catch (NumberFormatException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed latitude");
                    } //end try catch
                } //case "lat"
                case "lon" -> {
                    String longitudeString = jsonReader.nextString();

                    try {
                        longitude = Double.parseDouble(longitudeString);
                    } catch (NumberFormatException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed longitude");
                    } //end try catch
                } //case "lon"
                case "heading" -> {
                    String headingString = jsonReader.nextString();

                    try {
                        heading = Integer.parseInt(headingString);
                    } catch (NumberFormatException e) {
                        LOGGER.atError()
                              .withThrowable(e)
                              .log("the response includes a malformed heading");
                    } //end try catch
                } //case "heading"
                default -> jsonReader.nextString();
            } //end switch
        } //end while

        jsonReader.endObject();

        return new Train(run, route, destination, station, description, predictionTime, arrivalTime, due, scheduled,
                         fault, delayed, latitude, longitude, heading);
    } //readTrain

    /**
     * Serializes the specified {@link Train} using the specified {@link JsonWriter}.
     *
     * @param jsonWriter the {@link JsonWriter} to be used in the operation
     * @param train the {@link Train} to be used in the operation
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonWriter} or {@link Train} is {@code null}
     */
    @Override
    public void write(JsonWriter jsonWriter, Train train) throws IOException {
        TrainTypeAdapter.writeTrain(jsonWriter, train);
    } //write

    /**
     * Deserializes a {@link Train} object using the specified {@link JsonReader}.
     *
     * @param jsonReader the {@link JsonReader} to be used in the operation
     * @return the deserialized {@link Train} object
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if the specified {@link JsonReader} is {@code null}
     */
    @Override
    public Train read(JsonReader jsonReader) throws IOException {
        return TrainTypeAdapter.readTrain(jsonReader);
    } //read
}