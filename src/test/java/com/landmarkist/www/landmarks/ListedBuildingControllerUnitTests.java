package com.landmarkist.www.landmarks;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ListedBuildingControllerUnitTests {
    @Test
    public void givenAStringOfPointsReturnsAPolygon() {
        String query = "40.078811,-76.730422,41.078811,-74.730422,40.078811,-74.730422,39.961879,-76.730422,39.961879,-76.730422,40.078811,-76.730422";
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(40.078811, -76.730422),
                new Coordinate(41.078811, -74.730422),
                new Coordinate(40.078811, -74.730422),
                new Coordinate(39.961879, -76.730422),
                new Coordinate(39.961879, -76.730422),
                new Coordinate(40.078811, -76.730422)};
        Polygon expectedPolygon = new GeometryFactory().createPolygon(coordinates);

        Polygon polygon = ListedBuildingController.createPolygonFromQueryString(query);

        assertEquals(expectedPolygon, polygon);
    }

    @Test
    public void givenAnotherStringOfPointsReturnsAPolygon() {
        String query = "-72.2819874,42.9278490,-72.2818050,42.9280258,-72.2825668,42.9275387,-72.2806678,42.9272441,-72.2819874,42.9278490";
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(-72.2819874, 42.9278490),
                new Coordinate(-72.2818050, 42.9280258),
                new Coordinate(-72.2825668, 42.9275387),
                new Coordinate(-72.2806678, 42.9272441),
                new Coordinate(-72.2819874, 42.9278490),
        };
        Polygon expectedPolygon = new GeometryFactory().createPolygon(coordinates);

        Polygon polygon = ListedBuildingController.createPolygonFromQueryString(query);

        assertEquals(expectedPolygon, polygon);
    }

    @Test
    public void givenAPolygonThatDoesNotClose__ThrowsAnException() {
        String query = "-72.2819874,42.9278490,-72.2818050,42.9280258,-72.2825668,42.9275387,-72.2806678,42.9272441";

        assertThrows(IllegalArgumentException.class, () -> ListedBuildingController.createPolygonFromQueryString(query));
    }
}
