package app.pet.controllers;

import app.pet.enums.AdoptionStatus;
import app.pet.enums.Role;
import app.pet.enums.Status;
import app.pet.models.Adoption;
import app.pet.models.Animal;
import app.pet.models.User;
import app.pet.services.AdoptionService;
import app.pet.services.AnimalService;
import app.pet.services.UserService;
import app.pet.services.JWTService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdoptionService adoptionService;
    private final AnimalService animalService;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // --- Użytkownicy ---
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users/me")
    public User getCurrentUser(Authentication auth) {
        Long id = Long.valueOf(auth.getName());
        return userService.getUserById(id);
    }

    @GetMapping("/users/me/adoptions")
    public List<MyAdoptionDTO> getMyAdoptions(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return adoptionService.findByUserIdAndStatus(userId, AdoptionStatus.APPROVED)
            .stream()
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

    @GetMapping("/users/me/requests")
    public List<Adoption> getMyRequests(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return adoptionService.findByUserId(userId);
    }

    // --- Rejestracja & logowanie ---
    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        User user = new User();
        user.setUsername(req.getEmail());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        User saved = userService.createUser(user);

        String accessToken  = jwtService.generateAccessToken(saved);
        String refreshToken = jwtService.generateRefreshToken(saved);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        User user = userService.getUserByUsername(req.getUsername());
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private Role role;
    }

    @Data
    @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    @AllArgsConstructor
    private static class MyAdoptionDTO {
        private Long id;
        private Long animalId;
        private String animalName;
        private int animalAge;
        private LocalDate requestDate;
        private AdoptionStatus status;
        private LocalDate decisionDate;
    }
}
