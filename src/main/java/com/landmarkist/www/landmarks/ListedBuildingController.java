package com.landmarkist.www.landmarks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class ListedBuildingController {
    ListedBuildingRestRepository repository;

    @Autowired
    public ListedBuildingController(ListedBuildingRestRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/listedBuildings/search/findAllInPolygon")
    @ResponseBody
    public ResponseEntity<?> findListedBuildingsInPolygon(@RequestParam(name = "polygon") String polygon) {
        Iterable<ListedBuilding> listedBuildings = repository.findAll();
        System.out.println("Looking for buildings in polygon " + polygon);

        CollectionModel<ListedBuilding> resources = CollectionModel.of(listedBuildings);

        return ResponseEntity.ok(resources);
    }

    public static Polygon createPolygonFromQueryString(String query) {
        String[] array = query.split(",");

        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < array.length; i += 2) {
            coordinates.add(new Coordinate(Double.parseDouble(array[i]), Double.parseDouble(array[i + 1])));
        }

        return new GeometryFactory().createPolygon(coordinates.toArray(new Coordinate[]{}));
    }
}
