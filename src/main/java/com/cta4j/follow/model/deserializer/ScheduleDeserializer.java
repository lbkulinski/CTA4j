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

package com.cta4j.follow.model.deserializer;

import com.cta4j.follow.model.Schedule;
import com.cta4j.follow.model.Train;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A deserializer for the {@link Schedule} class.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @version November 26, 2022
 */
public final class ScheduleDeserializer extends StdDeserializer<Schedule> {
    /**
     * Constructs an instance of the {@link ScheduleDeserializer} class.
     *
     * @param clazz the {@link Class} to be used in the operation
     */
    public ScheduleDeserializer(Class<?> clazz) {
        super(clazz);
    } //ScheduleDeserializer

    /**
     * Constructs an instance of the {@link ScheduleDeserializer} class.
     */
    public ScheduleDeserializer() {
        this(null);
    } //ScheduleDeserializer

    /**
     * Returns a {@link Train}'s route using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s route using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a route
     */
    private String getRoute(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode routeNode = jsonNode.get("rt");

        if ((routeNode == null) || !routeNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"rt\" does not exist or is not a string in the specified content");
        } //end if

        String[] routeParts = routeNode.asText()
                                       .split("\s+");

        if (routeParts.length == 0) {
            throw new JsonMappingException(jsonParser,
                "the field \"rt\" is not valid in the specified content");
        } //end if

