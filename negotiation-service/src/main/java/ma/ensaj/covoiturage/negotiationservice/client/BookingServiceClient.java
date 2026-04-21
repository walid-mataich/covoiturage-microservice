package ma.ensaj.covoiturage.negotiationservice.client;

import lombok.RequiredArgsConstructor;

import ma.ensaj.covoiturage.negotiationservice.dto.external.BookingCreatedResponse;
import ma.ensaj.covoiturage.negotiationservice.dto.external.BookingRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingServiceClient {

    private final WebClient.Builder webClientBuilder;

    public BookingCreatedResponse createBooking(
            UUID tripId,
            UUID passengerId,
            UUID driverId,
            Integer seatsBooked,
            Double agreedPrice) {
        try {
            BookingRequest request = BookingRequest.builder()
                    .tripId(tripId)
                    .bookingType("FROM_TRIP_OFFER")
                    .seatsBooked(seatsBooked)
                    .proposedPrice(agreedPrice)
                    .driverId(driverId)
                    .build();

            // Log pour confirmer
            System.out.println("=== BookingServiceClient ===");
            System.out.println("tripId       : " + tripId);
            System.out.println("passengerId  : " + passengerId);
            System.out.println("driverId     : " + driverId);
            System.out.println("seatsBooked  : " + seatsBooked);
            System.out.println("agreedPrice  : " + agreedPrice);

            return webClientBuilder.build()
                    .post()
                    .uri("lb://booking-service/api/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-User-Id", passengerId.toString())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(BookingCreatedResponse.class)
                    .block();

        } catch (WebClientResponseException e) {
            throw new RuntimeException(
                    "Erreur création booking : "
                            + e.getStatusCode()
                            + " — "
                            + e.getResponseBodyAsString()
            );
        }
    }
}