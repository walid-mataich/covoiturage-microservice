package ma.ensaj.covoiturage.messagingservice.dto.external;

import lombok.Data;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String role;
}