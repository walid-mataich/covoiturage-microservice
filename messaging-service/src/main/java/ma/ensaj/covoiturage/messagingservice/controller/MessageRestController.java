package ma.ensaj.covoiturage.messagingservice.controller;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.messagingservice.dto.response.MessageResponse;
import ma.ensaj.covoiturage.messagingservice.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageRestController {

    private final MessageService messageService;

    // Historique d'une conversation
    // conversationId = bookingId ou negotiationId
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @PathVariable UUID conversationId) {
        return ResponseEntity.ok(
                messageService.getConversation(conversationId));
    }

    // Messages non lus de l'utilisateur connecté
    @GetMapping("/unread")
    public ResponseEntity<List<MessageResponse>> getUnread(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(
                messageService.getUnreadMessages(UUID.fromString(userId)));
    }

    // Compter les non lus
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> countUnread(
            @RequestHeader("X-User-Id") String userId) {
        long count = messageService.countUnread(UUID.fromString(userId));
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    // Marquer tous les messages d'une conversation comme lus
    @PatchMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID conversationId,
            @RequestHeader("X-User-Id") String userId) {
        messageService.markAsRead(conversationId, UUID.fromString(userId));
        return ResponseEntity.ok().build();
    }
}