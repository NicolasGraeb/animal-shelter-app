package app.pet.services;


import app.pet.models.Intake;
import app.pet.repositories.IntakeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntakeService {

    private final IntakeRepository intakeRepository;

    @Autowired
    public IntakeService(IntakeRepository intakeRepository) {
        this.intakeRepository = intakeRepository;
    }

    public List<Intake> getAllIntakes() {
        return intakeRepository.findAll();
    }

    public Intake getIntakeById(Long id) {
        return intakeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intake not found with id: " + id));
    }

    public Intake createIntake(Intake intake) {
        return intakeRepository.save(intake);
    }

    public Intake updateIntake(Long id, Intake intakeDetails) {
        Intake existing = getIntakeById(id);
        existing.setAnimal(intakeDetails.getAnimal());
        existing.setReceivedFrom(intakeDetails.getReceivedFrom());
        existing.setReceivedDate(intakeDetails.getReceivedDate());
        existing.setNotes(intakeDetails.getNotes());
        return intakeRepository.save(existing);
    }

    public void deleteIntake(Long id) {
        Intake existing = getIntakeById(id);
        intakeRepository.delete(existing);
    }
}

