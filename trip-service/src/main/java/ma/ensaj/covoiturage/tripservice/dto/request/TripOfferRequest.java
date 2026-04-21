package ma.ensaj.covoiturage.tripservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripOfferRequest {

    @NotBlank(message = "La ville de départ est obligatoire")
    private String departureCity;

    @NotBlank(message = "L'adresse de départ est obligatoire")
    private String departureAddress;

    @NotNull(message = "La latitude de départ est obligatoire")
    private Double departureLat;

    @NotNull(message = "La longitude de départ est obligatoire")
    private Double departureLng;

    @NotBlank(message = "La ville de destination est obligatoire")
    private String destinationCity;

    @NotBlank(message = "L'adresse de destination est obligatoire")
    private String destinationAddress;

    @NotNull(message = "La latitude de destination est obligatoire")
    private Double destinationLat;

    @NotNull(message = "La longitude de destination est obligatoire")
    private Double destinationLng;

    @NotNull(message = "L'heure de départ est obligatoire")
    @Future(message = "L'heure de départ doit être dans le futur")
    private LocalDateTime departureTime;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Au moins 1 place disponible")
    @Max(value = 8, message = "Maximum 8 places")
    private Integer availableSeats;

    @NotNull(message = "Le prix par place est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private Double pricePerSeat;

    private Boolean isPriceNegotiable = true;
    private String description;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;
}