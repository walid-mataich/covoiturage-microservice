package ma.ensaj.covoiturage.negotiationservice.service;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.negotiationservice.client.BookingServiceClient;
import ma.ensaj.covoiturage.negotiationservice.client.TripServiceClient;
import ma.ensaj.covoiturage.negotiationservice.dto.external.BookingCreatedResponse;
import ma.ensaj.covoiturage.negotiationservice.dto.external.TripOfferResponse;
import ma.ensaj.covoiturage.negotiationservice.dto.request.CounterOfferRequest;
import ma.ensaj.covoiturage.negotiationservice.dto.request.StartNegotiationRequest;
import ma.ensaj.covoiturage.negotiationservice.dto.response.NegotiationResponse;
import ma.ensaj.covoiturage.negotiationservice.entity.Negotiation;
import ma.ensaj.covoiturage.negotiationservice.entity.NegotiationOffer;
import ma.ensaj.covoiturage.negotiationservice.entity.enums.NegotiationStatus;
import ma.ensaj.covoiturage.negotiationservice.entity.enums.OfferSide;
import ma.ensaj.covoiturage.negotiationservice.repository.NegotiationOfferRepository;
import ma.ensaj.covoiturage.negotiationservice.repository.NegotiationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final NegotiationOfferRepository negotiationOfferRepository;
    private final TripServiceClient tripServiceClient;
    private final BookingServiceClient bookingServiceClient;

    // Passager ouvre une négociation sur un TripOffer
    public NegotiationResponse startNegotiation(
            StartNegotiationRequest request, UUID passengerId) {

        // 1. Récupérer les détails du trajet depuis trip-service
        TripOfferResponse trip = tripServiceClient
                .getTripOffer(request.getTripId());

        // 2. Vérifier que le trajet est négociable
        if (!Boolean.TRUE.equals(trip.getIsPriceNegotiable())) {
            throw new RuntimeException(
                    "Ce trajet n'accepte pas la négociation de prix");
        }

        if (!"OPEN".equals(trip.getStatus())) {
            throw new RuntimeException("Ce trajet n'est plus disponible");
        }

        if (trip.getAvailableSeats() < request.getSeatsNeeded()) {
            throw new RuntimeException(
                    "Pas assez de places. Disponibles : "
                            + trip.getAvailableSeats());
        }

        // 3. Vérifier qu'une négociation n'est pas déjà ouverte
        negotiationRepository
                .findByDriverIdAndPassengerIdAndTripIdAndStatus(
                        trip.getDriverId(),
                        passengerId,
                        request.getTripId(),
                        NegotiationStatus.OPEN)
                .ifPresent(n -> {
                    throw new RuntimeException(
                            "Une négociation est déjà en cours sur ce trajet");
                });

        // 4. Créer la négociation
        Negotiation negotiation = Negotiation.builder()
                .driverId(trip.getDriverId())   // ← depuis trip-service
                .passengerId(passengerId)
                .tripId(request.getTripId())
                .initialPrice(trip.getPricePerSeat()) // ← prix original
                .seatsNeeded(request.getSeatsNeeded())
                .status(NegotiationStatus.OPEN)
                .build();

        negotiation = negotiationRepository.save(negotiation);

        // 5. Ajouter la première offre du passager
        NegotiationOffer firstOffer = NegotiationOffer.builder()
                .negotiation(negotiation)
                .side(OfferSide.PASSENGER)
                .submittedBy(passengerId)
                .proposedPrice(request.getProposedPrice())
                .message(request.getMessage())
                .build();

        negotiationOfferRepository.save(firstOffer);

        return mapToResponse(negotiation,
                List.of(firstOffer));
    }

    // Driver soumet une contre-offre
    public NegotiationResponse driverCounterOffer(
            UUID negotiationId,
            CounterOfferRequest request,
            UUID driverId) {

        Negotiation negotiation = getNegotiationAndValidate(
                negotiationId, NegotiationStatus.OPEN);

        if (!negotiation.getDriverId().equals(driverId)) {
            throw new RuntimeException("Action non autorisée");
        }

        NegotiationOffer offer = NegotiationOffer.builder()
                .negotiation(negotiation)
                .side(OfferSide.DRIVER)
                .submittedBy(driverId)
                .proposedPrice(request.getProposedPrice())
                .message(request.getMessage())
                .build();

        negotiationOfferRepository.save(offer);

        return mapToResponse(negotiation,
                getOffers(negotiationId));
    }

    // Passager soumet une contre-offre
    public NegotiationResponse passengerCounterOffer(
            UUID negotiationId,
            CounterOfferRequest request,
            UUID passengerId) {

        Negotiation negotiation = getNegotiationAndValidate(
                negotiationId, NegotiationStatus.OPEN);

        if (!negotiation.getPassengerId().equals(passengerId)) {
            throw new RuntimeException("Action non autorisée");
        }

        NegotiationOffer offer = NegotiationOffer.builder()
                .negotiation(negotiation)
                .side(OfferSide.PASSENGER)
                .submittedBy(passengerId)
                .proposedPrice(request.getProposedPrice())
                .message(request.getMessage())
                .build();

        negotiationOfferRepository.save(offer);

        return mapToResponse(negotiation,
                getOffers(negotiationId));
    }

    // Dans driverAccepts()
    public NegotiationResponse driverAccepts(UUID negotiationId, UUID driverId) {

        Negotiation negotiation = getNegotiationAndValidate(
                negotiationId, NegotiationStatus.OPEN);

        if (!negotiation.getDriverId().equals(driverId)) {
            throw new RuntimeException("Action non autorisée");
        }

        List<NegotiationOffer> offers = getOffers(negotiationId);
        NegotiationOffer lastPassengerOffer = offers.stream()
                .filter(o -> o.getSide() == OfferSide.PASSENGER)
                .reduce((first, second) -> second)
                .orElseThrow(() ->
                        new RuntimeException("Aucune offre passager trouvée"));

        Double agreedPrice = lastPassengerOffer.getProposedPrice();

        // Log pour vérifier
        System.out.println("=== driverAccepts ===");
        System.out.println("negotiation.driverId   : " + negotiation.getDriverId());
        System.out.println("negotiation.passengerId: " + negotiation.getPassengerId());
        System.out.println("agreedPrice            : " + agreedPrice);

        BookingCreatedResponse booking = bookingServiceClient.createBooking(
                negotiation.getTripId(),
                negotiation.getPassengerId(),
                negotiation.getDriverId(),   // ← driverId explicite
                negotiation.getSeatsNeeded(),
                agreedPrice
        );

        negotiation.setStatus(NegotiationStatus.AGREED);
        negotiation.setAgreedPrice(agreedPrice);
        negotiation.setAgreedAt(LocalDateTime.now());
        negotiation.setBookingId(booking.getId());
        negotiationRepository.save(negotiation);

        return mapToResponse(negotiation, offers);
    }

    // Dans passengerAccepts() — même fix
    public NegotiationResponse passengerAccepts(UUID negotiationId, UUID passengerId) {

        Negotiation negotiation = getNegotiationAndValidate(
                negotiationId, NegotiationStatus.OPEN);

        if (!negotiation.getPassengerId().equals(passengerId)) {
            throw new RuntimeException("Action non autorisée");
        }

        List<NegotiationOffer> offers = getOffers(negotiationId);
        NegotiationOffer lastDriverOffer = offers.stream()
                .filter(o -> o.getSide() == OfferSide.DRIVER)
                .reduce((first, second) -> second)
                .orElseThrow(() ->
                        new RuntimeException("Aucune offre driver trouvée"));

        Double agreedPrice = lastDriverOffer.getProposedPrice();

        BookingCreatedResponse booking = bookingServiceClient.createBooking(
                negotiation.getTripId(),
                negotiation.getPassengerId(),
                negotiation.getDriverId(),   // ← driverId explicite
                negotiation.getSeatsNeeded(),
                agreedPrice
        );

        negotiation.setStatus(NegotiationStatus.AGREED);
        negotiation.setAgreedPrice(agreedPrice);
        negotiation.setAgreedAt(LocalDateTime.now());
        negotiation.setBookingId(booking.getId());
        negotiationRepository.save(negotiation);

        return mapToResponse(negotiation, offers);
    }

    // Driver rejette la négociation
    public NegotiationResponse driverRejects(
            UUID negotiationId, UUID driverId) {

        Negotiation negotiation = getNegotiationAndValidate(
                negotiationId, NegotiationStatus.OPEN);

        if (!negotiation.getDriverId().equals(driverId)) {
            throw new RuntimeException("Action non autorisée");
        }

        negotiation.setStatus(NegotiationStatus.REJECTED);
        negotiationRepository.save(negotiation);

        return mapToResponse(negotiation, getOffers(negotiationId));
    }

    // Passager rejette la négociation
    public NegotiationResponse passengerRejects(
            UUID negotiationId, UUID passengerId) {

        Negotiation negotiation = getNegotiationAndValidate(
                negotiationId, NegotiationStatus.OPEN);

        if (!negotiation.getPassengerId().equals(passengerId)) {
            throw new RuntimeException("Action non autorisée");
        }

        negotiation.setStatus(NegotiationStatus.REJECTED);
        negotiationRepository.save(negotiation);

        return mapToResponse(negotiation, getOffers(negotiationId));
    }

    // Voir le détail d'une négociation avec historique
    public NegotiationResponse getNegotiation(UUID negotiationId) {
        Negotiation negotiation = negotiationRepository.findById(negotiationId)
                .orElseThrow(() ->
                        new RuntimeException("Négociation non trouvée"));
        return mapToResponse(negotiation, getOffers(negotiationId));
    }

    // Toutes les négociations du passager
    public List<NegotiationResponse> getPassengerNegotiations(
            UUID passengerId) {
        return negotiationRepository
                .findByPassengerIdOrderByCreatedAtDesc(passengerId)
                .stream()
                .map(n -> mapToResponse(n, getOffers(n.getId())))
                .collect(Collectors.toList());
    }

    // Toutes les négociations du driver
    public List<NegotiationResponse> getDriverNegotiations(UUID driverId) {
        return negotiationRepository
                .findByDriverIdOrderByCreatedAtDesc(driverId)
                .stream()
                .map(n -> mapToResponse(n, getOffers(n.getId())))
                .collect(Collectors.toList());
    }

    // Helpers
    private Negotiation getNegotiationAndValidate(
            UUID id, NegotiationStatus expectedStatus) {
        Negotiation negotiation = negotiationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Négociation non trouvée"));
        if (negotiation.getStatus() != expectedStatus) {
            throw new RuntimeException(
                    "Cette négociation n'est plus " + expectedStatus);
        }
        return negotiation;
    }

    private List<NegotiationOffer> getOffers(UUID negotiationId) {
        return negotiationOfferRepository
                .findByNegotiationIdOrderByCreatedAtAsc(negotiationId);
    }

    private NegotiationResponse mapToResponse(
            Negotiation negotiation, List<NegotiationOffer> offers) {
        return NegotiationResponse.builder()
                .id(negotiation.getId())
                .driverId(negotiation.getDriverId())
                .passengerId(negotiation.getPassengerId())
                .tripId(negotiation.getTripId())
                .initialPrice(negotiation.getInitialPrice())
                .agreedPrice(negotiation.getAgreedPrice())
                .seatsNeeded(negotiation.getSeatsNeeded())
                .status(negotiation.getStatus())
                .bookingId(negotiation.getBookingId())
                .createdAt(negotiation.getCreatedAt())
                .agreedAt(negotiation.getAgreedAt())
                .offers(offers.stream()
                        .map(o -> NegotiationResponse.OfferDetail.builder()
                                .id(o.getId())
                                .side(o.getSide())
                                .submittedBy(o.getSubmittedBy())
                                .proposedPrice(o.getProposedPrice())
                                .message(o.getMessage())
                                .createdAt(o.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}