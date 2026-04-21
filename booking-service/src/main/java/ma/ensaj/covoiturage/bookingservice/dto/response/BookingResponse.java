package ma.ensaj.covoiturage.bookingservice.dto.response;

import lombok.*;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingStatus;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private UUID id;
    private UUID passengerId;
    private UUID driverId;
    private UUID tripId;
    private BookingType bookingType;
    private String departureCity;
    private String destinationCity;
    private LocalDateTime departureTime;
    private Integer seatsBooked;
    private Double totalPrice;
    private BookingStatus status;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;
}