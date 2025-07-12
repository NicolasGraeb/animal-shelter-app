package app.pet.controllers;

import app.pet.models.Favorite;
import app.pet.models.User;
import app.pet.models.Animal;
import app.pet.services.FavoriteService;
import app.pet.services.UserService;
import app.pet.services.AnimalService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;
    private final AnimalService animalService;

    public FavoriteController(FavoriteService favoriteService,
                              UserService userService,
                              AnimalService animalService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
        this.animalService = animalService;
    }

    @GetMapping
    public List<Favorite> getAllFavorites(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        User user = userService.getUserById(userId);
        return favoriteService.getFavoritesByUser(user.getId());
    }

    @PostMapping
    public Favorite createFavorite(@RequestBody FavoriteRequest request,
                                   Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        User user = userService.getUserById(userId);
        Animal animal = animalService.getAnimalById(request.getAnimalId());
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setAnimal(animal);
        return favoriteService.createFavorite(favorite);
    }

    @DeleteMapping("/{animalId}")
    public void removeFavorite(@PathVariable Long animalId,
                               Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        favoriteService.removeFavorite(userId, animalId);
    }

    @GetMapping("/exists/{animalId}")
    public ResponseEntity<Map<String, Boolean>> isFavorite(
            @PathVariable Long animalId,
            Authentication auth
    ) {
        Long userId = Long.parseLong(auth.getName());
        boolean liked = favoriteService.isFavorite(userId, animalId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @Data
    private static class FavoriteRequest {
        private Long animalId;
    }
}
