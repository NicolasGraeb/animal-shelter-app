package app.pet.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import app.pet.enums.AdoptionStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adoptions")
@Data @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"animal","user"})
public class Adoption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AdoptionStatus status;

    @Column(name = "decision_date")
    private LocalDate decisionDate;
}