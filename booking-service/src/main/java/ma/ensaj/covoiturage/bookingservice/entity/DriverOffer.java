package ma.ensaj.covoiturage.bookingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.ensaj.covoiturage.bookingservice.entity.enums.DriverOfferStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "driver_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Le driver qui répond
    @Column(nullable = false)
    private UUID driverId;

    // La demande du passager (RideRequest dans trip-service)
    @Column(nullable = false)
    private UUID rideRequestId;

    // Le passager concerné
    @Column(nullable = false)
    private UUID passengerId;

    // Prix proposé par le driver
    @Column(nullable = false)
    private Double proposedPrice;

    // Infos véhicule
    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;

    // Heure de départ proposée
    @Column(nullable = false)
    private LocalDateTime proposedDepartureTime;

    // Message du driver
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverOfferStatus status = DriverOfferStatus.PENDING;

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