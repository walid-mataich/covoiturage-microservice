package ma.ensaj.covoiturage.bookingservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingType;

import java.util.UUID;

@Data
public class BookingRequest {

    @NotNull(message = "L'ID du trajet est obligatoire")
    private UUID tripId;

    @NotNull(message = "Le type de réservation est obligatoire")
    private BookingType bookingType;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Au moins 1 place")
    private Integer seatsBooked;

    // Utilisé uniquement pour FROM_RIDE_REQUEST
    // le driver qui a soumis l'offre acceptée
    private UUID driverId;
    // Ajoute ce champ dans BookingRequest.java
    private Double proposedPrice; // prix final après négociation


}