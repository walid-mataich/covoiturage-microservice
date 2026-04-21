package ma.ensaj.covoiturage.negotiationservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class StartNegotiationRequest {

    // ID du TripOffer — on récupère le reste depuis trip-service
    @NotNull(message = "L'ID du trajet est obligatoire")
    private UUID tripId;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Au moins 1 place")
    private Integer seatsNeeded;

    // Prix initial proposé par le passager
    @NotNull(message = "Le prix proposé est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private Double proposedPrice;

    private String message;
}