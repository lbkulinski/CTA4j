package com.cta4j.model.adapters.bus;

import com.cta4j.model.bus.Stop;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

public final class StopTypeAdapter extends TypeAdapter<Stop> {
    public static void writeStop(JsonWriter jsonWriter, Stop stop) throws IOException {
        Objects.requireNonNull(jsonWriter, "the specified JSON writer is null");

        Objects.requireNonNull(stop, "the specified route is null");

        jsonWriter.beginObject();

        jsonWriter.name("id");

        Integer id = stop.id();

        jsonWriter.value(id);

        jsonWriter.name("name");

        String name = stop.name();

        jsonWriter.value(name);

        jsonWriter.endObject();
    } //writeRoute

    @Override
    public void write(JsonWriter jsonWriter, Stop stop) throws IOException {
        StopTypeAdapter.writeStop(jsonWriter, stop);
    } //write

    public static Stop readStop(JsonReader jsonReader) throws IOException {
        Objects.requireNonNull(jsonReader, "the specified JSON reader is null");

        jsonReader.beginObject();

        Integer stopId = null;

        String stopName = null;

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();

                continue;
            } //end if

            switch (name) {
                case "stpid" -> stopId = jsonReader.nextInt();
                case "stpnm" -> stopName = jsonReader.nextString();
                default -> jsonReader.nextString();
            } //end switch
        } //end while

        jsonReader.endObject();

        return new Stop(stopId, stopName);
    } //readRoute

    @Override
    public Stop read(JsonReader jsonReader) throws IOException {
        return StopTypeAdapter.readStop(jsonReader);
    } //read
}