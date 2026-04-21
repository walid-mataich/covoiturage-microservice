package ma.ensaj.covoiturage.tripservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.tripservice.dto.request.RideRequestRequest;
import ma.ensaj.covoiturage.tripservice.dto.response.RideRequestResponse;
import ma.ensaj.covoiturage.tripservice.service.RideRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips/requests")
@RequiredArgsConstructor
public class RideRequestController {

    private final RideRequestService rideRequestService;

    // Passager publie une demande
    @PostMapping
    public ResponseEntity<RideRequestResponse> createRequest(
            @Valid @RequestBody RideRequestRequest request,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rideRequestService.createRideRequest(
                        request, UUID.fromString(passengerId)));
    }

    // Driver cherche des demandes proches
    @GetMapping("/nearby")
    public ResponseEntity<List<RideRequestResponse>> searchNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10.0") Double radius) {
        return ResponseEntity.ok(
                rideRequestService.searchNearbyRequests(lat, lng, radius));
    }

    // Recherche par ville
    @GetMapping("/search")
    public ResponseEntity<List<RideRequestResponse>> searchByCity(
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(rideRequestService.searchByCity(from, to));
    }

    // Demandes du passager connecté
    @GetMapping("/my-requests")
    public ResponseEntity<List<RideRequestResponse>> getMyRequests(
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                rideRequestService.getPassengerRequests(
                        UUID.fromString(passengerId)));
    }

    // Détail d'une demande
    @GetMapping("/{id}")
    public ResponseEntity<RideRequestResponse> getRequest(
            @PathVariable UUID id) {
        return ResponseEntity.ok(rideRequestService.getById(id));
    }

    // Annuler une demande
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RideRequestResponse> cancelRequest(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                rideRequestService.cancelRequest(
                        id, UUID.fromString(passengerId)));
    }
}