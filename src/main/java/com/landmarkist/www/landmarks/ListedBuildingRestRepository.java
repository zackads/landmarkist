package com.landmarkist.www.landmarks;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ListedBuildingRestRepository extends PagingAndSortingRepository<ListedBuilding, UUID> {

}
