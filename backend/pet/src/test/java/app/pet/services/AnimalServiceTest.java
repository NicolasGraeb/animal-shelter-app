package app.pet.services;

import app.pet.models.Animal;
import app.pet.repositories.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AnimalService animalService;

    private Animal fido;

    @BeforeEach
    void setUp() {
        fido = new Animal();
        fido.setId(1L);
        fido.setName("Fido");
        fido.setSpecies("Dog");
        fido.setAge(5);
        fido.setDescription("friendly");
    }

    @Test
    void getAllAnimals_returnsAll() {
        when(animalRepository.findAll()).thenReturn(List.of(fido));
        var result = animalService.getAllAnimals();
        assertThat(result).containsExactly(fido);
        verify(animalRepository).findAll();
    }

    @Test
    void getAnimalById_existing_returnsAnimal() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(fido));
        var result = animalService.getAnimalById(1L);
        assertThat(result).isSameAs(fido);
    }

    @Test
    void getAnimalById_missing_throws() {
        when(animalRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> animalService.getAnimalById(2L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Animal not found with id: 2");
    }

    @Test
    void createAnimal_savesAndReturns() {
        when(animalRepository.save(fido)).thenReturn(fido);
        var result = animalService.createAnimal(fido);
        assertThat(result).isSameAs(fido);
        verify(animalRepository).save(fido);
    }

    @Test
    void updateAnimal_existing_updatesAndSaves() {
        Animal updates = new Animal();
        updates.setName("Rex");
        updates.setSpecies("Dog");
        updates.setAge(6);
        updates.setDescription("playful");
        updates.setSex(null);
        updates.setImageData(null);
        updates.setStatus(null);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(fido));
        when(animalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = animalService.updateAnimal(1L, updates);

        assertThat(result.getName()).isEqualTo("Rex");
        assertThat(result.getAge()).isEqualTo(6);
        assertThat(result.getDescription()).isEqualTo("playful");
        verify(animalRepository).save(fido);
    }

    @Test
    void deleteAnimal_existing_deletes() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(fido));
        animalService.deleteAnimal(1L);
        verify(animalRepository).delete(fido);
    }
}
