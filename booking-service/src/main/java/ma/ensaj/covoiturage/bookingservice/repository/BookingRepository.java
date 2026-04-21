package ma.ensaj.covoiturage.bookingservice.repository;

import ma.ensaj.covoiturage.bookingservice.entity.Booking;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    // Toutes les réservations d'un passager
    List<Booking> findByPassengerIdOrderByCreatedAtDesc(UUID passengerId);

    // Toutes les réservations reçues par un driver
    List<Booking> findByDriverIdOrderByCreatedAtDesc(UUID driverId);

    // Réservations d'un trajet spécifique
    List<Booking> findByTripId(UUID tripId);

    // Réservations par statut pour un passager
    List<Booking> findByPassengerIdAndStatus(UUID passengerId, BookingStatus status);

    // Réservations par statut pour un driver
    List<Booking> findByDriverIdAndStatus(UUID driverId, BookingStatus status);

    // Vérifier si une réservation existe déjà
    boolean existsByPassengerIdAndTripIdAndStatusNot(
            UUID passengerId, UUID tripId, BookingStatus status);
}