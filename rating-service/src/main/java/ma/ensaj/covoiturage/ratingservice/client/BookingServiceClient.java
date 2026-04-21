package ma.ensaj.covoiturage.ratingservice.client;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.ratingservice.dto.external.BookingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingServiceClient {

    private final WebClient.Builder webClientBuilder;

    public BookingResponse getBooking(UUID bookingId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("lb://booking-service/api/bookings/" + bookingId)
                    .retrieve()
                    .bodyToMono(BookingResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Booking introuvable : " + bookingId);
        }
    }
}