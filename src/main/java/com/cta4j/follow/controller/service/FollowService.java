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

            List<Train> trains = List.of();

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

            List<Train> trains = List.of();

            schedule = new Schedule(trains);
        } //end try catch

        Body<?> body = Body.success(schedule);

        return ResponseEntity.ok(body);
    } //getSchedule
}