package com.landmarkist.api.listedBuilding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URL;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ListedBuildingController.class)
public class ListedBuildingControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListedBuildingRepository mockRepository;

    @Test
    public void canSearchByPolygon() throws Exception {
        Mockito.when(mockRepository.findAllInPolygon(any(Polygon.class)))
                .thenReturn(List.of(ListedBuilding.builder()
                        .name("Grade 1 Guardhouse")
                        .grade("I")
                        .location(new GeometryFactory().createPoint(new Coordinate(0.25, 0.75)))
                        .locationName("Testershire").listEntry("1")
                        .hyperlink(new URL("https://historicengland.org.uk/1"))
                        .build()));

        String validPolygon = "40.078811,-76.730422,41.078811,-74.730422,40.078811,-74.730422,39.961879,-76.730422,39.961879,-76.730422,40.078811,-76.730422";

        this.mockMvc.perform(get("/api/listedBuildings/search/findAllInPolygon?polygon=" + validPolygon))
                .andExpect(status().isOk());

        verify(mockRepository).findAllInPolygon(any(Polygon.class));
    }

    @Test
    public void sendsRequestErrorWhenPolygonIsInvalid() throws Exception {
        String unclosedPolygon = "40.078811,-76.730422,41.078811,-74.730422,40.078811,-74.730422";

        this.mockMvc.perform(get("/api/listedBuildings/search/findAllInPolygon?polygon=" + unclosedPolygon))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenAListedBuildingIsFound_thenIncludeInformationAboutTheListing() throws Exception {
        ListedBuilding listedBuilding = ListedBuilding.builder()
                .name("Grade 1 Guardhouse")
                .grade("I")
                .location(new GeometryFactory().createPoint(new Coordinate(0.25, 0.75)))
                .locationName("Testershire")
                .listEntry("42")
                .hyperlink(new URL("https://historicengland.org.uk/42"))
                .build();
        Mockito.when(mockRepository.findAllInPolygon(any(Polygon.class)))
                .thenReturn(List.of(listedBuilding));

        String validPolygon = "40.078811,-76.730422,41.078811,-74.730422,40.078811,-74.730422,39.961879,-76.730422,39.961879,-76.730422,40.078811,-76.730422";

        var response = this.mockMvc
                .perform(get("/api/listedBuildings/search/findAllInPolygon?polygon=" + validPolygon))
                // type
                .andExpect(jsonPath("$.features[0].type").value("Feature"))
                // geometry
                .andExpect(jsonPath("$.features[0].geometry.type").value("Point"))
                .andExpect(jsonPath("$.features[0].geometry.coordinates[0]").value(0.25))
                .andExpect(jsonPath("$.features[0].geometry.coordinates[1]").value(0.75))
                // properties
                .andExpect(jsonPath("$.features[0].properties.hyperlink").value("https://historicengland.org.uk/42"))
                .andExpect(jsonPath("$.features[0].properties.locationName").value("Testershire"))
                .andExpect(jsonPath("$.features[0].properties.grade").value("I"))
                .andExpect(jsonPath("$.features[0].properties.name").value("Grade 1 Guardhouse"))
                .andExpect(jsonPath("$.features[0].properties.id").value(listedBuilding.getId()))
                .andExpect(jsonPath("$.features[0].properties.listEntry").value("42"));
    }

    @Test
    public void givenAStringOfPointsReturnsAPolygon() {
        String query = "40.078811,-76.730422,41.078811,-74.730422,40.078811,-74.730422,39.961879,-76.730422,39.961879,-76.730422,40.078811,-76.730422";
        Coordinate[] coordinates = new Coordinate[] {new Coordinate(40.078811, -76.730422),
                new Coordinate(41.078811, -74.730422), new Coordinate(40.078811, -74.730422),
                new Coordinate(39.961879, -76.730422), new Coordinate(39.961879, -76.730422),
                new Coordinate(40.078811, -76.730422),};
        Polygon expectedPolygon = new GeometryFactory().createPolygon(coordinates);

        Polygon polygon = ListedBuildingController.createPolygonFromQueryString(query);

        assertEquals(expectedPolygon, polygon);
    }

    @Test
    public void givenAnotherStringOfPointsReturnsAPolygon() {
        String query = "-72.2819874,42.9278490,-72.2818050,42.9280258,-72.2825668,42.9275387,-72.2806678,42.9272441,-72.2819874,42.9278490";
        Coordinate[] coordinates = new Coordinate[] {new Coordinate(-72.2819874, 42.9278490),
                new Coordinate(-72.2818050, 42.9280258), new Coordinate(-72.2825668, 42.9275387),
                new Coordinate(-72.2806678, 42.9272441), new Coordinate(-72.2819874, 42.9278490),};
        Polygon expectedPolygon = new GeometryFactory().createPolygon(coordinates);

        Polygon polygon = ListedBuildingController.createPolygonFromQueryString(query);

        assertEquals(expectedPolygon, polygon);
    }

    @Test
    public void givenAPolygonThatDoesNotClose__ThrowsAnException() {
        String query = "-72.2819874,42.9278490,-72.2818050,42.9280258,-72.2825668,42.9275387,-72.2806678,42.9272441";

        assertThrows(IllegalArgumentException.class,
                () -> ListedBuildingController.createPolygonFromQueryString(query));
    }

    @Configuration
    @Import(ListedBuildingController.class)
    static class Config {
    }
}
