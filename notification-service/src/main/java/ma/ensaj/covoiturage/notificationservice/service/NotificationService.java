package ma.ensaj.covoiturage.notificationservice.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.notificationservice.dto.request.RegisterTokenRequest;
import ma.ensaj.covoiturage.notificationservice.dto.request.SendNotificationRequest;
import ma.ensaj.covoiturage.notificationservice.dto.response.NotificationResponse;
import ma.ensaj.covoiturage.notificationservice.entity.UserFcmToken;
import ma.ensaj.covoiturage.notificationservice.repository.UserFcmTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserFcmTokenRepository tokenRepository;

    // Enregistrer le token FCM d'un appareil
    public void registerToken(UUID userId, RegisterTokenRequest request) {

        // Vérifier si le token existe déjà
        tokenRepository.findByFcmToken(request.getFcmToken())
                .ifPresentOrElse(
                        existing -> {
                            // Mettre à jour l'userId si le token
                            // appartient maintenant à quelqu'un d'autre
                            existing.setUserId(userId);
                            tokenRepository.save(existing);
                        },
                        () -> {
                            // Créer un nouveau token
                            UserFcmToken token = UserFcmToken.builder()
                                    .userId(userId)
                                    .fcmToken(request.getFcmToken())
                                    .deviceType(request.getDeviceType())
                                    .build();
                            tokenRepository.save(token);
                        }
                );

        System.out.println("✓ Token FCM enregistré pour user : " + userId);
    }

    // Supprimer le token FCM (déconnexion)
    public void removeToken(String fcmToken) {
        tokenRepository.deleteByFcmToken(fcmToken);
    }

    // Envoyer une notification push à un utilisateur
    public NotificationResponse sendNotification(
            SendNotificationRequest request) {

        // Récupérer tous les tokens de l'utilisateur
        List<UserFcmToken> tokens = tokenRepository
                .findByUserId(request.getRecipientId());

        if (tokens.isEmpty()) {
            System.out.println(
                    "⚠ Aucun token FCM pour user : "
                            + request.getRecipientId());
            return NotificationResponse.builder()
                    .recipientId(request.getRecipientId())
                    .title(request.getTitle())
                    .body(request.getBody())
                    .tokenCount(0)
                    .successCount(0)
                    .failureCount(0)
                    .build();
        }

        // Vérifier que Firebase est initialisé
        if (FirebaseApp.getApps().isEmpty()) {
            System.err.println(
                    "⚠ Firebase non initialisé — notification non envoyée");
            return NotificationResponse.builder()
                    .recipientId(request.getRecipientId())
                    .title(request.getTitle())
                    .body(request.getBody())
                    .tokenCount(tokens.size())
                    .successCount(0)
                    .failureCount(tokens.size())
                    .build();
        }

        int successCount = 0;
        int failureCount = 0;

        for (UserFcmToken userToken : tokens) {
            try {
                // Construire le message FCM
                Message.Builder messageBuilder = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .build())
                        .setToken(userToken.getFcmToken());

                // Ajouter les données supplémentaires si présentes
                if (request.getData() != null) {
                    messageBuilder.putAllData(request.getData());
                }

                // Envoyer la notification
                String response = FirebaseMessaging.getInstance()
                        .send(messageBuilder.build());

                System.out.println(
                        "✓ Notification envoyée : " + response);
                successCount++;

            } catch (FirebaseMessagingException e) {
                System.err.println(
                        "✗ Erreur envoi notification : " + e.getMessage());
                failureCount++;

                // Si le token est invalide, le supprimer
                if (e.getMessagingErrorCode() ==
                        MessagingErrorCode.UNREGISTERED ||
                        e.getMessagingErrorCode() ==
                                MessagingErrorCode.INVALID_ARGUMENT) {
                    tokenRepository.deleteByFcmToken(
                            userToken.getFcmToken());
                    System.out.println(
                            "⚠ Token invalide supprimé : "
                                    + userToken.getFcmToken());
                }
            }
        }

        return NotificationResponse.builder()
                .recipientId(request.getRecipientId())
                .title(request.getTitle())
                .body(request.getBody())
                .tokenCount(tokens.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }

    // Notifications prédéfinies pour les événements métier
    public void notifyBookingConfirmed(UUID passengerId, UUID driverId,
                                       String driverName, UUID bookingId) {

        // Notifier le passager
        sendNotification(SendNotificationRequest.builder()
                .recipientId(passengerId)
                .title("Réservation confirmée !")
                .body(driverName + " a confirmé votre réservation.")
                .data(java.util.Map.of(
                        "type", "BOOKING_CONFIRMED",
                        "bookingId", bookingId.toString()))
                .build());
    }

    public void notifyBookingCompleted(UUID passengerId, UUID driverId,
                                       UUID bookingId) {

        // Notifier le passager
        sendNotification(SendNotificationRequest.builder()
                .recipientId(passengerId)
                .title("Trajet terminé !")
                .body("N'oubliez pas d'évaluer votre driver.")
                .data(java.util.Map.of(
                        "type", "BOOKING_COMPLETED",
                        "bookingId", bookingId.toString()))
                .build());

        // Notifier le driver
        sendNotification(SendNotificationRequest.builder()
                .recipientId(driverId)
                .title("Trajet terminé !")
                .body("N'oubliez pas d'évaluer votre passager.")
                .data(java.util.Map.of(
                        "type", "BOOKING_COMPLETED",
                        "bookingId", bookingId.toString()))
                .build());
    }

    public void notifyNewMessage(UUID receiverId,
                                 String senderName, String messagePreview, UUID conversationId) {

        sendNotification(SendNotificationRequest.builder()
                .recipientId(receiverId)
                .title("Nouveau message de " + senderName)
                .body(messagePreview)
                .data(java.util.Map.of(
                        "type", "NEW_MESSAGE",
                        "conversationId", conversationId.toString()))
                .build());
    }

    public void notifyNewDriverOffer(UUID passengerId,
                                     String driverName, Double price, UUID rideRequestId) {

        sendNotification(SendNotificationRequest.builder()
                .recipientId(passengerId)
                .title("Nouvelle offre de " + driverName)
                .body(driverName + " propose " + price + " DH pour votre trajet.")
                .data(java.util.Map.of(
                        "type", "NEW_DRIVER_OFFER",
                        "rideRequestId", rideRequestId.toString()))
                .build());
    }

    public void notifyNegotiationUpdate(UUID userId,
                                        String message, UUID negotiationId) {

        sendNotification(SendNotificationRequest.builder()
                .recipientId(userId)
                .title("Mise à jour négociation")
                .body(message)
                .data(java.util.Map.of(
                        "type", "NEGOTIATION_UPDATE",
                        "negotiationId", negotiationId.toString()))
                .build());
    }
}