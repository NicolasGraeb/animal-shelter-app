package app.pet.services;

import app.pet.enums.AdoptionStatus;
import app.pet.models.Adoption;
import app.pet.models.Animal;
import app.pet.models.User;
import app.pet.repositories.AdoptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final UserService userService;
    private final AnimalService animalService;

    public AdoptionService(AdoptionRepository adoptionRepository,
                           UserService userService,
                           AnimalService animalService) {
        this.adoptionRepository = adoptionRepository;
        this.userService       = userService;
        this.animalService     = animalService;
    }


    public List<Adoption> findAll() {
        return adoptionRepository.findAll();
    }

    public Adoption getAdoptionById(Long id) {
        return adoptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adoption not found with id: " + id));
    }

    @Transactional
    public Adoption createRequest(Long userId, Long animalId, LocalDate requestDate) {
        User user       = userService.getUserById(userId);
        Animal animal   = animalService.getAnimalById(animalId);

        Adoption adoption = new Adoption();
        adoption.setUser(user);
        adoption.setAnimal(animal);
        adoption.setRequestDate(requestDate);
        adoption.setStatus(AdoptionStatus.PENDING);
        adoption.setDecisionDate(null);

        return adoptionRepository.save(adoption);
    }

    public List<Adoption> getAllAdoptions() {
        return adoptionRepository.findAll();
    }

    public List<Adoption> findByUserId(Long userId) {
        return adoptionRepository.findByUserId(userId);
    }

    public List<Adoption> findByUserIdAndStatus(Long userId, AdoptionStatus status) {
        return adoptionRepository.findByUserIdAndStatus(userId, status);
    }

    @Transactional
    public Adoption updateStatus(Long id, AdoptionStatus newStatus, LocalDate decisionDate) {
        Adoption adoption = getAdoptionById(id);
        adoption.setStatus(newStatus);
        adoption.setDecisionDate(decisionDate);
        return adoptionRepository.save(adoption);
    }

    public Adoption createAdoption(Adoption adoption) {
        return adoptionRepository.save(adoption);
    }

    public Adoption updateAdoption(Long id, Adoption details) {
        Adoption existing = getAdoptionById(id);
        existing.setAnimal(details.getAnimal());
        existing.setUser(details.getUser());
        existing.setRequestDate(details.getRequestDate());
        existing.setStatus(details.getStatus());
        existing.setDecisionDate(details.getDecisionDate());
        return adoptionRepository.save(existing);
    }

    public void deleteAdoption(Long id) {
        Adoption existing = getAdoptionById(id);
        adoptionRepository.delete(existing);
    }
}
