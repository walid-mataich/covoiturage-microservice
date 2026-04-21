package ma.ensaj.covoiturage.negotiationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.negotiationservice.dto.request.CounterOfferRequest;
import ma.ensaj.covoiturage.negotiationservice.dto.request.StartNegotiationRequest;
import ma.ensaj.covoiturage.negotiationservice.dto.response.NegotiationResponse;
import ma.ensaj.covoiturage.negotiationservice.service.NegotiationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/negotiations")
@RequiredArgsConstructor
public class NegotiationController {

    private final NegotiationService negotiationService;

    // Passager ouvre une négociation sur un TripOffer
    @PostMapping
    public ResponseEntity<NegotiationResponse> startNegotiation(
            @Valid @RequestBody StartNegotiationRequest request,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(negotiationService.startNegotiation(
                        request, UUID.fromString(passengerId)));
    }

    // Voir le détail d'une négociation
    @GetMapping("/{id}")
    public ResponseEntity<NegotiationResponse> getNegotiation(
            @PathVariable UUID id) {
        return ResponseEntity.ok(negotiationService.getNegotiation(id));
    }

    // Toutes les négociations du passager connecté
    @GetMapping("/my-negotiations/passenger")
    public ResponseEntity<List<NegotiationResponse>> getPassengerNegotiations(
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                negotiationService.getPassengerNegotiations(
                        UUID.fromString(passengerId)));
    }

    // Toutes les négociations du driver connecté
    @GetMapping("/my-negotiations/driver")
    public ResponseEntity<List<NegotiationResponse>> getDriverNegotiations(
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                negotiationService.getDriverNegotiations(
                        UUID.fromString(driverId)));
    }

    // Driver soumet une contre-offre
    @PostMapping("/{id}/driver-counter")
    public ResponseEntity<NegotiationResponse> driverCounterOffer(
            @PathVariable UUID id,
            @Valid @RequestBody CounterOfferRequest request,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                negotiationService.driverCounterOffer(
                        id, request, UUID.fromString(driverId)));
    }

    // Passager soumet une contre-offre
    @PostMapping("/{id}/passenger-counter")
    public ResponseEntity<NegotiationResponse> passengerCounterOffer(
            @PathVariable UUID id,
            @Valid @RequestBody CounterOfferRequest request,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                negotiationService.passengerCounterOffer(
                        id, request, UUID.fromString(passengerId)));
    }

    // Driver accepte la dernière offre du passager → booking créé
    @PatchMapping("/{id}/driver-accept")
    public ResponseEntity<NegotiationResponse> driverAccepts(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                negotiationService.driverAccepts(
                        id, UUID.fromString(driverId)));
    }

    // Passager accepte la dernière offre du driver → booking créé
    @PatchMapping("/{id}/passenger-accept")
    public ResponseEntity<NegotiationResponse> passengerAccepts(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                negotiationService.passengerAccepts(
                        id, UUID.fromString(passengerId)));
    }

    // Driver rejette la négociation
    @PatchMapping("/{id}/driver-reject")
    public ResponseEntity<NegotiationResponse> driverRejects(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                negotiationService.driverRejects(
                        id, UUID.fromString(driverId)));
    }

    // Passager rejette la négociation
    @PatchMapping("/{id}/passenger-reject")
    public ResponseEntity<NegotiationResponse> passengerRejects(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                negotiationService.passengerRejects(
                        id, UUID.fromString(passengerId)));
    }
}