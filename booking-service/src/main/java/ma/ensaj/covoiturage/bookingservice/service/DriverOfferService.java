package ma.ensaj.covoiturage.bookingservice.service;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.bookingservice.client.TripServiceClient;
import ma.ensaj.covoiturage.bookingservice.dto.external.RideRequestResponse;
import ma.ensaj.covoiturage.bookingservice.dto.request.BookingRequest;
import ma.ensaj.covoiturage.bookingservice.dto.request.DriverOfferRequest;
import ma.ensaj.covoiturage.bookingservice.dto.response.BookingResponse;
import ma.ensaj.covoiturage.bookingservice.dto.response.DriverOfferResponse;
import ma.ensaj.covoiturage.bookingservice.entity.DriverOffer;
import ma.ensaj.covoiturage.bookingservice.entity.enums.BookingType;
import ma.ensaj.covoiturage.bookingservice.entity.enums.DriverOfferStatus;
import ma.ensaj.covoiturage.bookingservice.repository.DriverOfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverOfferService {

    private final DriverOfferRepository driverOfferRepository;
    private final BookingService bookingService;
    private final TripServiceClient tripServiceClient;

    public DriverOfferResponse submitOffer(
            DriverOfferRequest request, UUID driverId) {

        // 1. Récupérer la RideRequest depuis trip-service
        //    → on obtient passengerId, departureCity, etc.
        //    → le driver n'a pas besoin de les envoyer
        RideRequestResponse rideRequest = tripServiceClient
                .getRideRequest(request.getRideRequestId());

        // 2. Vérifier que la demande est encore en attente
        if (!"PENDING".equals(rideRequest.getStatus())) {
            throw new RuntimeException(
                    "Cette demande n'est plus disponible");
        }

        // 3. Vérifier que le driver n'a pas déjà soumis une offre
        boolean alreadyOffered = driverOfferRepository
                .existsByDriverIdAndRideRequestIdAndStatusNot(
                        driverId,
                        request.getRideRequestId(),
                        DriverOfferStatus.REJECTED
                );

        if (alreadyOffered) {
            throw new RuntimeException(
                    "Vous avez déjà soumis une offre pour cette demande");
        }

        // 4. Vérifier que le prix proposé respecte le budget max
        if (rideRequest.getMaxBudget() != null &&
                request.getProposedPrice() > rideRequest.getMaxBudget()) {
            throw new RuntimeException(
                    "Votre prix (" + request.getProposedPrice() +
                            " DH) dépasse le budget max du passager (" +
                            rideRequest.getMaxBudget() + " DH)"
            );
        }

        // 5. Construire l'offre avec les données du trip-service
        //    passengerId vient de rideRequest, pas du client
        DriverOffer offer = DriverOffer.builder()
                .driverId(driverId)
                .rideRequestId(request.getRideRequestId())
                .passengerId(rideRequest.getPassengerId()) // ← trip-service
                .proposedPrice(request.getProposedPrice())
                .proposedDepartureTime(request.getProposedDepartureTime())
                .message(request.getMessage())
                .status(DriverOfferStatus.PENDING)
                .build();

        return mapToResponse(driverOfferRepository.save(offer));
    }

    public BookingResponse acceptOffer(UUID offerId, UUID passengerId) {

        DriverOffer offer = getOfferAndValidatePassenger(offerId, passengerId);

        if (offer.getStatus() != DriverOfferStatus.PENDING) {
            throw new RuntimeException("Cette offre n'est plus disponible");
        }

        RideRequestResponse rideRequest = tripServiceClient
                .getRideRequest(offer.getRideRequestId());

        // Accepter cette offre
        offer.setStatus(DriverOfferStatus.ACCEPTED);
        driverOfferRepository.save(offer);

        // Rejeter les autres offres automatiquement
        driverOfferRepository
                .findByRideRequestIdAndStatus(
                        offer.getRideRequestId(),
                        DriverOfferStatus.PENDING)
                .stream()
                .filter(o -> !o.getId().equals(offerId))
                .forEach(o -> {
                    o.setStatus(DriverOfferStatus.REJECTED);
                    driverOfferRepository.save(o);
                });

        // Créer le booking avec le bon driverId
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setTripId(offer.getRideRequestId());
        bookingRequest.setBookingType(BookingType.FROM_RIDE_REQUEST);
        bookingRequest.setSeatsBooked(rideRequest.getSeatsNeeded());
        bookingRequest.setDriverId(offer.getDriverId());       // ← Younes ✓
        bookingRequest.setProposedPrice(offer.getProposedPrice()); // ← prix négocié ✓

        // createBooking avec passengerId = Mehdi
        BookingResponse booking = bookingService
                .createBooking(bookingRequest, passengerId);

        // confirmBooking avec driverId = Younes
        // maintenant booking.driverId == offer.driverId ✓
        return bookingService.confirmBooking(
                booking.getId(), offer.getDriverId());
    }

    public DriverOfferResponse rejectOffer(UUID offerId, UUID passengerId) {
        DriverOffer offer = getOfferAndValidatePassenger(offerId, passengerId);
        offer.setStatus(DriverOfferStatus.REJECTED);
        return mapToResponse(driverOfferRepository.save(offer));
    }

    public List<DriverOfferResponse> getOffersForRequest(UUID rideRequestId) {

        // Enrichir chaque offre avec les détails de la RideRequest
        RideRequestResponse rideRequest = tripServiceClient
                .getRideRequest(rideRequestId);

        return driverOfferRepository
                .findByRideRequestIdAndStatus(
                        rideRequestId, DriverOfferStatus.PENDING)
                .stream()
                .map(offer -> mapToResponseWithDetails(offer, rideRequest))
                .collect(Collectors.toList());
    }

    public List<DriverOfferResponse> getDriverOffers(UUID driverId) {
        return driverOfferRepository
                .findByDriverIdOrderByCreatedAtDesc(driverId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DriverOffer getOfferAndValidatePassenger(
            UUID offerId, UUID passengerId) {

        DriverOffer offer = driverOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        // Log pour déboguer
        System.out.println("=== DEBUG acceptOffer ===");
        System.out.println("offerId         : " + offerId);
        System.out.println("offer.passengerId: " + offer.getPassengerId());
        System.out.println("X-User-Id reçu  : " + passengerId);
        System.out.println("match           : " + offer.getPassengerId().equals(passengerId));

        if (!offer.getPassengerId().equals(passengerId)) {
            throw new RuntimeException(
                    "Action non autorisée — " +
                            "offer.passengerId=" + offer.getPassengerId() +
                            " xUserId=" + passengerId
            );
        }

        return offer;
    }

    private DriverOfferResponse mapToResponse(DriverOffer offer) {
        return DriverOfferResponse.builder()
                .id(offer.getId())
                .driverId(offer.getDriverId())
                .rideRequestId(offer.getRideRequestId())
                .passengerId(offer.getPassengerId())
                .proposedPrice(offer.getProposedPrice())
                .proposedDepartureTime(offer.getProposedDepartureTime())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .createdAt(offer.getCreatedAt())
                .build();
    }

    // Version enrichie avec les détails du trajet
    private DriverOfferResponse mapToResponseWithDetails(
            DriverOffer offer, RideRequestResponse rideRequest) {
        return DriverOfferResponse.builder()
                .id(offer.getId())
                .driverId(offer.getDriverId())
                .rideRequestId(offer.getRideRequestId())
                .passengerId(offer.getPassengerId())
                .proposedPrice(offer.getProposedPrice())
                .proposedDepartureTime(offer.getProposedDepartureTime())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .createdAt(offer.getCreatedAt())
                // Infos du trajet enrichies depuis trip-service
                .departureCity(rideRequest.getDepartureCity())
                .destinationCity(rideRequest.getDestinationCity())
                .seatsNeeded(rideRequest.getSeatsNeeded())
                .maxBudget(rideRequest.getMaxBudget())
                .build();
    }
}