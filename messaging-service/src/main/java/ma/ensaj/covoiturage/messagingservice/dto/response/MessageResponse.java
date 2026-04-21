package ma.ensaj.covoiturage.messagingservice.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private UUID id;
    private UUID conversationId;

    // Sender
    private UUID senderId;
    private String senderName;      // ← enrichi depuis user-service
    private String senderPhoto;     // ← enrichi depuis user-service

    // Receiver
    private UUID receiverId;
    private String receiverName;    // ← enrichi depuis user-service
    private String receiverPhoto;   // ← enrichi depuis user-service

    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}