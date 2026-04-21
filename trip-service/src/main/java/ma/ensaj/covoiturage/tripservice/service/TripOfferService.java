package ma.ensaj.covoiturage.tripservice.service;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.tripservice.dto.request.TripOfferRequest;
import ma.ensaj.covoiturage.tripservice.dto.response.TripOfferResponse;
import ma.ensaj.covoiturage.tripservice.entity.TripOffer;
import ma.ensaj.covoiturage.tripservice.entity.enums.TripStatus;
import ma.ensaj.covoiturage.tripservice.repository.TripOfferRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripOfferService {

    private final TripOfferRepository tripOfferRepository;

    // Driver publie un trajet
    public TripOfferResponse createTripOffer(TripOfferRequest request, UUID driverId) {
        TripOffer trip = TripOffer.builder()
                .driverId(driverId)
                .departureCity(request.getDepartureCity())
                .departureAddress(request.getDepartureAddress())
                .departureLat(request.getDepartureLat())
                .departureLng(request.getDepartureLng())
                .destinationCity(request.getDestinationCity())
                .destinationAddress(request.getDestinationAddress())
                .destinationLat(request.getDestinationLat())
                .destinationLng(request.getDestinationLng())
                .departureTime(request.getDepartureTime())
                .availableSeats(request.getAvailableSeats())
                .pricePerSeat(request.getPricePerSeat())
                .isPriceNegotiable(request.getIsPriceNegotiable())
                .description(request.getDescription())
                .vehicleBrand(request.getVehicleBrand())
                .vehicleModel(request.getVehicleModel())
                .vehiclePlate(request.getVehiclePlate())
                .status(TripStatus.OPEN)
                .build();

        return mapToResponse(tripOfferRepository.save(trip));
    }

    // Recherche de trajets par ville
    public List<TripOfferResponse> searchTrips(
            String departureCity, String destinationCity) {
        return tripOfferRepository
                .findByDepartureCityIgnoreCaseAndDestinationCityIgnoreCaseAndStatusAndDepartureTimeAfter(
                        departureCity,
                        destinationCity,
                        TripStatus.OPEN,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Recherche de trajets proches (géolocalisation)
    public List<TripOfferResponse> searchNearbyTrips(
            Double lat, Double lng, Double radiusKm) {
        return tripOfferRepository
                .findNearbyTrips(lat, lng, radiusKm, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Tous les trajets d'un driver
    public List<TripOfferResponse> getDriverTrips(UUID driverId) {
        return tripOfferRepository
                .findByDriverIdOrderByCreatedAtDesc(driverId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Détail d'un trajet
    public TripOfferResponse getTripById(UUID id) {
        TripOffer trip = tripOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));
        return mapToResponse(trip);
    }

    // Annuler un trajet
    public TripOfferResponse cancelTrip(UUID id, UUID driverId) {
        TripOffer trip = tripOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        if (!trip.getDriverId().equals(driverId)) {
            throw new RuntimeException("Action non autorisée");
        }

        trip.setStatus(TripStatus.CANCELLED);
        return mapToResponse(tripOfferRepository.save(trip));
    }

    public TripOfferResponse mapToResponse(TripOffer trip) {
        return TripOfferResponse.builder()
                .id(trip.getId())
                .driverId(trip.getDriverId())
                .departureCity(trip.getDepartureCity())
                .departureAddress(trip.getDepartureAddress())
                .departureLat(trip.getDepartureLat())
                .departureLng(trip.getDepartureLng())
                .destinationCity(trip.getDestinationCity())
                .destinationAddress(trip.getDestinationAddress())
                .destinationLat(trip.getDestinationLat())
                .destinationLng(trip.getDestinationLng())
                .departureTime(trip.getDepartureTime())
                .availableSeats(trip.getAvailableSeats())
                .pricePerSeat(trip.getPricePerSeat())
                .isPriceNegotiable(trip.getIsPriceNegotiable())
                .description(trip.getDescription())
                .vehicleBrand(trip.getVehicleBrand())
                .vehicleModel(trip.getVehicleModel())
                .vehiclePlate(trip.getVehiclePlate())
                .status(trip.getStatus())
                .createdAt(trip.getCreatedAt())
                .build();
    }
}