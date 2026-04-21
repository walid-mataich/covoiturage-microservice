package ma.ensaj.covoiturage.messagingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // La conversation est identifiée par bookingId ou negotiationId
    @Column(nullable = false)
    private UUID conversationId;

    // Qui a envoyé le message
    @Column(nullable = false)
    private UUID senderId;

    // Qui doit recevoir le message
    @Column(nullable = false)
    private UUID receiverId;

    @Column(nullable = false, length = 1000)
    private String content;

    // Message lu ou pas
    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}