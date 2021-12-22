package com.landmarkist.www.landmarks;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

@RepositoryRestResource
public interface ListedBuildingRestRepository extends PagingAndSortingRepository<ListedBuilding, UUID> {

    @Override
    @RestResource(exported = false)
    void deleteById(UUID uuid);

    @Override
    @RestResource(exported = false)
    void delete(ListedBuilding entity);

    @Override
    @RestResource(exported = false)
    void deleteAllById(Iterable<? extends UUID> uuids);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends ListedBuilding> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();

    @Override
    @RestResource(exported = false)
    <S extends ListedBuilding> S save(S entity);

    @Override
    @RestResource(exported = false)
    <S extends ListedBuilding> Iterable<S> saveAll(Iterable<S> entities);
}
