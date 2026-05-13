package com.petadoption.system.repository;

import com.petadoption.system.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    // Find pets by status (e.g., all AVAILABLE pets)
    List<Pet> findByStatus(Pet.Status status);

    // Find pets by type (e.g., all DOGs)
    List<Pet> findByType(Pet.PetType type);

    // Find pets by type AND status (e.g., AVAILABLE DOGs)
    List<Pet> findByTypeAndStatus(Pet.PetType type, Pet.Status status);

    // Find pets by status ordered by creation date (newest first)
    List<Pet> findByStatusOrderByCreatedAtDesc(Pet.Status status);
}
