package ma.ensaj.covoiturage.negotiationservice.dto.external;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BookingRequest {

    private UUID tripId;
    private String bookingType;
    private Integer seatsBooked;
    private Double proposedPrice;
    private UUID driverId;
}