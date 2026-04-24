package ma.ensaj.covoiturage.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_fcm_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // L'utilisateur propriétaire du token
    @Column(nullable = false)
    private UUID userId;

    // Le token FCM de l'appareil mobile
    @Column(nullable = false, unique = true)
    private String fcmToken;

    // Type d'appareil
    private String deviceType; // ANDROID ou IOS

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}