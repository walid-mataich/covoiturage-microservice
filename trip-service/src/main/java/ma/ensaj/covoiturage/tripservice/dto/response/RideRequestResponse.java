package ma.ensaj.covoiturage.tripservice.dto.response;

import lombok.*;
import ma.ensaj.covoiturage.tripservice.entity.enums.RideRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestResponse {

    private UUID id;
    private UUID passengerId;
    private String departureCity;
    private String departureAddress;
    private Double departureLat;
    private Double departureLng;
    private String destinationCity;
    private String destinationAddress;
    private Double destinationLat;
    private Double destinationLng;
    private LocalDateTime desiredDepartureTime;
    private Integer seatsNeeded;
    private Double maxBudget;
    private String notes;
    private RideRequestStatus status;
    private LocalDateTime createdAt;
}