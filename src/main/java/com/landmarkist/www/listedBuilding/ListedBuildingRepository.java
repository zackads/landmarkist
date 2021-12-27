package com.landmarkist.www.listedBuilding;

import java.util.List;
import java.util.UUID;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ListedBuildingRepository
        extends JpaRepository<ListedBuilding, UUID> {
    @Query(
            value = "SELECT * FROM listed_building WHERE st_covers(geography(:polygon), listed_building.location);",
            nativeQuery = true
    )
    List<ListedBuilding> findAllInPolygon(@Param("polygon") Polygon polygon);
}
