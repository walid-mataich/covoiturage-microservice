package ma.ensaj.covoiturage.notificationservice.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID recipientId;
    private String title;
    private String body;
    private int tokenCount;     // combien d'appareils notifiés
    private int successCount;   // combien ont réussi
    private int failureCount;   // combien ont échoué
}