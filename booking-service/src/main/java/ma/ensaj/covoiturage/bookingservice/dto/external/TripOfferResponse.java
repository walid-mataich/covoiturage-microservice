package ma.ensaj.covoiturage.bookingservice.dto.external;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripOfferResponse {

    private UUID id;
    private UUID driverId;
    private String departureCity;
    private String departureAddress;
    private Double departureLat;
    private Double departureLng;
    private String destinationCity;
    private String destinationAddress;
    private Double destinationLat;
    private Double destinationLng;
    private LocalDateTime departureTime;
    private Integer availableSeats;
    private Double pricePerSeat;
    private Boolean isPriceNegotiable;
    private String status;
}