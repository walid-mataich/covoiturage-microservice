package ma.ensaj.covoiturage.bookingservice.dto.external;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RideRequestResponse {

    private UUID id;
    private UUID passengerId;
    private String departureCity;
    private String departureAddress;
    private String destinationCity;
    private String destinationAddress;
    private LocalDateTime desiredDepartureTime;
    private Integer seatsNeeded;
    private Double maxBudget;
    private String status;
}