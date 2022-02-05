package com.landmarkist.api.listedBuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.jts2geojson.GeoJSONWriter;

@RestController
@Validated
@RequestMapping("/api")
public class ListedBuildingController {

    ListedBuildingRepository repository;

    @Autowired
    public ListedBuildingController(ListedBuildingRepository repository) {
        this.repository = repository;
    }

    public static Polygon createPolygonFromQueryString(String query) {
        String[] array = query.split(",");

        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < array.length; i += 2) {
            coordinates.add(new Coordinate(Double.parseDouble(array[i]), Double.parseDouble(array[i + 1])));
        }

        return new GeometryFactory().createPolygon(coordinates.toArray(new Coordinate[] {}));
    }

    @GetMapping(value = "listedBuildings/search/findAllInPolygon")
    @ResponseBody
    public ResponseEntity<FeatureCollection> findListedBuildingsInPolygon2(@RequestParam("polygon") String polygon) {
        try {
            List<ListedBuilding> listedBuildings = repository.findAllInPolygon(createPolygonFromQueryString(polygon));

            return new ResponseEntity<>(createFeatureCollection(listedBuildings), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There was a problem with the polygon.  Try again.");
        }
    }

    private FeatureCollection createFeatureCollection(List<ListedBuilding> listedBuildings) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter();

        Feature[] features = listedBuildings.stream().map(listedBuilding -> {
            Map<String, Object> properties = new HashMap<>();
            Geometry geo = listedBuilding.getLocation();

            return new Feature(geoJSONWriter.write(geo), properties);
        }).toArray(Feature[]::new);

        return new FeatureCollection(features);
    }
}
