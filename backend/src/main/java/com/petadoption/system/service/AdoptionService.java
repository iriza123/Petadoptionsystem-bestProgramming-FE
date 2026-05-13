package com.petadoption.system.service;

import com.petadoption.system.exception.BadRequestException;
import com.petadoption.system.exception.ResourceNotFoundException;
import com.petadoption.system.model.AdoptionRequest;
import com.petadoption.system.model.Notification;
import com.petadoption.system.model.Pet;
import com.petadoption.system.repository.AdoptionRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionService {

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final PetService petService;
    private final NotificationService notificationService;

    /**
     * Submit an adoption request
     */
    public AdoptionRequest submitRequest(Long userId, Long petId, String reason) {
        // Check if pet exists and is available
        Pet pet = petService.getPetById(petId);

        if (pet.getStatus() != Pet.Status.AVAILABLE) {
            throw new BadRequestException("This pet is not available for adoption");
        }

        // Create adoption request
        AdoptionRequest request = new AdoptionRequest();
        request.setUserId(userId);
        request.setPetId(petId);
        request.setReason(reason);
        request.setStatus(AdoptionRequest.RequestStatus.PENDING);

        AdoptionRequest savedRequest = adoptionRequestRepository.save(request);

        // Update pet status to PENDING
        petService.updatePetStatus(petId, Pet.Status.PENDING);

        // Create notification for user
        notificationService.createNotification(
                userId,
                "Your adoption request for " + pet.getName() + " has been submitted successfully!",
                Notification.NotificationType.SYSTEM_MESSAGE
        );

        return savedRequest;
    }

    /**
     * Get all requests by a user
     */
    public List<AdoptionRequest> getUserRequests(Long userId) {
        return adoptionRequestRepository.findByUserId(userId);
    }

    /**
     * Get all pending requests (for admin)
     */
    public List<AdoptionRequest> getPendingRequests() {
        return adoptionRequestRepository.findByStatus(AdoptionRequest.RequestStatus.PENDING);
    }

    /**
     * Get all requests (for admin)
     */
    public List<AdoptionRequest> getAllRequests() {
        return adoptionRequestRepository.findAllByOrderByRequestDateDesc();
    }

    /**
     * Get request by ID
     */
    public AdoptionRequest getRequestById(Long id) {
        return adoptionRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption Request", "id", id));
    }

    /**
     * Approve adoption request
     */
    @Transactional
    public AdoptionRequest approveRequest(Long requestId, String adminNotes) {
        AdoptionRequest request = getRequestById(requestId);

        if (request.getStatus() != AdoptionRequest.RequestStatus.PENDING) {
            throw new BadRequestException("This request has already been processed");
        }

        // Update request status
        request.setStatus(AdoptionRequest.RequestStatus.APPROVED);
        request.setResponseDate(LocalDateTime.now());
        request.setAdminNotes(adminNotes);

        AdoptionRequest updatedRequest = adoptionRequestRepository.save(request);

        // Update pet status to ADOPTED
        Pet pet = petService.getPetById(request.getPetId());
        petService.updatePetStatus(request.getPetId(), Pet.Status.ADOPTED);

        // Create notification for user
        notificationService.createNotification(
                request.getUserId(),
                "Congratulations! Your adoption request for " + pet.getName() + " has been approved!",
                Notification.NotificationType.ADOPTION_APPROVED
        );

        return updatedRequest;
    }

    /**
     * Reject adoption request
     */
    @Transactional
    public AdoptionRequest rejectRequest(Long requestId, String adminNotes) {
        AdoptionRequest request = getRequestById(requestId);

        if (request.getStatus() != AdoptionRequest.RequestStatus.PENDING) {
            throw new BadRequestException("This request has already been processed");
        }

        // Update request status
        request.setStatus(AdoptionRequest.RequestStatus.REJECTED);
        request.setResponseDate(LocalDateTime.now());
        request.setAdminNotes(adminNotes);

        AdoptionRequest updatedRequest = adoptionRequestRepository.save(request);

        // Update pet status back to AVAILABLE
        Pet pet = petService.getPetById(request.getPetId());
        petService.updatePetStatus(request.getPetId(), Pet.Status.AVAILABLE);

        // Create notification for user
        notificationService.createNotification(
                request.getUserId(),
                "Unfortunately, your adoption request for " + pet.getName() + " has been rejected.",
                Notification.NotificationType.ADOPTION_REJECTED
        );

        return updatedRequest;
    }
}
