package ma.ensaj.covoiturage.messagingservice.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LocationUpdate {

    private UUID bookingId;
    private UUID passengerId;
    private Double lat;
    private Double lng;
    private Double speed;        // km/h — optionnel
    private LocalDateTime timestamp;
}