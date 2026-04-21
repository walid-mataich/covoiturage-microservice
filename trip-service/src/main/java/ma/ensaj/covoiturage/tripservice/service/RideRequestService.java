package ma.ensaj.covoiturage.tripservice.service;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.tripservice.dto.request.RideRequestRequest;
import ma.ensaj.covoiturage.tripservice.dto.response.RideRequestResponse;
import ma.ensaj.covoiturage.tripservice.entity.RideRequest;
import ma.ensaj.covoiturage.tripservice.entity.enums.RideRequestStatus;
import ma.ensaj.covoiturage.tripservice.repository.RideRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    // Passager publie une demande de trajet
    public RideRequestResponse createRideRequest(
            RideRequestRequest request, UUID passengerId) {
        RideRequest rideRequest = RideRequest.builder()
                .passengerId(passengerId)
                .departureCity(request.getDepartureCity())
                .departureAddress(request.getDepartureAddress())
                .departureLat(request.getDepartureLat())
                .departureLng(request.getDepartureLng())
                .destinationCity(request.getDestinationCity())
                .destinationAddress(request.getDestinationAddress())
                .destinationLat(request.getDestinationLat())
                .destinationLng(request.getDestinationLng())
                .desiredDepartureTime(request.getDesiredDepartureTime())
                .seatsNeeded(request.getSeatsNeeded())
                .maxBudget(request.getMaxBudget())
                .notes(request.getNotes())
                .status(RideRequestStatus.PENDING)
                .build();

        return mapToResponse(rideRequestRepository.save(rideRequest));
    }

    // Driver consulte les demandes proches de lui
    public List<RideRequestResponse> searchNearbyRequests(
            Double lat, Double lng, Double radiusKm) {
        return rideRequestRepository
                .findNearbyRequests(lat, lng, radiusKm, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Recherche par ville
    public List<RideRequestResponse> searchByCity(
            String departureCity, String destinationCity) {
        return rideRequestRepository
                .findByDepartureCityIgnoreCaseAndDestinationCityIgnoreCaseAndStatus(
                        departureCity,
                        destinationCity,
                        RideRequestStatus.PENDING
                )
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Toutes les demandes d'un passager
    public List<RideRequestResponse> getPassengerRequests(UUID passengerId) {
        return rideRequestRepository
                .findByPassengerIdOrderByCreatedAtDesc(passengerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Détail d'une demande
    public RideRequestResponse getById(UUID id) {
        return mapToResponse(rideRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée")));
    }

    // Annuler une demande
    public RideRequestResponse cancelRequest(UUID id, UUID passengerId) {
        RideRequest request = rideRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (!request.getPassengerId().equals(passengerId)) {
            throw new RuntimeException("Action non autorisée");
        }

        request.setStatus(RideRequestStatus.CANCELLED);
        return mapToResponse(rideRequestRepository.save(request));
    }

    public RideRequestResponse mapToResponse(RideRequest r) {
        return RideRequestResponse.builder()
                .id(r.getId())
                .passengerId(r.getPassengerId())
                .departureCity(r.getDepartureCity())
                .departureAddress(r.getDepartureAddress())
                .departureLat(r.getDepartureLat())
                .departureLng(r.getDepartureLng())
                .destinationCity(r.getDestinationCity())
                .destinationAddress(r.getDestinationAddress())
                .destinationLat(r.getDestinationLat())
                .destinationLng(r.getDestinationLng())
                .desiredDepartureTime(r.getDesiredDepartureTime())
                .seatsNeeded(r.getSeatsNeeded())
                .maxBudget(r.getMaxBudget())
                .notes(r.getNotes())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}