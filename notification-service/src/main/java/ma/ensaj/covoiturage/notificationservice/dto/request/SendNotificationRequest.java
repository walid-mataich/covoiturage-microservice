package ma.ensaj.covoiturage.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class SendNotificationRequest {

    @NotNull(message = "L'ID du destinataire est obligatoire")
    private UUID recipientId;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "Le message est obligatoire")
    private String body;

    // Données supplémentaires pour l'app (optionnel)
    // ex: { "bookingId": "uuid", "type": "BOOKING_CONFIRMED" }
    private Map<String, String> data;
}