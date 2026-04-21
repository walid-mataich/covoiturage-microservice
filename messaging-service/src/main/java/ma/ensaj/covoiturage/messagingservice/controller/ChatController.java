package ma.ensaj.covoiturage.messagingservice.controller;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.messagingservice.dto.request.MessageRequest;
import ma.ensaj.covoiturage.messagingservice.dto.response.MessageResponse;
import ma.ensaj.covoiturage.messagingservice.service.MessageService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    // Le client envoie vers /app/chat
    // Le serveur redistribue vers /user/{receiverId}/queue/messages
    @MessageMapping("/chat")
    public void sendMessage(
            @Payload MessageRequest request,
            @Header("X-User-Id") String senderId) {

        // 1. Sauvegarder en base
        MessageResponse saved = messageService.saveMessage(
                request, UUID.fromString(senderId));

        // 2. Envoyer au destinataire en temps réel
        messagingTemplate.convertAndSendToUser(
                request.getReceiverId().toString(),
                "/queue/messages",
                saved
        );

        // 3. Renvoyer une confirmation à l'expéditeur
        messagingTemplate.convertAndSendToUser(
                senderId,
                "/queue/messages/sent",
                saved
        );
    }
}