package com.landmarkist.www.landmarks;

import java.util.UUID;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListedBuildingRepository extends PagingAndSortingRepository<ListedBuilding, UUID> {
    @Query(value = "SELECT * FROM listed_building", nativeQuery = true)
    Page<ListedBuilding> findAllInPolygon(Polygon polygon, Pageable pageable);
}
