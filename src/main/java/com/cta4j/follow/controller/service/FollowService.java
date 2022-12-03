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

package com.cta4j.follow.controller.service;

import com.cta4j.follow.model.Schedule;
import com.cta4j.follow.model.Train;
import com.cta4j.utils.Body;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * A service of the CTA4j application used to follow trains.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @version November 26, 2022
 */
@Service
public final class FollowService {
    /**
     * The {@link Logger} of the {@link FollowService} class.
     */
    private static final Logger LOGGER;

    /**
     * The {@link Properties} of the {@link FollowService} class.
     */
    private static final Properties PROPERTIES;

    static {
        LOGGER = LogManager.getLogger(FollowService.class);

        PROPERTIES = new Properties();

        FollowService.loadProperties();
    } //static

    /**
     * Loads the properties of this {@link FollowService}.
     */
    private static void loadProperties() {
        String pathString = "src/main/resources/api-key.properties";

        Path path = Path.of(pathString);

        try (var reader = Files.newBufferedReader(path)) {
            FollowService.PROPERTIES.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } //end try catch
    } //loadProperties

    /**
     * Returns a {@link ResponseEntity} containing the outcome of reading the {@link Schedule} associated with the
     * specified run.
     *
     * @param run the run to be used in the operation
     * @return a {@link ResponseEntity} containing the outcome of reading the {@link Schedule} associated with the
     * specified run
     */
    public ResponseEntity<Body<?>> getSchedule(int run) {
        String key = FollowService.PROPERTIES.getProperty("train_key");

        if (key == null) {
            return ResponseEntity.internalServerError()
                                 .build();
        } //end if

        String uriString = """
        https://lapi.transitchicago.com/api/1.0/ttfollow.aspx\
        ?key=%s&runnumber=%s&outputType=JSON""".formatted(key, run);

        URI uri = URI.create(uriString);

        HttpRequest request = HttpRequest.newBuilder(uri)
                                         .GET()
                                         .build();

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response;

        try {
            response = client.send(request, bodyHandler);
        } catch (IOException | InterruptedException e) {
            FollowService.LOGGER.atError()
                                .withThrowable(e)
                                .log();

            Set<Train> trains = Set.of();

            Schedule schedule = new Schedule(trains);

            Body<?> body = Body.success(schedule);

            return ResponseEntity.ok()
                                 .body(body);
        } //end try catch

        String responseBody = response.body();

        ObjectMapper mapper = new ObjectMapper();

        Schedule schedule;

        try {
            schedule = mapper.readValue(responseBody, Schedule.class);
        } catch (JsonProcessingException e) {
            FollowService.LOGGER.atError()
                                .withThrowable(e)
                                .log();

            Set<Train> trains = Set.of();

            schedule = new Schedule(trains);
        } //end try catch

        Body<?> body = Body.success(schedule);

        return ResponseEntity.ok(body);
    } //getSchedule
}