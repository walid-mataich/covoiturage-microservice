package ma.ensaj.covoiturage.messagingservice.repository;

import ma.ensaj.covoiturage.messagingservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    // Historique d'une conversation triée par date
    List<Message> findByConversationIdOrderByCreatedAtAsc(
            UUID conversationId);

    // Messages non lus pour un utilisateur
    List<Message> findByReceiverIdAndIsReadFalse(UUID receiverId);

    // Compter les messages non lus
    long countByReceiverIdAndIsReadFalse(UUID receiverId);

    // Marquer tous les messages d'une conversation comme lus
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true " +
            "WHERE m.conversationId = :convId " +
            "AND m.receiverId = :userId")
    void markAllAsRead(
            @Param("convId") UUID conversationId,
            @Param("userId") UUID userId);
}