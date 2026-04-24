package ma.ensaj.covoiturage.notificationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.notificationservice.dto.request.RegisterTokenRequest;
import ma.ensaj.covoiturage.notificationservice.dto.request.SendNotificationRequest;
import ma.ensaj.covoiturage.notificationservice.dto.response.NotificationResponse;
import ma.ensaj.covoiturage.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // App mobile enregistre son token FCM au démarrage
    @PostMapping("/register-token")
    public ResponseEntity<Void> registerToken(
            @Valid @RequestBody RegisterTokenRequest request,
            @RequestHeader("X-User-Id") String userId) {
        notificationService.registerToken(
                UUID.fromString(userId), request);
        return ResponseEntity.ok().build();
    }

    // App mobile supprime son token FCM à la déconnexion
    @DeleteMapping("/remove-token")
    public ResponseEntity<Void> removeToken(
            @RequestParam String fcmToken) {
        notificationService.removeToken(fcmToken);
        return ResponseEntity.ok().build();
    }

    // Envoyer une notification (appelé par les autres services)
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(
                notificationService.sendNotification(request));
    }

    // Notification booking confirmé
    @PostMapping("/booking-confirmed")
    public ResponseEntity<Void> notifyBookingConfirmed(
            @RequestParam UUID passengerId,
            @RequestParam UUID driverId,
            @RequestParam String driverName,
            @RequestParam UUID bookingId) {
        notificationService.notifyBookingConfirmed(
                passengerId, driverId, driverName, bookingId);
        return ResponseEntity.ok().build();
    }

    // Notification booking terminé
    @PostMapping("/booking-completed")
    public ResponseEntity<Void> notifyBookingCompleted(
            @RequestParam UUID passengerId,
            @RequestParam UUID driverId,
            @RequestParam UUID bookingId) {
        notificationService.notifyBookingCompleted(
                passengerId, driverId, bookingId);
        return ResponseEntity.ok().build();
    }
}