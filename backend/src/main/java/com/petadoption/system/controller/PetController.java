package com.petadoption.system.controller;

import com.petadoption.system.dto.ApiResponse;
import com.petadoption.system.model.Pet;
import com.petadoption.system.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PetController {

    private final PetService petService;

    /**
     * Get all available pets
     * GET /api/pets
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Pet>>> getAllPets() {
        List<Pet> pets = petService.getAvailablePets();
        return ResponseEntity.ok(ApiResponse.success("Pets retrieved successfully", pets));
    }

    /**
     * Get pet by ID
     * GET /api/pets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pet>> getPetById(@PathVariable Long id) {
        Pet pet = petService.getPetById(id);
        return ResponseEntity.ok(ApiResponse.success("Pet retrieved successfully", pet));
    }

    /**
     * Filter pets by type
     * GET /api/pets/filter?type=DOG
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Pet>>> filterPets(
            @RequestParam(required = false) Pet.PetType type,
            @RequestParam(required = false) Pet.Status status
    ) {
        List<Pet> pets;

        if (type != null && status != null) {
            pets = petService.getPetsByTypeAndStatus(type, status);
        } else if (type != null) {
            pets = petService.getPetsByType(type);
        } else {
            pets = petService.getAllPets();
        }

        return ResponseEntity.ok(ApiResponse.success("Pets filtered successfully", pets));
    }

    /**
     * Add new pet (Admin)
     * POST /api/pets
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Pet>> addPet(@Valid @RequestBody Pet pet) {
        Pet savedPet = petService.addPet(pet);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pet added successfully", savedPet));
    }

    /**
     * Update pet (Admin)
     * PUT /api/pets/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Pet>> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody Pet pet
    ) {
        Pet updatedPet = petService.updatePet(id, pet);
        return ResponseEntity.ok(ApiResponse.success("Pet updated successfully", updatedPet));
    }

    /**
     * Delete pet (Admin)
     * DELETE /api/pets/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.ok(ApiResponse.success("Pet deleted successfully"));
    }
}
