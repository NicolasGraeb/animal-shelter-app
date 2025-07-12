package app.pet.repositories;

import app.pet.enums.AdoptionStatus;
import app.pet.models.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdoptionRepository extends JpaRepository<Adoption,Long> {

    List<Adoption> findByUserId(Long userId);

    List<Adoption> findByUserIdAndStatus(Long userId, AdoptionStatus status);

    List<Adoption> findByAnimalId(Long animalId);

    List<Adoption> findByStatus(AdoptionStatus status);
}
