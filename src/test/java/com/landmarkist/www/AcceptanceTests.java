package com.landmarkist.www;

import com.landmarkist.www.landmarks.ListedBuilding;
import com.landmarkist.www.landmarks.ListedBuildingRepository;
import io.github.wimdeblauwe.testcontainers.cypress.CypressContainer;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTest;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTestResults;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTestSuite;
import io.github.wimdeblauwe.testcontainers.cypress.MochawesomeGatherTestResultsStrategy;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.Testcontainers;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:tc:postgis:13-3.1-alpine:///landmarkist", // Use a Testcontainers database
                "spring.flyway.locations=classpath:/db/migration", // Ony run schema migrations; don't load reference data
        }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public final class AcceptanceTests {

    @Autowired
    private ListedBuildingRepository listedBuildingRepository;

    @LocalServerPort
    private int port;

    @TestFactory
    List<DynamicContainer> runCypressTests()
            throws InterruptedException, IOException, TimeoutException {
        seedTestDatabase();

        Testcontainers.exposeHostPorts(port);

        MochawesomeGatherTestResultsStrategy gradleTestResultStrategy = new MochawesomeGatherTestResultsStrategy(
                FileSystems
                        .getDefault()
                        .getPath(
                                "build",
                                "resources",
                                "test",
                                "e2e",
                                "cypress",
                                "reports",
                                "mochawesome"
                        )
        );

        try (
                CypressContainer container = new CypressContainer()
                        .withLocalServerPort(port)
                        .withGatherTestResultsStrategy(gradleTestResultStrategy)
        ) {
            container.start();
            CypressTestResults testResults = container.getTestResults();

            return convertToJUnitDynamicTests(testResults); // (2)
        }
    }

    @NotNull
    private List<DynamicContainer> convertToJUnitDynamicTests(
            CypressTestResults testResults
    ) {
        List<DynamicContainer> dynamicContainers = new ArrayList<>();
        List<CypressTestSuite> suites = testResults.getSuites();
        for (CypressTestSuite suite : suites) {
            createContainerFromSuite(dynamicContainers, suite);
        }
        return dynamicContainers;
    }

    private void createContainerFromSuite(
            List<DynamicContainer> dynamicContainers,
            CypressTestSuite suite
    ) {
        List<DynamicTest> dynamicTests = new ArrayList<>();
        for (CypressTest test : suite.getTests()) {
            dynamicTests.add(
                    DynamicTest.dynamicTest(
                            test.getDescription(),
                            () -> {
                                if (!test.isSuccess()) {
                                    log.error(test.getErrorMessage());
                                    log.error(test.getStackTrace());
                                }
                                Assertions.assertTrue(test.isSuccess());
                            }
                    )
            );
        }
        dynamicContainers.add(
                DynamicContainer.dynamicContainer(suite.getTitle(), dynamicTests)
        );
    }

    private void seedTestDatabase() throws MalformedURLException {
        listedBuildingRepository.save(
                ListedBuilding
                        .builder()
                        .name("Grade 1 Guardhouse")
                        .grade("I")
                        .location(new GeometryFactory().createPoint(new Coordinate(0.5, 0.5)))
                        .locationName("Testershire")
                        .listEntry("1")
                        .hyperlink(new URL("https://historicengland.org.uk/1"))
                        .build()
        );

        listedBuildingRepository.save(
                ListedBuilding
                        .builder()
                        .name("Grade II* Towers")
                        .grade("II*")
                        .location(new GeometryFactory().createPoint(new Coordinate(0.5, 0.5)))
                        .locationName("Testershire")
                        .listEntry("2")
                        .hyperlink(new URL("https://historicengland.org.uk/2"))
                        .build()
        );

        listedBuildingRepository.save(
                ListedBuilding
                        .builder()
                        .name("Grade II Toilet")
                        .grade("II")
                        .location(new GeometryFactory().createPoint(new Coordinate(0.5, 0.5)))
                        .locationName("Testershire")
                        .listEntry("3")
                        .hyperlink(new URL("https://historicengland.org.uk/3"))
                        .build()
        );
    }
}
