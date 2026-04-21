package ma.ensaj.covoiturage.negotiationservice.client;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.negotiationservice.dto.external.TripOfferResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TripServiceClient {

    private final WebClient.Builder webClientBuilder;

    public TripOfferResponse getTripOffer(UUID tripId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("lb://trip-service/api/trips/offers/" + tripId)
                    .retrieve()
                    .bodyToMono(TripOfferResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Trajet introuvable : " + tripId);
        }
    }
}