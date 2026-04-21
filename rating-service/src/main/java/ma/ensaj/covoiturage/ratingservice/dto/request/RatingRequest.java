package ma.ensaj.covoiturage.ratingservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class RatingRequest {

    @NotNull(message = "L'ID du booking est obligatoire")
    private UUID bookingId;

    // L'utilisateur noté — on valide côté service
    // que c'est bien le driver ou le passager du booking
    @NotNull(message = "L'ID de l'utilisateur noté est obligatoire")
    private UUID ratedUserId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimum est 1")
    @Max(value = 5, message = "La note maximum est 5")
    private Integer score;

    private String comment;
}