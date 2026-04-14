package ma.ensaj.covoiturage.userservice.dto.request;


import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicture;

    // Champs driver
    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;
    private Integer vehicleSeats;
}
