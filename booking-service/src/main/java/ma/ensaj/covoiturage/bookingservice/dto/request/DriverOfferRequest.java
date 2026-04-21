package ma.ensaj.covoiturage.bookingservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DriverOfferRequest {

    @NotNull(message = "L'ID de la demande est obligatoire")
    private UUID rideRequestId;

    // Plus besoin de passengerId — on le récupère depuis trip-service

    @NotNull(message = "Le prix proposé est obligatoire")
    private Double proposedPrice;

    @NotNull(message = "L'heure de départ est obligatoire")
    private LocalDateTime proposedDepartureTime;

    private String message;

    // Plus besoin de vehicleBrand, vehicleModel, vehiclePlate
    // on les récupère depuis user-service via driverId (X-User-Id)
}