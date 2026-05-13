package com.petadoption.system.repository;

import com.petadoption.system.model.AdoptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    // Find all requests by a specific user (for "My Requests" page)
    List<AdoptionRequest> findByUserId(Long userId);

    // Find requests by status (e.g., all PENDING requests for admin)
    List<AdoptionRequest> findByStatus(AdoptionRequest.RequestStatus status);

    // Find requests for a specific pet
    List<AdoptionRequest> findByPetId(Long petId);

    // Find user's requests by status
    List<AdoptionRequest> findByUserIdAndStatus(Long userId, AdoptionRequest.RequestStatus status);

    // Find all requests ordered by date (newest first)
    List<AdoptionRequest> findAllByOrderByRequestDateDesc();
}
