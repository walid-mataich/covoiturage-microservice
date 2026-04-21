package ma.ensaj.covoiturage.ratingservice.repository;

import ma.ensaj.covoiturage.ratingservice.entity.Rating;
import ma.ensaj.covoiturage.ratingservice.entity.enums.RatedSide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    // Vérifier qu'une note n'a pas déjà été donnée pour ce booking
    boolean existsByBookingIdAndRaterId(UUID bookingId, UUID raterId);

    // Toutes les notes reçues par un utilisateur
    List<Rating> findByRatedIdOrderByCreatedAtDesc(UUID ratedId);

    // Notes reçues par un driver
    List<Rating> findByRatedIdAndRatedSide(UUID ratedId, RatedSide side);

    // Calculer la moyenne des notes d'un utilisateur
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.ratedId = :userId")
    Optional<Double> calculateAverageRating(@Param("userId") UUID userId);

    // Compter le nombre total de notes
    long countByRatedId(UUID ratedId);
}