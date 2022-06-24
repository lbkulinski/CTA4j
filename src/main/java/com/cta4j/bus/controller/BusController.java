package com.cta4j.bus.controller;

import com.cta4j.bus.model.Bus;
import com.cta4j.bus.model.Route;
import com.cta4j.bus.model.Stop;
import com.cta4j.utils.Body;
import com.cta4j.bus.utils.BusUtils;
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
     * Returns a JSON response containing information about routes.
     *
     * @return a JSON response containing information about routes
     */
    @GetMapping("/route")
    public ResponseEntity<Body<Set<Route>>> read() {
        Set<Route> routes = BusUtils.getRoutes();

        Body<Set<Route>> body = Body.success(routes);

        return new ResponseEntity<>(body, HttpStatus.OK);
    } //getBuses

    /**
     * Returns a JSON response containing information about directions.
     *
     * @return a JSON response containing information about directions
     */
    @GetMapping("/direction")
    public ResponseEntity<Body<Set<String>>> read(@RequestParam String route) {
        Set<String> directions = BusUtils.getDirections(route);

        Body<Set<String>> body = Body.success(directions);

        return new ResponseEntity<>(body, HttpStatus.OK);
    } //getBuses

    /**
     * Returns a JSON response containing information about stops.
     *
     * @return a JSON response containing information about stops
     */
    @GetMapping("/stop")
    public ResponseEntity<Body<Set<Stop>>> read(@RequestParam String route, @RequestParam String direction) {
        Set<Stop> stops = BusUtils.getStops(route, direction);

        Body<Set<Stop>> body = Body.success(stops);

        return new ResponseEntity<>(body, HttpStatus.OK);
    } //getBuses

    /**
     * Returns a JSON response containing information about buses using the specified stop ID and routes.
     *
     * @param stopId the stop ID to be used in the operation
     * @param routes the routes to be used in the operation
     * @return a JSON response containing information about buses using the specified stop ID and routes
     */
    @GetMapping
    public ResponseEntity<Body<Set<Bus>>> read(@RequestParam("stop_id") int stopId,
                                               @RequestParam(value = "route[]", required = false) String[] routes) {
        if (routes == null) {
            routes = new String[0];
        } //end if

        Set<Bus> buses = BusUtils.getBuses(stopId, routes);

        Body<Set<Bus>> body = Body.success(buses);

        return new ResponseEntity<>(body, HttpStatus.OK);
    } //getBuses
}