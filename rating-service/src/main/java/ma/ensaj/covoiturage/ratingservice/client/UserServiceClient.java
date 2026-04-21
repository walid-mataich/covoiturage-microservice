package ma.ensaj.covoiturage.ratingservice.client;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.ratingservice.dto.external.UserRatingUpdate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    public void updateUserRating(UUID userId, UserRatingUpdate update) {
        try {
            webClientBuilder.build()
                    .patch()
                    .uri("lb://user-service/api/users/" + userId + "/rating")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(update)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            System.out.println("=== UserServiceClient ===");
            System.out.println("userId        : " + userId);
            System.out.println("averageRating : " + update.getAverageRating());
            System.out.println("totalRatings  : " + update.getTotalRatings());

        } catch (WebClientResponseException e) {
            System.err.println("Erreur MAJ rating : "
                    + e.getStatusCode()
                    + " — "
                    + e.getResponseBodyAsString());
        }
    }
}