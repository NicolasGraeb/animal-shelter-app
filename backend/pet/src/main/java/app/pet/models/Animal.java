package app.pet.models;

import app.pet.enums.AnimalHabit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import app.pet.enums.Status;
import app.pet.enums.Sex;
import java.util.List;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "animals")
@Data @NoArgsConstructor @AllArgsConstructor
public class Animal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 30)
    private String species;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Sex sex;

    private Integer age;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "image_data")
    private byte[] imageData;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AnimalHabit animalHabit;

    @OneToMany(mappedBy = "animal")
    @JsonIgnore
    private List<Intake> intakes;

    @OneToMany(mappedBy = "animal")
    @JsonIgnore
    private List<Adoption> adoptions;

    @OneToMany(mappedBy = "animal")
    @JsonIgnore
    private List<Favorite> favorites;
}



