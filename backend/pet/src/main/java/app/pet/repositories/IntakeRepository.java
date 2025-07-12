package app.pet.repositories;

import app.pet.models.Intake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeRepository extends JpaRepository<Intake,Long> {
}
