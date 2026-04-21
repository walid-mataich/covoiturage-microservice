package ma.ensaj.covoiturage.negotiationservice.dto.external;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripOfferResponse {
    private UUID id;
    private UUID driverId;
    private String departureCity;
    private String destinationCity;
    private LocalDateTime departureTime;
    private Integer availableSeats;
    private Double pricePerSeat;
    private Boolean isPriceNegotiable;
    private String status;
}