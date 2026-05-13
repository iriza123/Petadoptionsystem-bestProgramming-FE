package com.petadoption.system;

import com.petadoption.system.exception.BadRequestException;
import com.petadoption.system.exception.ResourceNotFoundException;
import com.petadoption.system.model.AdoptionRequest;
import com.petadoption.system.model.Notification;
import com.petadoption.system.model.Pet;
import com.petadoption.system.repository.AdoptionRequestRepository;
import com.petadoption.system.service.AdoptionService;
import com.petadoption.system.service.NotificationService;
import com.petadoption.system.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdoptionService.
 * Tests cover submitting, approving, rejecting, and retrieving adoption requests.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdoptionService Tests")
class AdoptionServiceTest {

    @Mock
    private AdoptionRequestRepository adoptionRequestRepository;

    @Mock
    private PetService petService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AdoptionService adoptionService;

    private Pet availablePet;
    private Pet pendingPet;
    private AdoptionRequest pendingRequest;

    @BeforeEach
    void setUp() {
        // Available pet at Dog Haus
        availablePet = new Pet();
        availablePet.setId(1L);
        availablePet.setName("Buddy");
        availablePet.setType(Pet.PetType.DOG);
        availablePet.setStatus(Pet.Status.AVAILABLE);

        // Pet with pending status
        pendingPet = new Pet();
        pendingPet.setId(1L);
        pendingPet.setName("Buddy");
        pendingPet.setStatus(Pet.Status.PENDING);

        // Pending adoption request
        pendingRequest = new AdoptionRequest();
        pendingRequest.setId(1L);
        pendingRequest.setUserId(2L);
        pendingRequest.setPetId(1L);
        pendingRequest.setReason("I love dogs and have a big yard in Kimironko");
        pendingRequest.setStatus(AdoptionRequest.RequestStatus.PENDING);
    }

    // ===================== SUBMIT REQUEST TESTS =====================

    @Test
    @DisplayName("SubmitRequest - Success: request is created and pet status set to PENDING")
    void submitRequest_Success() {
        when(petService.getPetById(1L)).thenReturn(availablePet);
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenReturn(pendingRequest);
        when(petService.updatePetStatus(eq(1L), eq(Pet.Status.PENDING))).thenReturn(pendingPet);
        when(notificationService.createNotification(anyLong(), anyString(),
                eq(Notification.NotificationType.SYSTEM_MESSAGE))).thenReturn(new Notification());

        AdoptionRequest result = adoptionService.submitRequest(
                2L, 1L, "I love dogs and have a big yard in Kimironko"
        );

        assertNotNull(result);
        assertEquals(AdoptionRequest.RequestStatus.PENDING, result.getStatus());
        verify(petService, times(1)).updatePetStatus(1L, Pet.Status.PENDING);
        verify(notificationService, times(1)).createNotification(
                eq(2L), anyString(), eq(Notification.NotificationType.SYSTEM_MESSAGE)
        );
    }

    @Test
    @DisplayName("SubmitRequest - Fail: throws BadRequestException when pet is not AVAILABLE")
    void submitRequest_PetNotAvailable_ThrowsBadRequestException() {
        Pet adoptedPet = new Pet();
        adoptedPet.setId(1L);
        adoptedPet.setName("Buddy");
        adoptedPet.setStatus(Pet.Status.ADOPTED);

        when(petService.getPetById(1L)).thenReturn(adoptedPet);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> adoptionService.submitRequest(2L, 1L, "I want this pet")
        );

