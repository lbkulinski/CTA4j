package com.cta4j.model.adapters.bus;

import com.cta4j.model.bus.Route;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

public final class RouteTypeAdapter extends TypeAdapter<Route> {
    public static void writeRoute(JsonWriter jsonWriter, Route route) throws IOException {
        Objects.requireNonNull(jsonWriter, "the specified JSON writer is null");

        Objects.requireNonNull(route, "the specified route is null");

        jsonWriter.beginObject();

        jsonWriter.name("id");

        String id = route.id();

        jsonWriter.value(id);

        jsonWriter.name("name");

        String name = route.name();

        jsonWriter.value(name);

        jsonWriter.endObject();
    } //writeRoute

    @Override
    public void write(JsonWriter jsonWriter, Route route) throws IOException {
        RouteTypeAdapter.writeRoute(jsonWriter, route);
    } //write

    public static Route readRoute(JsonReader jsonReader) throws IOException {
        Objects.requireNonNull(jsonReader, "the specified JSON reader is null");

        jsonReader.beginObject();

        String routeId = null;

        String routeName = null;

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();

                continue;
            } //end if

            switch (name) {
                case "rt" -> routeId = jsonReader.nextString();
                case "rtnm" -> routeName = jsonReader.nextString();
                default -> jsonReader.nextString();
            } //end switch
        } //end while

        jsonReader.endObject();

        return new Route(routeId, routeName);
    } //readRoute

    @Override
    public Route read(JsonReader jsonReader) throws IOException {
        return RouteTypeAdapter.readRoute(jsonReader);
    } //read
}