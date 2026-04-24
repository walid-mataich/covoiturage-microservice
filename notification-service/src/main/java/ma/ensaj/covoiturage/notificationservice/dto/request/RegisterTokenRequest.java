package ma.ensaj.covoiturage.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterTokenRequest {

    @NotBlank(message = "Le token FCM est obligatoire")
    private String fcmToken;

    private String deviceType; // ANDROID ou IOS
}