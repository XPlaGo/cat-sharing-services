package ru.xplago.catservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xplago.catservice.entities.Owner;

import java.util.Optional;

public interface
OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
}
