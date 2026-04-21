package ma.ensaj.covoiturage.ratingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.ensaj.covoiturage.ratingservice.entity.enums.RatedSide;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Le booking concerné
    @Column(nullable = false)
    private UUID bookingId;

    // Qui a donné la note
    @Column(nullable = false)
    private UUID raterId;

    // Qui a reçu la note
    @Column(nullable = false)
    private UUID ratedId;

    // Driver ou Passenger qui est noté
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatedSide ratedSide;

    // Note entre 1 et 5
    @Column(nullable = false)
    private Integer score;

    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}