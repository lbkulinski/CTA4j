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

package com.cta4j.follow.controller;

import com.cta4j.follow.controller.service.FollowService;
import com.cta4j.utils.Body;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * A controller of the CTA4j application used to follow trains.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @version November 26, 2022
 */
@RestController
@RequestMapping("/api/follow")
public final class FollowController {
    /**
     * The {@link FollowService} of this {@link FollowController}.
     */
    private final FollowService service;

    /**
     * Constructs an instance of the {@link FollowController} class.
     *
     * @param service the {@link FollowService} to be used in the operation
     */
    public FollowController(FollowService service) {
        Objects.requireNonNull(service, "the specified service is null");

        this.service = service;
    } //FollowController

    /**
     * Returns a {@link ResponseEntity} containing the outcome of reading the {@link com.cta4j.follow.model.Schedule}
     * associated with the specified run.
     *
     * @param run the run to be used in the operation
     * @return a {@link ResponseEntity} containing the outcome of reading the {@link com.cta4j.follow.model.Schedule}
     * associated with the specified run
     */
    @GetMapping
    public ResponseEntity<Body<?>> read(@RequestParam int run) {
        return this.service.getSchedule(run);
    } //read
}