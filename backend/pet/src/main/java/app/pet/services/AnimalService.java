package app.pet.services;

import app.pet.models.Animal;
import app.pet.repositories.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    @Autowired
    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public Animal getAnimalById(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
    }

    public Animal createAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    public Animal updateAnimal(Long id, Animal animalDetails) {
        Animal existing = getAnimalById(id);
        existing.setName(animalDetails.getName());
        existing.setSpecies(animalDetails.getSpecies());
        existing.setSex(animalDetails.getSex());
        existing.setAge(animalDetails.getAge());
        existing.setDescription(animalDetails.getDescription());
        existing.setImageData(animalDetails.getImageData());
        existing.setStatus(animalDetails.getStatus());
        return animalRepository.save(existing);
    }

    public void deleteAnimal(Long id) {
        Animal existing = getAnimalById(id);
        animalRepository.delete(existing);
    }
}

