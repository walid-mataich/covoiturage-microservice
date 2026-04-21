package ma.ensaj.covoiturage.tripservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.ensaj.covoiturage.tripservice.entity.enums.TripStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ID du driver (vient du token JWT via header X-User-Id)
    @Column(nullable = false)
    private UUID driverId;

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

    // Détails du trajet
    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private Double pricePerSeat;

    private Boolean isPriceNegotiable = true;

    private String description;

    // Véhicule
    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.OPEN;

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