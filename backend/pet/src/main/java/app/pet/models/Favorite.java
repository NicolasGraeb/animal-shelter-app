package app.pet.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "favorites",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","animal_id"}))
@Data @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"animal","user"})
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(name = "liked_at", nullable = false, updatable = false)
    private Instant likedAt = Instant.now();
}
