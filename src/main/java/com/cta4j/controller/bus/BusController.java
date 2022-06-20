package com.cta4j.controller.bus;

import com.cta4j.model.bus.Bus;
import com.cta4j.utils.Body;
import com.cta4j.utils.BusUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/bus")
public final class BusController {
    /**
     * Returns a JSON response containing information about buses using the specified stop ID and routes.
     *
     * @param stopId the stop ID to be used in the operation
     * @param routes the routes to be used in the operation
     * @return a JSON response containing information about buses using the specified stop ID and routes
     */
    @GetMapping
    public ResponseEntity<Body<Set<Bus>>> read(@RequestParam(value = "stop_id") int stopId,
                                               @RequestParam(value = "route[]", required = false) String[] routes) {
        if (routes == null) {
            routes = new String[0];
        } //end if

        Set<Bus> buses = BusUtils.getBuses(stopId, routes);

        Body<Set<Bus>> body = Body.success(buses);

        return new ResponseEntity<>(body, HttpStatus.OK);
    } //getBuses
}