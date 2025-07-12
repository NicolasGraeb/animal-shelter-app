package app.pet.controllers;

import app.pet.enums.AdoptionStatus;
import app.pet.enums.Sex;
import app.pet.enums.Status;
import app.pet.models.Animal;
import app.pet.models.Adoption;
import app.pet.models.Intake;
import app.pet.models.User;
import app.pet.services.AnimalService;
import app.pet.services.AdoptionService;
import app.pet.services.IntakeService;
import app.pet.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.Arrays;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private static final Logger log = LoggerFactory.getLogger(AnimalController.class);

    private final AnimalService animalService;
    private final AdoptionService adoptionService;
    private final IntakeService intakeService;
    private final UserService userService;

    public AnimalController(AnimalService animalService,
                            AdoptionService adoptionService,
                            IntakeService intakeService
                            , UserService userService) {
        this.animalService = animalService;
        this.adoptionService = adoptionService;
        this.intakeService = intakeService;
        this.userService = userService;
    }

    @GetMapping
    public List<Animal> getAllAvailableAnimals() {
    return animalService.getAllAnimals().stream()
        .filter(animal -> animal.getStatus() == Status.AVAILABLE)
        .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Animal getAnimalById(@PathVariable Long id) {
        return animalService.getAnimalById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Animal createAnimal(
        @RequestParam String name,
        @RequestParam String species,
        @RequestParam("sex") String sexStr,
        @RequestParam Integer age,
        @RequestParam String description,
        @RequestParam("status") String statusStr,
        @RequestPart(name="image", required=false) MultipartFile image,
        HttpServletRequest request
    ) throws IOException {
        log.info("POST /api/animals called");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String h = headerNames.nextElement();
            log.info("Header: {}={}", h, request.getHeader(h));
        }

        log.info("Raw sexStr: '{}' bytes={}", sexStr, Arrays.toString(sexStr.getBytes()));
        log.info("Raw statusStr: '{}' bytes={}", statusStr, Arrays.toString(statusStr.getBytes()));

        Sex sex;
        Status status;
        try {
            String sexClean = sexStr.trim().toUpperCase();
            String statusClean = statusStr.trim().toUpperCase();
            sex = Sex.valueOf(sexClean);
            status = Status.valueOf(statusClean);
        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value: sex='{}', status='{}'", sexStr, statusStr);
            throw new RuntimeException("Invalid sex or status value");
        }

        log.info("Cleaned enums - sex={}, status={}", sex, status);

        Animal a = new Animal();
        a.setName(name);
        a.setSpecies(species);
        a.setSex(sex);
        a.setAge(age);
        a.setDescription(description);
        a.setStatus(status);

        if (image != null && !image.isEmpty()) {
            log.info("Received image: filename={}, size={}", image.getOriginalFilename(), image.getSize());
            a.setImageData(image.getBytes());
        }

        Animal saved = animalService.createAnimal(a);
        log.info("Created Animal id={}", saved.getId());
        return saved;
    }

    @PutMapping("/{id}")
    public Animal updateAnimal(@PathVariable Long id, @RequestBody Animal animal) {
        return animalService.updateAnimal(id, animal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/adoptions")
    public List<Adoption> getAdoptionsForAnimal(@PathVariable Long id) {
        return adoptionService.getAllAdoptions().stream()
                .filter(a -> a.getAnimal().getId().equals(id))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/adoptions")
    public Adoption createAdoptionForAnimal(@PathVariable Long id) {
        String principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userService.getUserById(Long.valueOf(principal));

        Animal animal = animalService.getAnimalById(id);

        Adoption adoption = new Adoption();
        adoption.setAnimal(animal);
        adoption.setUser(user);
        adoption.setRequestDate(LocalDate.now());
        adoption.setStatus(AdoptionStatus.PENDING);

        return adoptionService.createAdoption(adoption);
    }

    @GetMapping("/{id}/intakes")
    public List<Intake> getIntakesForAnimal(@PathVariable Long id) {
        return intakeService.getAllIntakes().stream()
                .filter(i -> i.getAnimal().getId().equals(id))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/intakes")
    public Intake createIntakeForAnimal(@PathVariable Long id, @RequestBody Intake intake) {
        Animal animal = animalService.getAllAnimals().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
        intake.setAnimal(animal);
        return intakeService.createIntake(intake);
    }
}
