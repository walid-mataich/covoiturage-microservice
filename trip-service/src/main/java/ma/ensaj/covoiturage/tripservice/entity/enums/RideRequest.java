package ma.ensaj.covoiturage.tripservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.ensaj.covoiturage.tripservice.entity.enums.RideRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ride_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ID du passager (vient du token JWT via header X-User-Id)
    @Column(nullable = false)
    private UUID passengerId;

    // Départ
    @Column(nullable = false)
    private String departureCity;

    @Column(nullable = false)
    private String departureAddress;

    @Column(nullable = false)
    private Double departureLat;

    @Column(nullable = false)
    private Double departureLng;

    // Destination
    @Column(nullable = false)
    private String destinationCity;

    @Column(nullable = false)
    private String destinationAddress;

    @Column(nullable = false)
    private Double destinationLat;

    @Column(nullable = false)
    private Double destinationLng;

    // Détails
    @Column(nullable = false)
    private LocalDateTime desiredDepartureTime;

    @Column(nullable = false)
    private Integer seatsNeeded;

    // Budget max proposé par le passager
    private Double maxBudget;

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideRequestStatus status = RideRequestStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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