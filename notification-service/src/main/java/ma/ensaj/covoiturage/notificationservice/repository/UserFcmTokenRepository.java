package ma.ensaj.covoiturage.notificationservice.repository;

import ma.ensaj.covoiturage.notificationservice.entity.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFcmTokenRepository
        extends JpaRepository<UserFcmToken, UUID> {

    // Tous les tokens d'un utilisateur (plusieurs appareils)
    List<UserFcmToken> findByUserId(UUID userId);

    // Trouver un token existant
    Optional<UserFcmToken> findByFcmToken(String fcmToken);

    // Supprimer les tokens d'un utilisateur
    void deleteByUserId(UUID userId);

    // Supprimer un token spécifique
    void deleteByFcmToken(String fcmToken);
}