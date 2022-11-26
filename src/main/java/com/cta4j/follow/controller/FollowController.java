package com.cta4j.follow.controller;

import com.cta4j.follow.controller.service.FollowService;
import com.cta4j.utils.Body;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/follow")
public final class FollowController {
    private final FollowService service;

    public FollowController(FollowService service) {
        Objects.requireNonNull(service, "the specified service is null");

        this.service = service;
    } //FollowController

    @GetMapping
    public ResponseEntity<Body<?>> read(@RequestParam int run) {
        return this.service.getSchedule(run);
    } //read
}