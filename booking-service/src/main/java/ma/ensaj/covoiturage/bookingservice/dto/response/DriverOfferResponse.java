package ma.ensaj.covoiturage.bookingservice.dto.response;

import lombok.*;
import ma.ensaj.covoiturage.bookingservice.entity.enums.DriverOfferStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverOfferResponse {

    private UUID id;
    private UUID driverId;
    private UUID rideRequestId;
    private UUID passengerId;
    private Double proposedPrice;
    private LocalDateTime proposedDepartureTime;
    private String message;
    private DriverOfferStatus status;
    private LocalDateTime createdAt;

    // Enrichi depuis trip-service (optionnel selon l'endpoint)
    private String departureCity;
    private String destinationCity;
    private Integer seatsNeeded;
    private Double maxBudget;
}