        return routeParts[0].toUpperCase();
    } //getRoute

    /**
     * Returns a {@link Train}'s run using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s run using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a run
     */
    private int getRun(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode runNode = jsonNode.get("rn");

        if ((runNode == null) || !runNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"rt\" does not exist or is not a string in the specified content");
        } //end if

        String runString = runNode.asText();

        int run;

        try {
            run = Integer.parseInt(runString);
        } catch (NumberFormatException e) {
            throw new JsonMappingException(jsonParser,
                "the field \"rt\" in the specified content is not a valid int", e);
        } //end try catch

        return run;
    } //getRun

    /**
     * Returns a {@link Train}'s station using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s station using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a station
     */
    private String getStation(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode stationNode = jsonNode.get("staNm");

        if ((stationNode == null) || !stationNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"staNm\" does not exist or is not a string in the specified content");
        } //end if

        return stationNode.asText();
    } //getStation

    /**
     * Returns a {@link Train}'s destination using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s destination using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a destination
     */
    private String getDestination(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode destinationNode = jsonNode.get("destNm");

        if ((destinationNode == null) || !destinationNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"destNm\" does not exist or is not a string in the specified content");
        } //end if

        return destinationNode.asText();
    } //getDestination

    /**
     * Returns a {@link Train}'s prediction time using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s prediction time using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a prediction time
     */
    private LocalDateTime getPredictionTime(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode predictionTimeNode = jsonNode.get("prdt");

        if ((predictionTimeNode == null) || !predictionTimeNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"prdt\" does not exist or is not a string in the specified content");
        } //end if

        String predictionTimeString = predictionTimeNode.asText();

        LocalDateTime predictionTime;

        try {
            predictionTime = LocalDateTime.parse(predictionTimeString);
        } catch (DateTimeParseException e) {
            throw new JsonMappingException(jsonParser,
                "the field \"prdt\" in the specified content is not a valid date", e);
        } //end try catch

        return predictionTime;
    } //getPredictionTime

    /**
     * Returns a {@link Train}'s arrival time using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s arrival time using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to an arrival time
     */
    private LocalDateTime getArrivalTime(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode arrivalTimeNode = jsonNode.get("arrT");

        if ((arrivalTimeNode == null) || !arrivalTimeNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"arrT\" does not exist or is not a string in the specified content");
        } //end if

        String arrivalTimeString = arrivalTimeNode.asText();

        LocalDateTime arrivalTime;

        try {
            arrivalTime = LocalDateTime.parse(arrivalTimeString);
        } catch (DateTimeParseException e) {
            throw new JsonMappingException(jsonParser,
                "the field \"arrT\" in the specified content is not a valid date", e);
        } //end try catch

        return arrivalTime;
    } //getArrivalTime

    /**
     * Returns a {@link Train}'s due flag using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s due flag using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a due flag
     */
    private Boolean getDue(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode dueNode = jsonNode.get("isApp");

        if ((dueNode == null) || !dueNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"isApp\" does not exist or is not a string in the specified content");
        } //end if

        String dueString = dueNode.asText();

        String trueString = "1";

        return Objects.equals(dueString, trueString);
    } //getDue

    /**
     * Returns a {@link Train}'s scheduled flag using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s scheduled flag using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a scheduled flag
     */
    private Boolean getScheduled(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode scheduledNode = jsonNode.get("isSch");

        if ((scheduledNode == null) || !scheduledNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"isSch\" does not exist or is not a string in the specified content");
        } //end if

        String scheduledString = scheduledNode.asText();

        String trueString = "1";

        return Objects.equals(scheduledString, trueString);
    } //getScheduled

    /**
     * Returns a {@link Train}'s fault flag using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s fault flag using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a fault flag
     */
    private Boolean getFault(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode faultNode = jsonNode.get("isFlt");

        if ((faultNode == null) || !faultNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"isFlt\" does not exist or is not a string in the specified content");
        } //end if

        String faultString = faultNode.asText();

        String trueString = "1";

        return Objects.equals(faultString, trueString);
    } //getFault

    /**
     * Returns a {@link Train}'s delayed flag using the specified {@link JsonParser} and {@link JsonNode}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param jsonNode the {@link JsonNode} to be used in the operation
     * @return a {@link Train}'s delayed flag using the specified {@link JsonParser} and {@link JsonNode}
     * @throws JsonMappingException if the specified {@link JsonNode} cannot be mapped to a delayed flag
     */
    private Boolean getDelayed(JsonParser jsonParser, JsonNode jsonNode) throws JsonMappingException {
        JsonNode delayedNode = jsonNode.get("isDly");

        if ((delayedNode == null) || !delayedNode.isTextual()) {
            throw new JsonMappingException(jsonParser,
                "the field \"isDly\" does not exist or is not a string in the specified content");
        } //end if

        String delayedString = delayedNode.asText();

        String trueString = "1";

        return Objects.equals(delayedString, trueString);
    } //getDelayed

    /**
     * Returns a {@link Train} that is deserialized using the specified {@link JsonParser} and
     * {@link DeserializationContext}.
     *
     * @param jsonParser the {@link JsonParser} to be used in the operation
     * @param deserializationContext the {@link DeserializationContext} to be used in the operation
     * @return a {@link Train} that is deserialized using the specified {@link JsonParser} and
     * {@link DeserializationContext}
     * @throws IOException if an I/O error occurs
     */
    @Override
    public Schedule deserialize(JsonParser jsonParser,
        DeserializationContext deserializationContext) throws IOException {
        JsonNode rootNode = jsonParser.getCodec()
                                      .readTree(jsonParser);

        JsonNode ctattNode = rootNode.get("ctatt");

        if ((ctattNode == null) || !ctattNode.isObject()) {
            throw new JsonMappingException(jsonParser,
                "the field \"ctatt\" does not exist or is not an object in the specified content");
        } //end if

        JsonNode etasNode = ctattNode.get("eta");

        if ((etasNode == null) || !etasNode.isArray()) {
            throw new JsonMappingException(jsonParser,
                "the field \"eta\" does not exist or is not an array in the specified content");
        } //end if

        if (etasNode.isEmpty()) {
            throw new JsonMappingException(jsonParser,
                "the field \"eta\" does not contain any elements in the specified content");
        } //end if

        List<Train> trains = new ArrayList<>();

        for (JsonNode etaNode : etasNode) {
            String route = this.getRoute(jsonParser, etaNode);

            int run = this.getRun(jsonParser, etaNode);

            String station = this.getStation(jsonParser, etaNode);

            String destination = this.getDestination(jsonParser, etaNode);

            LocalDateTime predictionTime = this.getPredictionTime(jsonParser, etaNode);

            LocalDateTime arrivalTime = this.getArrivalTime(jsonParser, etaNode);

            Boolean due = this.getDue(jsonParser, etaNode);

            Boolean scheduled = this.getScheduled(jsonParser, etaNode);

            Boolean fault = this.getFault(jsonParser, etaNode);

            Boolean delayed = this.getDelayed(jsonParser, etaNode);

            Train train = new Train(route, run, station, destination, predictionTime, arrivalTime, due, scheduled,
                fault, delayed);

            trains.add(train);
        } //end for

        return new Schedule(trains);
    } //deserialize
}