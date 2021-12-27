package com.landmarkist.www.listedBuilding;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.net.MalformedURLException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:tc:postgis:13-3.1-alpine:///landmarkist", // Use a Testcontainers database
                "spring.flyway.locations=classpath:/db/migration", // Ony run schema migrations; don't load reference data
        }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebTestClient
@Slf4j
public final class ListedBuildingAcceptanceTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ListedBuildingRepository listedBuildingRepository;

    @Test
    void givenListedBuildings__whenUserRequests__thenSendGeoJSON() throws MalformedURLException {
        listedBuildingRepository.save(
                ListedBuilding
                        .builder()
                        .name("Grade 1 Guardhouse")
                        .grade("I")
                        .location(new GeometryFactory().createPoint(new Coordinate(0.25, 0.75)))
                        .locationName("Testershire")
                        .listEntry("1")
                        .hyperlink(new URL("https://historicengland.org.uk/1"))
                        .build()
        );
        String polygonQuery = "polygon=0,0,0,1,1,1,1,0,0,0";

        WebTestClient.BodyContentSpec response = this.webTestClient
                .get()
                .uri("/api/listedBuildings/search/findAllInPolygon?" + polygonQuery)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();

        response.jsonPath("$.features", hasSize(1));
        response.jsonPath("$.features[0].geometry[0]", is(0.25));
        response.jsonPath("$.features[0].geometry[1]", is(0.75));
    }
}
