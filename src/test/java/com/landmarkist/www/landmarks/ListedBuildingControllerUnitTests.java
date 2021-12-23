package com.landmarkist.www.landmarks;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ListedBuildingController.class)
public class ListedBuildingControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListedBuildingRepository mockRepository;

    @Test
    public void canSearchByPolygon() throws Exception {
        Mockito.when(mockRepository
                        .findAllInPolygon(any(Polygon.class)))
                .thenReturn(List.of(ListedBuilding.builder().name("Testington Towers").build()));

        String polygonQuery = "40.078811,-76.730422,41.078811,-74.730422,40.078811,-74.730422,39.961879,-76.730422,39.961879,-76.730422,40.078811,-76.730422";

        this.mockMvc.perform(
                        get("/api/listedBuildings/search/findAllInPolygon?polygon=" + polygonQuery)).andExpect(status().isOk());

        verify(mockRepository).findAllInPolygon(any(Polygon.class));
    }

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

    @Configuration
    @Import(ListedBuildingController.class)
    static class Config {
    }
}
