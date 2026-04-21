package ma.ensaj.covoiturage.ratingservice.dto.external;

import lombok.Data;
import java.util.UUID;

@Data
public class BookingResponse {
    private UUID id;
    private UUID passengerId;
    private UUID driverId;
    private String status;
}