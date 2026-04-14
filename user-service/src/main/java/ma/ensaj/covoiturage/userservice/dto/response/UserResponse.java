package ma.ensaj.covoiturage.userservice.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.ensaj.covoiturage.userservice.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private String profilePicture;
    private Double averageRating;
    private Integer totalRatings;
    private Boolean isVerified;

    // Champs driver
    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;
    private Integer vehicleSeats;

    private LocalDateTime createdAt;
}
