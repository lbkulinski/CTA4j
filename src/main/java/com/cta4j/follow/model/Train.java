package com.cta4j.follow.model;

import java.time.LocalDateTime;

/**
 * A train of the Chicago Transit Authority.
 *
 * @author Logan Kulinski, rashes_lineage02@icloud.com
 * @version November 26, 2022
 * @param route the route of this {@link Train}
 * @param run the run of this {@link Train}
 * @param station the station of this {@link Train}
 * @param destination the destination of this {@link Train}
 * @param predictionTime the prediction time of this {@link Train}
 * @param arrivalTime the arrival time of this {@link Train}
 */
public record Train(String route, int run, String station, String destination, LocalDateTime predictionTime,
    LocalDateTime arrivalTime) {
}