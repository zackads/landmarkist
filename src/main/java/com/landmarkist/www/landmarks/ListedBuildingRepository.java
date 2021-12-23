package com.landmarkist.www.landmarks;

import java.util.List;
import java.util.UUID;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListedBuildingRepository extends CrudRepository<ListedBuilding, UUID> {
    @Query(value = "SELECT * FROM listed_building", nativeQuery = true)
    List<ListedBuilding> findAllInPolygon(Polygon polygon);
}
