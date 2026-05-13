package com.petadoption.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pet name is required")
    @Size(min = 2, max = 100, message = "Pet name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Pet type is required")
    @Column(nullable = false)
    private PetType type;

    @Size(max = 100, message = "Breed must be at most 100 characters")
    private String breed;

    @Min(value = 0, message = "Age must be a positive number")
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(max = 255, message = "Health status must be at most 255 characters")
    @Column(name = "health_status")
    private String healthStatus;

    @Column(length = 1000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.AVAILABLE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum PetType {
        DOG,
        CAT,
        BIRD,
        RABBIT,
        HAMSTER,
        FISH,
        OTHER
    }

    public enum Gender {
        MALE,
        FEMALE,
        UNKNOWN
    }

    public enum Status {
        AVAILABLE,
        PENDING,
        ADOPTED
    }
}
