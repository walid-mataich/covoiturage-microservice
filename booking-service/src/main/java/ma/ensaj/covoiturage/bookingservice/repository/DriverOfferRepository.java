package ma.ensaj.covoiturage.bookingservice.repository;

import ma.ensaj.covoiturage.bookingservice.entity.DriverOffer;
import ma.ensaj.covoiturage.bookingservice.entity.enums.DriverOfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DriverOfferRepository extends JpaRepository<DriverOffer, UUID> {

    // Toutes les offres reçues pour une RideRequest
    List<DriverOffer> findByRideRequestIdAndStatus(
            UUID rideRequestId, DriverOfferStatus status);

    // Toutes les offres soumises par un driver
    List<DriverOffer> findByDriverIdOrderByCreatedAtDesc(UUID driverId);

    // Toutes les offres reçues par un passager
    List<DriverOffer> findByPassengerIdOrderByCreatedAtDesc(UUID passengerId);

    // Vérifier si le driver a déjà soumis une offre pour cette demande
    boolean existsByDriverIdAndRideRequestIdAndStatusNot(
            UUID driverId, UUID rideRequestId, DriverOfferStatus status);
}