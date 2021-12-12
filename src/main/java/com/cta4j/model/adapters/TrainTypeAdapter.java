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
import com.google.gson.stream.JsonReader;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.stream.JsonToken;
import java.time.LocalDateTime;

/**
 * A type adapter for the {@link Train} class.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version December 11, 2021
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

        jsonWriter.name("rn");

        int rn = train.run();

        String rnString = String.valueOf(rn);

        jsonWriter.value(rnString);

        jsonWriter.name("destNm");

        jsonWriter.value(train.destination());

        jsonWriter.name("nextStaNm");

        jsonWriter.value(train.nextStop());

        jsonWriter.name("arrT");

        String arrTString = train.estimatedArrival()
                                 .toString();

        jsonWriter.value(arrTString);

        jsonWriter.name("isApp");

        boolean isApp = train.approaching();

        String isAppString = isApp ? "1" : "0";

        jsonWriter.value(isAppString);

        jsonWriter.name("isDly");

        boolean isDly = train.delayed();

        String isDlyString = isDly ? "1" : "0";

        jsonWriter.value(isDlyString);

        jsonWriter.name("lat");

        Double lat = train.latitude();

        String latString = String.valueOf(lat);

        jsonWriter.value(latString);

        jsonWriter.name("lon");

        Double lon = train.longitude();

        String lonString = String.valueOf(lon);

        jsonWriter.value(lonString);

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

                    int run = Integer.parseInt(runString);

                    keyToValue.put("run", run);
                } //case "rn"
                case "destNm" -> {
                    String destination = jsonReader.nextString();

                    keyToValue.put("destination", destination);
                } //case "destNm"
                case "nextStaNm" -> {
                    String nextStop = jsonReader.nextString();

                    keyToValue.put("nextStop", nextStop);
                } //case "nextStaNm"
                case "arrT" -> {
                    String estimatedArrivalString = jsonReader.nextString();

                    LocalDateTime estimatedArrival = LocalDateTime.parse(estimatedArrivalString);

                    keyToValue.put("estimatedArrival", estimatedArrival);
                } //case "arrT"
                case "isApp" -> {
                    String approachingString = jsonReader.nextString();

                    String approachingTrue = "1";

                    boolean approaching = Objects.equals(approachingString, approachingTrue);

                    keyToValue.put("approaching", approaching);
                } //case "isApp"
                case "isDly" -> {
                    String delayedString = jsonReader.nextString();

                    String delayedTrue = "1";

                    boolean delayed = Objects.equals(delayedString, delayedTrue);

                    keyToValue.put("delayed", delayed);
                } //case "isDly"
                case "lat" -> {
                    String latitudeString = jsonReader.nextString();

                    double latitude = Double.parseDouble(latitudeString);

                    keyToValue.put("latitude", latitude);
                } //case "lat"
                case "lon" -> {
                    String longitudeString = jsonReader.nextString();

                    double longitude = Double.parseDouble(longitudeString);

                    keyToValue.put("longitude", longitude);
                } //case "lon"
                default -> jsonReader.nextString();
            } //end switch
        } //end while

        jsonReader.endObject();

        Integer run = (Integer) keyToValue.get("run");

        if (run == null) {
            throw new IOException("the member \"rn\" was not included in the response");
        } //end if

        String destination = (String) keyToValue.get("destination");

        if (destination == null) {
            throw new IOException("the member \"destNm\" was not included in the response");
        } //end if

        String nextStop = (String) keyToValue.get("nextStop");

        if (nextStop == null) {
            throw new IOException("the member \"nextStaNm\" was not included in the response");
        } //end if

        LocalDateTime estimatedArrival = (LocalDateTime) keyToValue.get("estimatedArrival");

        if (estimatedArrival == null) {
            throw new IOException("the member \"arrT\" was not included in the response");
        } //end if

        Boolean approaching = (Boolean) keyToValue.get("approaching");

        if (approaching == null) {
            throw new IOException("the member \"isApp\" was not included in the response");
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

        return new Train(run, destination, nextStop, estimatedArrival, approaching, delayed, latitude, longitude);
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