        assertEquals("This pet is not available for adoption", exception.getMessage());
        verify(adoptionRequestRepository, never()).save(any(AdoptionRequest.class));
    }

    @Test
    @DisplayName("SubmitRequest - Fail: throws BadRequestException when pet is PENDING")
    void submitRequest_PetPending_ThrowsBadRequestException() {
        when(petService.getPetById(1L)).thenReturn(pendingPet);

        assertThrows(
                BadRequestException.class,
                () -> adoptionService.submitRequest(2L, 1L, "I want this pet")
        );
    }

    // ===================== APPROVE REQUEST TESTS =====================

    @Test
    @DisplayName("ApproveRequest - Success: request status set to APPROVED and pet to ADOPTED")
    void approveRequest_Success() {
        AdoptionRequest approvedRequest = new AdoptionRequest();
        approvedRequest.setId(1L);
        approvedRequest.setUserId(2L);
        approvedRequest.setPetId(1L);
        approvedRequest.setStatus(AdoptionRequest.RequestStatus.APPROVED);
        approvedRequest.setAdminNotes("Great adopter!");

        when(adoptionRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenReturn(approvedRequest);
        when(petService.getPetById(1L)).thenReturn(availablePet);
        when(petService.updatePetStatus(eq(1L), eq(Pet.Status.ADOPTED))).thenReturn(availablePet);
        when(notificationService.createNotification(anyLong(), anyString(),
                eq(Notification.NotificationType.ADOPTION_APPROVED))).thenReturn(new Notification());

        AdoptionRequest result = adoptionService.approveRequest(1L, "Great adopter!");

        assertNotNull(result);
        assertEquals(AdoptionRequest.RequestStatus.APPROVED, result.getStatus());
        verify(petService, times(1)).updatePetStatus(1L, Pet.Status.ADOPTED);
        verify(notificationService, times(1)).createNotification(
                eq(2L), anyString(), eq(Notification.NotificationType.ADOPTION_APPROVED)
        );
    }

    @Test
    @DisplayName("ApproveRequest - Fail: throws BadRequestException when request already processed")
    void approveRequest_AlreadyProcessed_ThrowsBadRequestException() {
        AdoptionRequest alreadyApproved = new AdoptionRequest();
        alreadyApproved.setId(1L);
        alreadyApproved.setStatus(AdoptionRequest.RequestStatus.APPROVED);

        when(adoptionRequestRepository.findById(1L)).thenReturn(Optional.of(alreadyApproved));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> adoptionService.approveRequest(1L, "notes")
        );

        assertEquals("This request has already been processed", exception.getMessage());
    }

    @Test
    @DisplayName("ApproveRequest - Fail: throws ResourceNotFoundException when request not found")
    void approveRequest_NotFound_ThrowsResourceNotFoundException() {
        when(adoptionRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adoptionService.approveRequest(99L, "notes")
        );
    }

    // ===================== REJECT REQUEST TESTS =====================

    @Test
    @DisplayName("RejectRequest - Success: request status set to REJECTED and pet back to AVAILABLE")
    void rejectRequest_Success() {
        AdoptionRequest rejectedRequest = new AdoptionRequest();
        rejectedRequest.setId(1L);
        rejectedRequest.setUserId(2L);
        rejectedRequest.setPetId(1L);
        rejectedRequest.setStatus(AdoptionRequest.RequestStatus.REJECTED);
        rejectedRequest.setAdminNotes("Insufficient experience");

        when(adoptionRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenReturn(rejectedRequest);
        when(petService.getPetById(1L)).thenReturn(availablePet);
        when(petService.updatePetStatus(eq(1L), eq(Pet.Status.AVAILABLE))).thenReturn(availablePet);
        when(notificationService.createNotification(anyLong(), anyString(),
                eq(Notification.NotificationType.ADOPTION_REJECTED))).thenReturn(new Notification());

        AdoptionRequest result = adoptionService.rejectRequest(1L, "Insufficient experience");

        assertNotNull(result);
        assertEquals(AdoptionRequest.RequestStatus.REJECTED, result.getStatus());
        verify(petService, times(1)).updatePetStatus(1L, Pet.Status.AVAILABLE);
        verify(notificationService, times(1)).createNotification(
                eq(2L), anyString(), eq(Notification.NotificationType.ADOPTION_REJECTED)
        );
    }

    @Test
    @DisplayName("RejectRequest - Fail: throws BadRequestException when request already processed")
    void rejectRequest_AlreadyProcessed_ThrowsBadRequestException() {
        AdoptionRequest alreadyRejected = new AdoptionRequest();
        alreadyRejected.setId(1L);
        alreadyRejected.setStatus(AdoptionRequest.RequestStatus.REJECTED);

        when(adoptionRequestRepository.findById(1L)).thenReturn(Optional.of(alreadyRejected));

        assertThrows(
                BadRequestException.class,
                () -> adoptionService.rejectRequest(1L, "notes")
        );
    }

    // ===================== GET REQUESTS TESTS =====================

    @Test
    @DisplayName("GetUserRequests - Returns all requests for a specific user")
    void getUserRequests_ReturnsUserRequests() {
        when(adoptionRequestRepository.findByUserId(2L)).thenReturn(List.of(pendingRequest));

        List<AdoptionRequest> requests = adoptionService.getUserRequests(2L);

        assertEquals(1, requests.size());
        assertEquals(2L, requests.get(0).getUserId());
    }

    @Test
    @DisplayName("GetPendingRequests - Returns only PENDING requests")
    void getPendingRequests_ReturnsPendingOnly() {
        when(adoptionRequestRepository.findByStatus(AdoptionRequest.RequestStatus.PENDING))
                .thenReturn(List.of(pendingRequest));

        List<AdoptionRequest> requests = adoptionService.getPendingRequests();

        assertEquals(1, requests.size());
        assertEquals(AdoptionRequest.RequestStatus.PENDING, requests.get(0).getStatus());
    }

    @Test
    @DisplayName("GetAllRequests - Returns all requests ordered by date")
    void getAllRequests_ReturnsAllRequests() {
        AdoptionRequest request2 = new AdoptionRequest();
        request2.setId(2L);
        request2.setStatus(AdoptionRequest.RequestStatus.APPROVED);

        when(adoptionRequestRepository.findAllByOrderByRequestDateDesc())
                .thenReturn(Arrays.asList(request2, pendingRequest));

        List<AdoptionRequest> requests = adoptionService.getAllRequests();

        assertEquals(2, requests.size());
    }
}
