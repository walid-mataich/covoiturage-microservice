package ma.ensaj.covoiturage.negotiationservice.repository;

import ma.ensaj.covoiturage.negotiationservice.entity.Negotiation;
import ma.ensaj.covoiturage.negotiationservice.entity.enums.NegotiationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NegotiationRepository extends JpaRepository<Negotiation, UUID> {

    // Trouver une négociation ouverte entre driver et passager sur un trajet
    Optional<Negotiation> findByDriverIdAndPassengerIdAndTripIdAndStatus(
            UUID driverId, UUID passengerId,
            UUID tripId, NegotiationStatus status);

    // Toutes les négociations d'un passager
    List<Negotiation> findByPassengerIdOrderByCreatedAtDesc(UUID passengerId);

    // Toutes les négociations d'un driver
    List<Negotiation> findByDriverIdOrderByCreatedAtDesc(UUID driverId);

    // Négociations ouvertes sur un trajet
    List<Negotiation> findByTripIdAndStatus(UUID tripId, NegotiationStatus status);
}