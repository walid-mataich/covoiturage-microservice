package ma.ensaj.covoiturage.tripservice.repository;

import ma.ensaj.covoiturage.tripservice.entity.TripOffer;
import ma.ensaj.covoiturage.tripservice.entity.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TripOfferRepository extends JpaRepository<TripOffer, UUID> {

    // Tous les trajets d'un driver
    List<TripOffer> findByDriverIdOrderByCreatedAtDesc(UUID driverId);

    // Recherche par ville de départ et destination
    List<TripOffer> findByDepartureCityIgnoreCaseAndDestinationCityIgnoreCaseAndStatusAndDepartureTimeAfter(
            String departureCity,
            String destinationCity,
            TripStatus status,
            LocalDateTime after
    );

    // Recherche par proximité géographique (rayon en km)
    @Query("""
        SELECT t FROM TripOffer t
        WHERE t.status = 'OPEN'
        AND t.departureTime > :now
        AND (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(t.departureLat)) *
                cos(radians(t.departureLng) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(t.departureLat))
            )
        ) <= :radiusKm
        ORDER BY t.departureTime ASC
    """)
    List<TripOffer> findNearbyTrips(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusKm") Double radiusKm,
            @Param("now") LocalDateTime now
    );

    // Trajets disponibles (OPEN) triés par date
    List<TripOffer> findByStatusOrderByDepartureTimeAsc(TripStatus status);
}