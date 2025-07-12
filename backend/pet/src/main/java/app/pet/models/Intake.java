package app.pet.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "intakes")
@Data @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"animal","user"})
public class Intake {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(name = "received_from", length = 100)
    private String receivedFrom;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
