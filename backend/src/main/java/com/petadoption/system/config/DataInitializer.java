package com.petadoption.system.config;

import com.petadoption.system.model.Pet;
import com.petadoption.system.model.User;
import com.petadoption.system.repository.PetRepository;
import com.petadoption.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PetRepository petRepository;

    @Override
    public void run(String... args) {
        // Create default admin user if it doesn't exist
        if (!userRepository.existsByEmail("admin@shelter.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@shelter.com");
            admin.setPassword("admin123");
            admin.setRole(User.Role.ADMIN);
            admin.setPhone("1234567890");
            admin.setAddress("Animal Shelter HQ");

            userRepository.save(admin);
            System.out.println("✅ Default admin user created!");
            System.out.println("   Email: admin@shelter.com");
            System.out.println("   Password: admin123");
        }

        // Create default pets if database is empty
        if (petRepository.count() == 0) {
            createDefaultPets();
            System.out.println("✅ Default pets created!");
        }
    }

    private void createDefaultPets() {
        // 1. Dog
        Pet dog1 = new Pet();
        dog1.setName("Buddy");
        dog1.setType(Pet.PetType.DOG);
        dog1.setBreed("Golden Retriever");
        dog1.setAge(3);
        dog1.setGender(Pet.Gender.MALE);
        dog1.setHealthStatus("Vaccinated, neutered, healthy");
        dog1.setDescription("Buddy is a friendly and playful Golden Retriever who loves kids and outdoor activities. He's well-trained and perfect for an active family.");
        dog1.setImageUrl("dog1.png");
        dog1.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(dog1);

        // 2. Cat
        Pet cat1 = new Pet();
        cat1.setName("Whiskers");
        cat1.setType(Pet.PetType.CAT);
        cat1.setBreed("Persian");
        cat1.setAge(2);
        cat1.setGender(Pet.Gender.FEMALE);
        cat1.setHealthStatus("Vaccinated, spayed, healthy");
        cat1.setDescription("Whiskers is a calm and affectionate Persian cat. She loves cuddles and is perfect for a quiet home.");
        cat1.setImageUrl("cat1.png");
        cat1.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(cat1);

        // 3. Bird
        Pet bird1 = new Pet();
        bird1.setName("Tweety");
        bird1.setType(Pet.PetType.BIRD);
        bird1.setBreed("Parrot");
        bird1.setAge(2);
        bird1.setGender(Pet.Gender.FEMALE);
        bird1.setHealthStatus("Healthy, active");
        bird1.setDescription("Tweety is a colorful and talkative parrot. She can learn words and loves to socialize.");
        bird1.setImageUrl("parrot1.png");
        bird1.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(bird1);

        // 4. Fish
        Pet fish1 = new Pet();
        fish1.setName("Bubbles");
        fish1.setType(Pet.PetType.FISH);
        fish1.setBreed("Goldfish");
        fish1.setAge(1);
        fish1.setGender(Pet.Gender.UNKNOWN);
        fish1.setHealthStatus("Healthy");
        fish1.setDescription("Bubbles is a beautiful orange goldfish, perfect for beginners. Easy to care for and peaceful.");
        fish1.setImageUrl("fish1.png");
        fish1.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(fish1);

        // 5. Hamster
        Pet hamster1 = new Pet();
        hamster1.setName("Nibbles");
        hamster1.setType(Pet.PetType.HAMSTER);
        hamster1.setBreed("Syrian Hamster");
        hamster1.setAge(1);
        hamster1.setGender(Pet.Gender.MALE);
        hamster1.setHealthStatus("Healthy");
        hamster1.setDescription("Nibbles is an adorable Syrian hamster who loves running on his wheel. Great for kids!");
        hamster1.setImageUrl("ham1.png");
        hamster1.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(hamster1);

        // 6. Another Dog (different breed)
        Pet dog2 = new Pet();
        dog2.setName("Max");
        dog2.setType(Pet.PetType.DOG);
        dog2.setBreed("German Shepherd");
        dog2.setAge(4);
        dog2.setGender(Pet.Gender.MALE);
        dog2.setHealthStatus("Vaccinated, neutered, trained");
        dog2.setDescription("Max is a loyal and intelligent German Shepherd. Great as a guard dog and family companion.");
        dog2.setImageUrl("dog2.png");
        dog2.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(dog2);

        // 7. Another Cat (different breed)
        Pet cat2 = new Pet();
        cat2.setName("Luna");
        cat2.setType(Pet.PetType.CAT);
        cat2.setBreed("Siamese");
        cat2.setAge(1);
        cat2.setGender(Pet.Gender.FEMALE);
        cat2.setHealthStatus("Vaccinated, healthy");
        cat2.setDescription("Luna is a playful young Siamese cat with beautiful blue eyes. She's energetic and loves to play.");
        cat2.setImageUrl("cat2.png");
        cat2.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(cat2);

        // 8. Third Dog
        Pet dog3 = new Pet();
        dog3.setName("Charlie");
        dog3.setType(Pet.PetType.DOG);
        dog3.setBreed("Beagle");
        dog3.setAge(2);
        dog3.setGender(Pet.Gender.MALE);
        dog3.setHealthStatus("Vaccinated, healthy");
        dog3.setDescription("Charlie is an energetic Beagle with a curious personality. He loves to explore and is great with children.");
        dog3.setImageUrl("dog3.png");
        dog3.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(dog3);

        // 9. Third Cat
        Pet cat3 = new Pet();
        cat3.setName("Mittens");
        cat3.setType(Pet.PetType.CAT);
        cat3.setBreed("Maine Coon");
        cat3.setAge(3);
        cat3.setGender(Pet.Gender.FEMALE);
        cat3.setHealthStatus("Vaccinated, spayed, healthy");
        cat3.setDescription("Mittens is a gentle giant Maine Coon. She's friendly, loves attention, and gets along well with other pets.");
        cat3.setImageUrl("cat3.png");
        cat3.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(cat3);

        // 10. Fourth Dog
        Pet dog4 = new Pet();
        dog4.setName("Rocky");
        dog4.setType(Pet.PetType.DOG);
        dog4.setBreed("Bulldog");
        dog4.setAge(5);
        dog4.setGender(Pet.Gender.MALE);
        dog4.setHealthStatus("Vaccinated, neutered, healthy");
        dog4.setDescription("Rocky is a calm and gentle Bulldog. He loves naps and short walks, perfect for apartment living.");
        dog4.setImageUrl("dog4.png");
        dog4.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(dog4);

        // 11. Second Bird
        Pet bird2 = new Pet();
        bird2.setName("Kiwi");
        bird2.setType(Pet.PetType.BIRD);
        bird2.setBreed("Canary");
        bird2.setAge(1);
        bird2.setGender(Pet.Gender.MALE);
        bird2.setHealthStatus("Healthy, vocal");
        bird2.setDescription("Kiwi is a cheerful Canary with a beautiful singing voice. He brings joy and music to any home.");
        bird2.setImageUrl("parrot2.png");
        bird2.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(bird2);

        // 12. Fourth Cat
        Pet cat4 = new Pet();
        cat4.setName("Oliver");
        cat4.setType(Pet.PetType.CAT);
        cat4.setBreed("British Shorthair");
        cat4.setAge(4);
        cat4.setGender(Pet.Gender.MALE);
        cat4.setHealthStatus("Vaccinated, neutered, healthy");
        cat4.setDescription("Oliver is a dignified British Shorthair with a calm temperament. He's independent but loves gentle cuddles.");
        cat4.setImageUrl("cat4.png");
        cat4.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(cat4);

        // 13. Fifth Dog
        Pet dog5 = new Pet();
        dog5.setName("Daisy");
        dog5.setType(Pet.PetType.DOG);
        dog5.setBreed("Poodle");
        dog5.setAge(3);
        dog5.setGender(Pet.Gender.FEMALE);
        dog5.setHealthStatus("Vaccinated, spayed, groomed");
        dog5.setDescription("Daisy is an elegant Poodle who is hypoallergenic and smart. She's well-trained and loves learning new tricks.");
        dog5.setImageUrl("dog5.png");
        dog5.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(dog5);

        // 14. Second Hamster
        Pet hamster2 = new Pet();
        hamster2.setName("Peanut");
        hamster2.setType(Pet.PetType.HAMSTER);
        hamster2.setBreed("Dwarf Hamster");
        hamster2.setAge(1);
        hamster2.setGender(Pet.Gender.FEMALE);
        hamster2.setHealthStatus("Healthy, active");
        hamster2.setDescription("Peanut is a tiny Dwarf Hamster with lots of energy. She's adorable and easy to care for.");
        hamster2.setImageUrl("ham2.png");
        hamster2.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(hamster2);

        // 15. Fifth Cat
        Pet cat5 = new Pet();
        cat5.setName("Shadow");
        cat5.setType(Pet.PetType.CAT);
        cat5.setBreed("Black Cat");
        cat5.setAge(2);
        cat5.setGender(Pet.Gender.MALE);
        cat5.setHealthStatus("Vaccinated, neutered, healthy");
        cat5.setDescription("Shadow is a mysterious black cat with golden eyes. He's affectionate, playful, and brings good luck!");
        cat5.setImageUrl("cat5.png");
        cat5.setStatus(Pet.Status.AVAILABLE);
        petRepository.save(cat5);
    }
}
