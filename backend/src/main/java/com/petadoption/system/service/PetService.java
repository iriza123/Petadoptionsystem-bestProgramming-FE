package com.petadoption.system.service;

import com.petadoption.system.exception.ResourceNotFoundException;
import com.petadoption.system.model.Pet;
import com.petadoption.system.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    /**
     * Get all pets
     */
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    /**
     * Get all available pets (for homepage)
     */
    public List<Pet> getAvailablePets() {
        return petRepository.findByStatusOrderByCreatedAtDesc(Pet.Status.AVAILABLE);
    }

    /**
     * Get pet by ID
     */
    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", "id", id));
    }

    /**
     * Filter pets by type
     */
    public List<Pet> getPetsByType(Pet.PetType type) {
        return petRepository.findByType(type);
    }

    /**
     * Filter pets by type and status
     */
    public List<Pet> getPetsByTypeAndStatus(Pet.PetType type, Pet.Status status) {
        return petRepository.findByTypeAndStatus(type, status);
    }

    /**
     * Add new pet (Admin only)
     */
    public Pet addPet(Pet pet) {
        // Set default status if not provided
        if (pet.getStatus() == null) {
            pet.setStatus(Pet.Status.AVAILABLE);
        }
        return petRepository.save(pet);
    }

    /**
     * Update pet (Admin only)
     */
    public Pet updatePet(Long id, Pet updatedPet) {
        Pet pet = getPetById(id);

        pet.setName(updatedPet.getName());
        pet.setType(updatedPet.getType());
        pet.setBreed(updatedPet.getBreed());
        pet.setAge(updatedPet.getAge());
        pet.setGender(updatedPet.getGender());
        pet.setHealthStatus(updatedPet.getHealthStatus());
        pet.setDescription(updatedPet.getDescription());
        pet.setImageUrl(updatedPet.getImageUrl());
        pet.setStatus(updatedPet.getStatus());

        return petRepository.save(pet);
    }

    /**
     * Delete pet (Admin only)
     */
    public void deletePet(Long id) {
        Pet pet = getPetById(id);
        petRepository.delete(pet);
    }

    /**
     * Update pet status
     */
    public Pet updatePetStatus(Long id, Pet.Status status) {
        Pet pet = getPetById(id);
        pet.setStatus(status);
        return petRepository.save(pet);
    }
}
