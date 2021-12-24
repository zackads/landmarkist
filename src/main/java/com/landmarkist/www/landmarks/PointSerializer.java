package com.landmarkist.www.landmarks;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.locationtech.jts.geom.Point;

public class PointSerializer extends JsonSerializer<Point> {
    @Override
    public void serialize(Point point, JsonGenerator jg, SerializerProvider serializerProvider) throws IOException {
        jg.writeStartObject();
        jg.writeNumberField("lat", point.getCoordinate().x);
        jg.writeNumberField("lng", point.getCoordinate().y);
        jg.writeEndObject();
    }
}
