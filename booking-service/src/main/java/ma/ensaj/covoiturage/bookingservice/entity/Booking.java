package ma.ensaj.covoiturage.bookingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingStatus;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Qui a créé la réservation
    @Column(nullable = false)
    private UUID passengerId;

    @Column(nullable = false)
    private UUID driverId;

    // Référence vers le trajet ou la demande (dans trip-service)
    @Column(nullable = false)
    private UUID tripId;       // ID du TripOffer ou RideRequest

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingType bookingType;

    // Détails du trajet (dupliqués pour éviter les appels inter-services)
    @Column(nullable = false)
    private String departureCity;

    @Column(nullable = false)
    private String destinationCity;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private Integer seatsBooked;

    // Prix final après négociation éventuelle
    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    // Raison d'annulation si applicable
    private String cancellationReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;

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