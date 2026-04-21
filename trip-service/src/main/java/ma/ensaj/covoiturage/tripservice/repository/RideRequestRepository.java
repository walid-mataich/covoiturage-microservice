package ma.ensaj.covoiturage.tripservice.repository;

import ma.ensaj.covoiturage.tripservice.entity.RideRequest;
import ma.ensaj.covoiturage.tripservice.entity.enums.RideRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, UUID> {

    // Toutes les demandes d'un passager
    List<RideRequest> findByPassengerIdOrderByCreatedAtDesc(UUID passengerId);

    // Demandes en attente par ville
    List<RideRequest> findByDepartureCityIgnoreCaseAndDestinationCityIgnoreCaseAndStatus(
            String departureCity,
            String destinationCity,
            RideRequestStatus status
    );

    // Demandes proches géographiquement (pour les drivers)
    @Query("""
        SELECT r FROM RideRequest r
        WHERE r.status = 'PENDING'
        AND r.desiredDepartureTime > :now
        AND (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(r.departureLat)) *
                cos(radians(r.departureLng) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(r.departureLat))
            )
        ) <= :radiusKm
        ORDER BY r.desiredDepartureTime ASC
    """)
    List<RideRequest> findNearbyRequests(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusKm") Double radiusKm,
            @Param("now") LocalDateTime now
    );
}