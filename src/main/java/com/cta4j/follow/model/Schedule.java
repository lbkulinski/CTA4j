package com.cta4j.follow.model;

import com.cta4j.follow.model.deserializer.ScheduleDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = ScheduleDeserializer.class)
public record Schedule(List<Train> trains) {
}