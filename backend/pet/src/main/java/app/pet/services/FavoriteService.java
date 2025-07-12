package app.pet.services;

import app.pet.models.Favorite;
import app.pet.repositories.FavoriteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public List<Favorite> getAllFavorites() {
        return favoriteRepository.findAll();
    }

    public List<Favorite> getFavoritesByUser(Long userId) {
        return favoriteRepository.findAllByUser_Id(userId);
    }

    public Favorite getFavoriteById(Long id) {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorite not found with id: " + id));
    }

    public Favorite createFavorite(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    public Favorite updateFavorite(Long id, Favorite favoriteDetails) {
        Favorite existing = getFavoriteById(id);
        existing.setUser(favoriteDetails.getUser());
        existing.setAnimal(favoriteDetails.getAnimal());
        return favoriteRepository.save(existing);
    }

    @Transactional
    public void removeFavorite(Long userId, Long animalId) {
        favoriteRepository.deleteByUser_IdAndAnimal_Id(userId, animalId);
    }

    public boolean isFavorite(Long userId, Long animalId) {
        return favoriteRepository.existsByUser_IdAndAnimal_Id(userId, animalId);
    }

    public void deleteByUserAndAnimal(Long userId, Long animalId) {
        favoriteRepository.deleteByUser_IdAndAnimal_Id(userId, animalId);
    }
}
