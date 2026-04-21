package ma.ensaj.covoiturage.messagingservice.service;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.messagingservice.client.UserServiceClient;
import ma.ensaj.covoiturage.messagingservice.dto.external.UserResponse;
import ma.ensaj.covoiturage.messagingservice.dto.request.MessageRequest;
import ma.ensaj.covoiturage.messagingservice.dto.response.MessageResponse;
import ma.ensaj.covoiturage.messagingservice.entity.Message;
import ma.ensaj.covoiturage.messagingservice.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserServiceClient userServiceClient; // ← injecté

    public MessageResponse saveMessage(MessageRequest request, UUID senderId) {
        Message message = Message.builder()
                .conversationId(request.getConversationId())
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .isRead(false)
                .build();

        return mapToResponse(messageRepository.save(message));
    }

    public List<MessageResponse> getConversation(UUID conversationId) {
        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getUnreadMessages(UUID userId) {
        return messageRepository
                .findByReceiverIdAndIsReadFalse(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public long countUnread(UUID userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    public void markAsRead(UUID conversationId, UUID userId) {
        messageRepository.markAllAsRead(conversationId, userId);
    }

    private MessageResponse mapToResponse(Message message) {

        // Appel user-service pour enrichir le message
        UserResponse sender   = userServiceClient.getUser(message.getSenderId());
        UserResponse receiver = userServiceClient.getUser(message.getReceiverId());

        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .senderName(sender.getFirstName() + " " + sender.getLastName())
                .senderPhoto(sender.getProfilePicture())
                .receiverId(message.getReceiverId())
                .receiverName(receiver.getFirstName() + " " + receiver.getLastName())
                .receiverPhoto(receiver.getProfilePicture())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}