package app.pet.repositories;

import app.pet.models.Favorite;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    List<Favorite> findAllByUser_Id(Long userId);
    Optional<Favorite> findByUser_IdAndAnimal_Id(Long userId, Long animalId);
    void deleteByUser_IdAndAnimal_Id(Long userId, Long animalId);
    boolean existsByUser_IdAndAnimal_Id(Long userId, Long animalId);
}
