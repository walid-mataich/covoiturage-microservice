package ma.ensaj.covoiturage.negotiationservice.dto.external;

import lombok.Data;
import java.util.UUID;

@Data
public class BookingCreatedResponse {
    private UUID id;
    private String status;
    private Double totalPrice;
}