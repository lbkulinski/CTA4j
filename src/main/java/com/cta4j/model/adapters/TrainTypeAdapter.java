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
import com.cta4j.model.Train;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Objects;
import com.cta4j.model.Route;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.stream.JsonReader;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.stream.JsonToken;
import java.time.format.DateTimeParseException;

/**
 * A type adapter for the {@link Train} class.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 28, 2021
 */
public final class TrainTypeAdapter extends TypeAdapter<Train> {
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

        int run = train.run();

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

        boolean due = train.due();

        jsonWriter.value(due);

        jsonWriter.name("scheduled");

        boolean scheduled = train.scheduled();

        jsonWriter.value(scheduled);

        jsonWriter.name("fault");

        boolean fault = train.fault();

        jsonWriter.value(fault);

        jsonWriter.name("delayed");

        boolean delayed = train.delayed();

        jsonWriter.value(delayed);

        jsonWriter.name("latitude");

        double latitude = train.latitude();

        jsonWriter.value(latitude);

        jsonWriter.name("longitude");

        double longitude = train.longitude();

        jsonWriter.value(longitude);

        jsonWriter.name("heading");

        int heading = train.heading();

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

        Map<String, Object> keyToValue = new HashMap<>();

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

                    int run;

                    try {
                        run = Integer.parseInt(runString);
                    } catch (NumberFormatException e) {
                        throw new IOException("the response includes a malformed run");
                    } //end try catch

                    keyToValue.put("run", run);
                } //case "rn"
                case "rt" -> {
                    String routeString = jsonReader.nextString();

                    routeString = routeString.toLowerCase();

                    Route route = switch (routeString) {
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

                            throw new IOException(errorMessage);
                        } //default
                    };

                    keyToValue.put("route", route);
                } //case "rt"
                case "destNm" -> {
                    String destination = jsonReader.nextString();

                    keyToValue.put("destination", destination);
                } //case "destNm"
                case "staNm" -> {
                    String station = jsonReader.nextString();

                    keyToValue.put("station", station);
                } //case "staNm"
                case "stpDe" -> {
                    String description = jsonReader.nextString();

                    keyToValue.put("description", description);
                } //case "stpDe"
                case "prdt" -> {
                    String predictionTimeString = jsonReader.nextString();

                    LocalDateTime predictionTime;

                    try {
                        predictionTime = LocalDateTime.parse(predictionTimeString);
                    } catch (DateTimeParseException e) {
                        throw new IOException("the response includes a malformed prediction time");
                    } //end try catch

                    keyToValue.put("predictionTime", predictionTime);
                } //case "prdt"
                case "arrT" -> {
                    String arrivalTimeString = jsonReader.nextString();

                    LocalDateTime arrivalTime;

                    try {
                        arrivalTime = LocalDateTime.parse(arrivalTimeString);
                    } catch (DateTimeParseException e) {
                        throw new IOException("the response includes a malformed arrival time");
                    } //end try catch

                    keyToValue.put("arrivalTime", arrivalTime);
                } //case "arrT"
                case "isApp" -> {
                    String dueString = jsonReader.nextString();

                    String dueTrue = "1";

                    boolean due = Objects.equals(dueString, dueTrue);

                    keyToValue.put("due", due);
                } //case "isApp"
                case "isSch" -> {
                    String scheduledString = jsonReader.nextString();

                    String scheduledTrue = "1";

                    boolean scheduled = Objects.equals(scheduledString, scheduledTrue);

                    keyToValue.put("scheduled", scheduled);
                } //case "isSch"
                case "isFlt" -> {
                    String faultString = jsonReader.nextString();

                    String faultTrue = "1";

                    boolean fault = Objects.equals(faultString, faultTrue);

                    keyToValue.put("fault", fault);
                } //case "isFlt"
                case "isDly" -> {
                    String delayedString = jsonReader.nextString();

                    String delayedTrue = "1";

                    boolean delayed = Objects.equals(delayedString, delayedTrue);

                    keyToValue.put("delayed", delayed);
                } //case "isDly"
                case "lat" -> {
                    String latitudeString = jsonReader.nextString();

                    double latitude;

                    try {
                        latitude = Double.parseDouble(latitudeString);
                    } catch (NumberFormatException e) {
                        throw new IOException("the response includes a malformed latitude");
                    } //end try catch

                    keyToValue.put("latitude", latitude);
                } //case "lat"
                case "lon" -> {
                    String longitudeString = jsonReader.nextString();

                    double longitude;

                    try {
                        longitude = Double.parseDouble(longitudeString);
                    } catch (NumberFormatException e) {
                        throw new IOException("the response includes a malformed longitude");
                    } //end try catch

                    keyToValue.put("longitude", longitude);
                } //case "lon"
                case "heading" -> {
                    String headingString = jsonReader.nextString();

                    int heading;

                    try {
                        heading = Integer.parseInt(headingString);
                    } catch (NumberFormatException e) {
                        throw new IOException("the response includes a malformed heading");
                    } //end try catch

                    keyToValue.put("heading", heading);
                } //case "heading"
                default -> jsonReader.nextString();
            } //end switch
        } //end while

        jsonReader.endObject();

        Integer run = (Integer) keyToValue.get("run");

        if (run == null) {
            throw new IOException("the member \"rn\" was not included in the response");
        } //end if

        Route route = (Route) keyToValue.get("route");

        if (route == null) {
            throw new IOException("the member \"rt\" was not included in the response");
        } //end if

        String destination = (String) keyToValue.get("destination");

        if (destination == null) {
            throw new IOException("the member \"destNm\" was not included in the response");
        } //end if

        String station = (String) keyToValue.get("station");

        if (station == null) {
            throw new IOException("the member \"staNm\" was not included in the response");
        } //end if

        String description = (String) keyToValue.get("description");

        if (description == null) {
            throw new IOException("the member \"stpDe\" was not included in the response");
        } //end if

        LocalDateTime predictionTime = (LocalDateTime) keyToValue.get("predictionTime");

        if (predictionTime == null) {
            throw new IOException("the member \"prdt\" was not included in the response");
        } //end if

        LocalDateTime arrivalTime = (LocalDateTime) keyToValue.get("arrivalTime");

        if (arrivalTime == null) {
            throw new IOException("the member \"arrT\" was not included in the response");
        } //end if

        Boolean due = (Boolean) keyToValue.get("due");

        if (due == null) {
            throw new IOException("the member \"isApp\" was not included in the response");
        } //end if

        Boolean scheduled = (Boolean) keyToValue.get("scheduled");

        if (scheduled == null) {
            throw new IOException("the member \"isSch\" was not included in the response");
        } //end if

        Boolean fault = (Boolean) keyToValue.get("fault");

        if (fault == null) {
            throw new IOException("the member \"isFlt\" was not included in the response");
        } //end if

        Boolean delayed = (Boolean) keyToValue.get("delayed");

        if (delayed == null) {
            throw new IOException("the member \"isDly\" was not included in the response");
        } //end if

        Double latitude = (Double) keyToValue.get("latitude");

        if (latitude == null) {
            throw new IOException("the member \"lat\" was not included in the response");
        } //end if

        Double longitude = (Double) keyToValue.get("longitude");

        if (longitude == null) {
            throw new IOException("the member \"lon\" was not included in the response");
        } //end if

        Integer heading = (Integer) keyToValue.get("heading");

        if (heading == null) {
            throw new IOException("the member \"heading\" was not included in the response");
        } //end if

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