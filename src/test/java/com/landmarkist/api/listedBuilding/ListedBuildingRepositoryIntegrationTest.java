package com.landmarkist.api.listedBuilding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ListedBuildingRepositoryIntegrationTest {

    private static final DockerImageName postgisImage = DockerImageName.parse("postgis/postgis:13-3.1-alpine")
            .asCompatibleSubstituteFor("postgres");
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(postgisImage)
            .withDatabaseName("landmarkist").withUsername("user").withPassword("password");
    @Autowired
    private ListedBuildingRepository listedBuildingRepository;

    @Test
    void givenAPolygon__whenListedBuildingsAreInThePolygon__thenReturnFoundListedBuildings()
            throws MalformedURLException {
        Coordinate[] coordinates = new Coordinate[] {new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(1, 1),
                new Coordinate(1, 0), new Coordinate(0, 0),};

        Polygon polygon = new GeometryFactory().createPolygon(coordinates);

        ListedBuilding listedBuilding = ListedBuilding.builder().name("Testington Towers").grade("I")
                .location(new GeometryFactory().createPoint(new Coordinate(0.5, 0.5))).locationName("Centroidville")
                .listEntry("1").hyperlink(new URL("https://en.wikipedia.org/wiki/Centroid")).build();
        listedBuildingRepository.save(listedBuilding);

        List<ListedBuilding> listedBuildings = listedBuildingRepository.findAllInPolygon(polygon);

        assertEquals(1, listedBuildings.size());
    }

    @Test
    void givenAPolygon__whenListedBuildingsAreNotInThePolygon__thenReturnEmptyList() {
        Coordinate[] coordinates = new Coordinate[] {new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(1, 1),
                new Coordinate(1, 0), new Coordinate(0, 0),};

        Polygon polygon = new GeometryFactory().createPolygon(coordinates);

        List<ListedBuilding> listedBuildings = listedBuildingRepository.findAllInPolygon(polygon);

        assertTrue(listedBuildings.isEmpty());
    }
}
