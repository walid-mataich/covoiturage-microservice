package ma.ensaj.covoiturage.messagingservice.client;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.messagingservice.dto.external.UserResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    public UserResponse getUser(UUID userId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("lb://user-service/api/users/" + userId)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            // Si l'utilisateur n'est pas trouvé
            // retourner un objet vide plutôt que planter
            UserResponse fallback = new UserResponse();
            fallback.setId(userId);
            fallback.setFirstName("Utilisateur");
            fallback.setLastName("inconnu");
            return fallback;
        }
    }
}