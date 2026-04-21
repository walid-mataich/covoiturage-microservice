package ma.ensaj.covoiturage.bookingservice.client;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.bookingservice.dto.external.TripOfferResponse;
import ma.ensaj.covoiturage.bookingservice.dto.external.RideRequestResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TripServiceClient {

    private final WebClient.Builder webClientBuilder;

    // Récupérer un TripOffer (flux 1 — driver publie)
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

    // Récupérer une RideRequest (flux 2 — passager commande)
    public RideRequestResponse getRideRequest(UUID requestId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("lb://trip-service/api/trips/requests/" + requestId)
                    .retrieve()
                    .bodyToMono(RideRequestResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Demande introuvable : " + requestId);
        }
    }
}