package com.landmarkist.www.landmarks;

import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.pool.TypePool;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class ListedBuildingController {
    ListedBuildingRepository repository;

    @Autowired
    public ListedBuildingController(ListedBuildingRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "listedBuildings/search/findAllInPolygon")
    @ResponseBody
    public List<ListedBuilding> findListedBuildingsInPolygon(@RequestParam("polygon") String polygon, @RequestParam(value = "size", defaultValue = "100") int size) {
        System.out.println("Looking for buildings in polygon " + polygon);

        try {
            return repository.findAllInPolygon(createPolygonFromQueryString(polygon));
        } catch(IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There was a problem with the polygon.  Try again.");
        }
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
