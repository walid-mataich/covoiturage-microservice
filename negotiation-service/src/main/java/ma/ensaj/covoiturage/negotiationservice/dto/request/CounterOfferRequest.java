package ma.ensaj.covoiturage.negotiationservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CounterOfferRequest {

    @NotNull(message = "Le prix proposé est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private Double proposedPrice;

    private String message;
}