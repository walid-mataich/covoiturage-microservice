package ma.ensaj.covoiturage.tripservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideRequestRequest {

    @NotBlank(message = "La ville de départ est obligatoire")
    private String departureCity;

    @NotBlank(message = "L'adresse de départ est obligatoire")
    private String departureAddress;

    @NotNull
    private Double departureLat;

    @NotNull
    private Double departureLng;

    @NotBlank(message = "La ville de destination est obligatoire")
    private String destinationCity;

    @NotBlank(message = "L'adresse de destination est obligatoire")
    private String destinationAddress;

    @NotNull
    private Double destinationLat;

    @NotNull
    private Double destinationLng;

    @NotNull(message = "L'heure souhaitée est obligatoire")
    @Future(message = "L'heure doit être dans le futur")
    private LocalDateTime desiredDepartureTime;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Au moins 1 place")
    private Integer seatsNeeded;

    private Double maxBudget;
    private String notes;
}