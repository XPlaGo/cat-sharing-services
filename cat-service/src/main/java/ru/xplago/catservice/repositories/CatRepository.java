package ru.xplago.catservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.xplago.catservice.entities.Cat;
import ru.xplago.catservice.entities.Owner;

import java.util.List;
import java.util.Optional;

public interface CatRepository extends JpaRepository<Cat, Long> {
    Optional<Cat> findCatByIdAndOwner(Long id, Owner owner);

    List<Cat> findCatsByOwnerId(Long ownerId);

    @Query("SELECT c.id FROM cat_cat c WHERE c.owner = :owner")
    List<Long> findCatsIdsByOwner(@Param("owner") Owner ownerId);

    @Query("SELECT c.id FROM cat_cat c WHERE c.tenant = :tenant")
    List<Long> findRentedCatsIdsByTenant(@Param("tenant") Owner tenant);
}
