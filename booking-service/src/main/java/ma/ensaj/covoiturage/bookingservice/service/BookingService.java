package ma.ensaj.covoiturage.bookingservice.service;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.bookingservice.client.TripServiceClient;
import ma.ensaj.covoiturage.bookingservice.dto.external.TripOfferResponse;
import ma.ensaj.covoiturage.bookingservice.dto.external.RideRequestResponse;
import ma.ensaj.covoiturage.bookingservice.dto.request.BookingRequest;
import ma.ensaj.covoiturage.bookingservice.dto.response.BookingResponse;
import ma.ensaj.covoiturage.bookingservice.entity.Booking;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingStatus;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingType;
import ma.ensaj.covoiturage.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripServiceClient tripServiceClient;

    public BookingResponse createBooking(BookingRequest request, UUID passengerId) {

        System.out.println("=== DEBUG createBooking depuis negotiation ===");
        System.out.println("tripId       : " + request.getTripId());
        System.out.println("bookingType  : " + request.getBookingType());
        System.out.println("seatsBooked  : " + request.getSeatsBooked());
        System.out.println("proposedPrice: " + request.getProposedPrice());
        System.out.println("passengerId  : " + passengerId);
        System.out.println("driverId     : " + request.getDriverId());

        boolean alreadyBooked = bookingRepository
                .existsByPassengerIdAndTripIdAndStatusNot(
                        passengerId,
                        request.getTripId(),
                        BookingStatus.CANCELLED
                );

        if (alreadyBooked) {
            throw new RuntimeException("Vous avez déjà réservé ce trajet");
        }

        Booking booking;

        if (request.getBookingType() == BookingType.FROM_TRIP_OFFER) {

            TripOfferResponse trip = tripServiceClient
                    .getTripOffer(request.getTripId());

            if (!"OPEN".equals(trip.getStatus())) {
                throw new RuntimeException("Ce trajet n'est plus disponible");
            }

            if (trip.getAvailableSeats() < request.getSeatsBooked()) {
                throw new RuntimeException(
                        "Pas assez de places. Disponibles : "
                                + trip.getAvailableSeats());
            }

            // Prioriser le driverId passé (cas négociation)
            // sinon prendre celui du trip-service (cas réservation directe)
            UUID resolvedDriverId = request.getDriverId() != null
                    ? request.getDriverId()
                    : trip.getDriverId();

            booking = Booking.builder()
                    .passengerId(passengerId)
                    .driverId(resolvedDriverId)  // ← résolu correctement
                    .tripId(trip.getId())
                    .bookingType(BookingType.FROM_TRIP_OFFER)
                    .departureCity(trip.getDepartureCity())
                    .destinationCity(trip.getDestinationCity())
                    .departureTime(trip.getDepartureTime())
                    .seatsBooked(request.getSeatsBooked())
                    .totalPrice(request.getProposedPrice() != null
                            ? request.getProposedPrice()
                            : trip.getPricePerSeat() * request.getSeatsBooked())
                    .status(BookingStatus.PENDING)
                    .build();
        } else {
            // FROM_RIDE_REQUEST
            RideRequestResponse rideRequest = tripServiceClient
                    .getRideRequest(request.getTripId());

            if (!"PENDING".equals(rideRequest.getStatus())) {
                throw new RuntimeException("Cette demande n'est plus disponible");
            }

            booking = Booking.builder()
                    .passengerId(rideRequest.getPassengerId()) // ← Mehdi (depuis trip-service)
                    .driverId(request.getDriverId())            // ← Younes (depuis l'offre) ✓
                    .tripId(rideRequest.getId())
                    .bookingType(BookingType.FROM_RIDE_REQUEST)
                    .departureCity(rideRequest.getDepartureCity())
                    .destinationCity(rideRequest.getDestinationCity())
                    .departureTime(rideRequest.getDesiredDepartureTime())
                    .seatsBooked(rideRequest.getSeatsNeeded())
                    .totalPrice(request.getProposedPrice() != null
                            ? request.getProposedPrice()
                            : rideRequest.getMaxBudget())
                    .status(BookingStatus.PENDING)
                    .build();
        }

        return mapToResponse(bookingRepository.save(booking));
    }

    // confirmBooking, rejectBooking, startTrip, completeTrip,
    // cancelBooking, getPassengerBookings, getDriverBookings,
    // getById, getPendingDriverBookings — inchangés
    public BookingResponse confirmBooking(UUID bookingId, UUID driverId) {
        Booking booking = getBookingAndValidateDriver(bookingId, driverId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Seules les réservations en attente peuvent être confirmées");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());
        return mapToResponse(bookingRepository.save(booking));
    }

    public BookingResponse rejectBooking(UUID bookingId, UUID driverId, String reason) {
        Booking booking = getBookingAndValidateDriver(bookingId, driverId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Seules les réservations en attente peuvent être refusées");
        }
        booking.setStatus(BookingStatus.REJECTED);
        booking.setCancellationReason(reason);
        return mapToResponse(bookingRepository.save(booking));
    }

    public BookingResponse startTrip(UUID bookingId, UUID driverId) {
        Booking booking = getBookingAndValidateDriver(bookingId, driverId);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Le trajet doit être confirmé pour démarrer");
        }
        booking.setStatus(BookingStatus.IN_PROGRESS);
        return mapToResponse(bookingRepository.save(booking));
    }

    public BookingResponse completeTrip(UUID bookingId, UUID driverId) {
        Booking booking = getBookingAndValidateDriver(bookingId, driverId);
        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new RuntimeException("Le trajet doit être en cours pour être terminé");
        }
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());
        return mapToResponse(bookingRepository.save(booking));
    }

    public BookingResponse cancelBooking(UUID bookingId, UUID passengerId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        if (!booking.getPassengerId().equals(passengerId)) {
            throw new RuntimeException("Action non autorisée");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED ||
                booking.getStatus() == BookingStatus.IN_PROGRESS) {
            throw new RuntimeException("Impossible d'annuler un trajet terminé ou en cours");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        return mapToResponse(bookingRepository.save(booking));
    }

    public List<BookingResponse> getPassengerBookings(UUID passengerId) {
        return bookingRepository.findByPassengerIdOrderByCreatedAtDesc(passengerId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getDriverBookings(UUID driverId) {
        return bookingRepository.findByDriverIdOrderByCreatedAtDesc(driverId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public BookingResponse getById(UUID id) {
        return mapToResponse(bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée")));
    }

    public List<BookingResponse> getPendingDriverBookings(UUID driverId) {
        return bookingRepository.findByDriverIdAndStatus(driverId, BookingStatus.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private Booking getBookingAndValidateDriver(UUID bookingId, UUID driverId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        if (!booking.getDriverId().equals(driverId)) {
            throw new RuntimeException("Action non autorisée");
        }
        return booking;
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .passengerId(booking.getPassengerId())
                .driverId(booking.getDriverId())
                .tripId(booking.getTripId())
                .bookingType(booking.getBookingType())
                .departureCity(booking.getDepartureCity())
                .destinationCity(booking.getDestinationCity())
                .departureTime(booking.getDepartureTime())
                .seatsBooked(booking.getSeatsBooked())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .cancellationReason(booking.getCancellationReason())
                .createdAt(booking.getCreatedAt())
                .confirmedAt(booking.getConfirmedAt())
                .completedAt(booking.getCompletedAt())
                .build();
    }
}