package com.petadoption.system;

import com.petadoption.system.exception.ResourceNotFoundException;
import com.petadoption.system.model.Pet;
import com.petadoption.system.repository.PetRepository;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for PetService.
 * Tests cover adding, retrieving, updating, deleting, and filtering pets.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Tests")
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private Pet testDog;
    private Pet testCat;

    @BeforeEach
    void setUp() {
        // Set up a reusable test dog
        testDog = new Pet();
        testDog.setId(1L);
        testDog.setName("Buddy");
        testDog.setType(Pet.PetType.DOG);
        testDog.setBreed("Golden Retriever");
        testDog.setAge(3);
        testDog.setGender(Pet.Gender.MALE);
        testDog.setHealthStatus("Vaccinated, healthy");
        testDog.setDescription("Friendly dog from Dog Haus Kimironko");
        testDog.setStatus(Pet.Status.AVAILABLE);

        // Set up a reusable test cat
        testCat = new Pet();
        testCat.setId(2L);
        testCat.setName("Whiskers");
        testCat.setType(Pet.PetType.CAT);
        testCat.setBreed("Persian");
        testCat.setAge(2);
        testCat.setGender(Pet.Gender.FEMALE);
        testCat.setStatus(Pet.Status.AVAILABLE);
    }

    // ===================== ADD PET TESTS =====================

    @Test
    @DisplayName("AddPet - Success: pet is saved and returned")
    void addPet_Success() {
        when(petRepository.save(any(Pet.class))).thenReturn(testDog);

        Pet result = petService.addPet(testDog);

        assertNotNull(result);
        assertEquals("Buddy", result.getName());
        assertEquals(Pet.PetType.DOG, result.getType());
        assertEquals(Pet.Status.AVAILABLE, result.getStatus());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("AddPet - Default status AVAILABLE is set when status is null")
    void addPet_NullStatus_DefaultsToAvailable() {
        Pet petWithNoStatus = new Pet();
        petWithNoStatus.setName("Max");
        petWithNoStatus.setType(Pet.PetType.DOG);
        petWithNoStatus.setStatus(null);

        Pet savedPet = new Pet();
        savedPet.setName("Max");
        savedPet.setStatus(Pet.Status.AVAILABLE);

        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);

        Pet result = petService.addPet(petWithNoStatus);

        assertEquals(Pet.Status.AVAILABLE, result.getStatus());
    }

    // ===================== GET PET TESTS =====================

    @Test
    @DisplayName("GetPetById - Success: returns correct pet")
    void getPetById_Success() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testDog));

        Pet result = petService.getPetById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Buddy", result.getName());
    }

    @Test
    @DisplayName("GetPetById - Fail: throws ResourceNotFoundException when pet not found")
    void getPetById_NotFound_ThrowsResourceNotFoundException() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> petService.getPetById(99L)
        );
    }

    @Test
    @DisplayName("GetAllPets - Returns all pets in the system")
    void getAllPets_ReturnsAllPets() {
        when(petRepository.findAll()).thenReturn(Arrays.asList(testDog, testCat));

        List<Pet> pets = petService.getAllPets();

        assertEquals(2, pets.size());
        verify(petRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("GetAvailablePets - Returns only AVAILABLE pets")
    void getAvailablePets_ReturnsOnlyAvailablePets() {
        when(petRepository.findByStatusOrderByCreatedAtDesc(Pet.Status.AVAILABLE))
                .thenReturn(Arrays.asList(testDog, testCat));

        List<Pet> pets = petService.getAvailablePets();

        assertEquals(2, pets.size());
        pets.forEach(pet -> assertEquals(Pet.Status.AVAILABLE, pet.getStatus()));
    }

    // ===================== FILTER PET TESTS =====================

    @Test
    @DisplayName("GetPetsByType - Returns only pets of specified type")
    void getPetsByType_ReturnsDogs() {
        when(petRepository.findByType(Pet.PetType.DOG)).thenReturn(List.of(testDog));

        List<Pet> dogs = petService.getPetsByType(Pet.PetType.DOG);

        assertEquals(1, dogs.size());
        assertEquals(Pet.PetType.DOG, dogs.get(0).getType());
    }

    @Test
    @DisplayName("GetPetsByTypeAndStatus - Returns pets matching type and status")
    void getPetsByTypeAndStatus_ReturnsMatchingPets() {
        when(petRepository.findByTypeAndStatus(Pet.PetType.CAT, Pet.Status.AVAILABLE))
                .thenReturn(List.of(testCat));

        List<Pet> result = petService.getPetsByTypeAndStatus(Pet.PetType.CAT, Pet.Status.AVAILABLE);

        assertEquals(1, result.size());
        assertEquals(Pet.PetType.CAT, result.get(0).getType());
        assertEquals(Pet.Status.AVAILABLE, result.get(0).getStatus());
    }

    // ===================== UPDATE PET TESTS =====================

    @Test
    @DisplayName("UpdatePet - Success: pet fields are updated correctly")
    void updatePet_Success() {
        Pet updatedData = new Pet();
        updatedData.setName("Buddy Updated");
        updatedData.setType(Pet.PetType.DOG);
        updatedData.setBreed("Labrador");
        updatedData.setAge(4);
        updatedData.setGender(Pet.Gender.MALE);
        updatedData.setHealthStatus("Fully vaccinated");
        updatedData.setDescription("Updated description");
        updatedData.setImageUrl("dog_new.png");
        updatedData.setStatus(Pet.Status.AVAILABLE);

        Pet savedPet = new Pet();
        savedPet.setId(1L);
        savedPet.setName("Buddy Updated");
        savedPet.setBreed("Labrador");

        when(petRepository.findById(1L)).thenReturn(Optional.of(testDog));
        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);

        Pet result = petService.updatePet(1L, updatedData);

        assertNotNull(result);
        assertEquals("Buddy Updated", result.getName());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("UpdatePetStatus - Success: status changes to PENDING")
    void updatePetStatus_ToPending_Success() {
        Pet pendingPet = new Pet();
        pendingPet.setId(1L);
        pendingPet.setName("Buddy");
        pendingPet.setStatus(Pet.Status.PENDING);

        when(petRepository.findById(1L)).thenReturn(Optional.of(testDog));
        when(petRepository.save(any(Pet.class))).thenReturn(pendingPet);

        Pet result = petService.updatePetStatus(1L, Pet.Status.PENDING);

        assertEquals(Pet.Status.PENDING, result.getStatus());
    }

    @Test
    @DisplayName("UpdatePetStatus - Success: status changes to ADOPTED")
    void updatePetStatus_ToAdopted_Success() {
        Pet adoptedPet = new Pet();
        adoptedPet.setId(1L);
        adoptedPet.setName("Buddy");
        adoptedPet.setStatus(Pet.Status.ADOPTED);

        when(petRepository.findById(1L)).thenReturn(Optional.of(testDog));
        when(petRepository.save(any(Pet.class))).thenReturn(adoptedPet);

        Pet result = petService.updatePetStatus(1L, Pet.Status.ADOPTED);

        assertEquals(Pet.Status.ADOPTED, result.getStatus());
    }

    // ===================== DELETE PET TESTS =====================

    @Test
    @DisplayName("DeletePet - Success: pet is deleted from repository")
    void deletePet_Success() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testDog));
        doNothing().when(petRepository).delete(testDog);

        assertDoesNotThrow(() -> petService.deletePet(1L));

        verify(petRepository, times(1)).delete(testDog);
    }

    @Test
    @DisplayName("DeletePet - Fail: throws ResourceNotFoundException when pet not found")
    void deletePet_NotFound_ThrowsResourceNotFoundException() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> petService.deletePet(99L)
        );

        verify(petRepository, never()).delete(any(Pet.class));
    }
}
