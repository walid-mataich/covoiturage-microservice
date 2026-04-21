package ma.ensaj.covoiturage.negotiationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.ensaj.covoiturage.negotiationservice.entity.enums.NegotiationStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "negotiations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Negotiation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID driverId;

    @Column(nullable = false)
    private UUID passengerId;

    // ID du TripOffer ou RideRequest dans trip-service
    @Column(nullable = false)
    private UUID tripId;

    // Prix initial du trajet (récupéré depuis trip-service)
    @Column(nullable = false)
    private Double initialPrice;

    // Prix final accepté par les deux parties
    private Double agreedPrice;

    @Column(nullable = false)
    private Integer seatsNeeded;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NegotiationStatus status = NegotiationStatus.OPEN;

    // ID du booking créé après accord (dans booking-service)
    private UUID bookingId;

    @OneToMany(mappedBy = "negotiation",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    private List<NegotiationOffer> offers = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime agreedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}