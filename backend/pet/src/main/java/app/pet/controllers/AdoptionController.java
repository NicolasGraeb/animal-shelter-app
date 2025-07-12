package app.pet.controllers;

import app.pet.enums.AdoptionStatus;
import app.pet.enums.Status;
import app.pet.models.Adoption;
import app.pet.models.Animal;
import app.pet.services.AdoptionService;
import app.pet.services.AnimalService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/api/adoptions")
@AllArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;
    private final AnimalService animalService;

    @PostMapping
    public ResponseEntity<Adoption> requestAdoption(
            @RequestBody RequestAdoption dto,
            Authentication auth
    ) {
        Long userId = Long.valueOf(auth.getName());
        Adoption created = adoptionService.createRequest(userId, dto.getAnimalId(), LocalDate.now());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/my")
    public List<MyAdoptionDTO> myRequests(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return adoptionService.findByUserId(userId).stream()
            .map(ad -> {
                Animal animal = animalService.getAnimalById(ad.getAnimal().getId());
                return new MyAdoptionDTO(
                    ad.getId(),
                    animal.getId(),
                    animal.getName(),
                    animal.getAge(),
                    ad.getRequestDate(),
                    ad.getStatus(),
                    ad.getDecisionDate()
                );
            })
            .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Adoption> allRequests() {
        return adoptionService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Adoption> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatus dto
    ) {
        Adoption updated = adoptionService.updateStatus(id, AdoptionStatus.valueOf(dto.getStatus()), LocalDate.now());
        if (dto.getStatus().equalsIgnoreCase("APPROVED")) {
            var animal = updated.getAnimal();
            animal.setStatus(Status.ADOPTED);
            animalService.updateAnimal(animal.getId(), animal);
        }
        return ResponseEntity.ok(updated);
    }

    @Data
    public static class RequestAdoption {
        private Long animalId;
    }

    @Data
    public static class UpdateStatus {
        private String status;
    }

    @Data
    @AllArgsConstructor
    public static class MyAdoptionDTO {
        private Long id;
        private Long animalId;
        private String animalName;
        private int animalAge;
        private LocalDate requestDate;
        private AdoptionStatus status;
        private LocalDate decisionDate;
    }
}
