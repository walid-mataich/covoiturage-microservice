package ma.ensaj.covoiturage.messagingservice.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class WebSocketChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null &&
                StompCommand.CONNECT.equals(accessor.getCommand())) {

            // Lire X-User-Id depuis les headers STOMP
            String userId = accessor.getFirstNativeHeader("X-User-Id");

            System.out.println("=== WebSocket CONNECT ===");
            System.out.println("X-User-Id : " + userId);

            if (userId != null) {
                // Stocker dans les attributs de session
                accessor.getSessionAttributes().put("userId", userId);

                // Définir le Principal de la session
                accessor.setUser(() -> userId);
            }
        }

        return message;
    }
}