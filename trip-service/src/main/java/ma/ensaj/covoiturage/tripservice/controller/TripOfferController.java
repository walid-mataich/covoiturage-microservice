package ma.ensaj.covoiturage.tripservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.tripservice.dto.request.TripOfferRequest;
import ma.ensaj.covoiturage.tripservice.dto.response.TripOfferResponse;
import ma.ensaj.covoiturage.tripservice.service.TripOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips/offers")
@RequiredArgsConstructor
public class TripOfferController {

    private final TripOfferService tripOfferService;

    // Driver publie un trajet
    // Le header X-User-Id est injecté automatiquement par le Gateway
    @PostMapping
    public ResponseEntity<TripOfferResponse> createTrip(
            @Valid @RequestBody TripOfferRequest request,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tripOfferService.createTripOffer(
                        request, UUID.fromString(driverId)));
    }

    // Recherche par ville
    @GetMapping("/search")
    public ResponseEntity<List<TripOfferResponse>> searchTrips(
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(tripOfferService.searchTrips(from, to));
    }

    // Recherche par géolocalisation
    @GetMapping("/nearby")
    public ResponseEntity<List<TripOfferResponse>> searchNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10.0") Double radius) {
        return ResponseEntity.ok(
                tripOfferService.searchNearbyTrips(lat, lng, radius));
    }

    // Trajets du driver connecté
    @GetMapping("/my-trips")
    public ResponseEntity<List<TripOfferResponse>> getMyTrips(
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                tripOfferService.getDriverTrips(UUID.fromString(driverId)));
    }

    // Détail d'un trajet
    @GetMapping("/{id}")
    public ResponseEntity<TripOfferResponse> getTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(tripOfferService.getTripById(id));
    }

    // Annuler un trajet
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TripOfferResponse> cancelTrip(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                tripOfferService.cancelTrip(id, UUID.fromString(driverId)));
    }


}