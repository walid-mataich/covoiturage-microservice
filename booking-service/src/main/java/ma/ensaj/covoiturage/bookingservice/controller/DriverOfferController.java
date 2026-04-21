package ma.ensaj.covoiturage.bookingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.bookingservice.dto.request.DriverOfferRequest;
import ma.ensaj.covoiturage.bookingservice.dto.response.BookingResponse;
import ma.ensaj.covoiturage.bookingservice.dto.response.DriverOfferResponse;
import ma.ensaj.covoiturage.bookingservice.service.DriverOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings/driver-offers")
@RequiredArgsConstructor
public class DriverOfferController {

    private final DriverOfferService driverOfferService;

    // Driver soumet une offre pour une RideRequest
    @PostMapping
    public ResponseEntity<DriverOfferResponse> submitOffer(
            @Valid @RequestBody DriverOfferRequest request,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(driverOfferService.submitOffer(
                        request, UUID.fromString(driverId)));
    }

    // Passager voit toutes les offres pour sa demande
    @GetMapping("/ride-request/{rideRequestId}")
    public ResponseEntity<List<DriverOfferResponse>> getOffersForRequest(
            @PathVariable UUID rideRequestId) {
        return ResponseEntity.ok(
                driverOfferService.getOffersForRequest(rideRequestId));
    }

    // Driver voit ses offres soumises
    @GetMapping("/my-offers")
    public ResponseEntity<List<DriverOfferResponse>> getMyOffers(
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                driverOfferService.getDriverOffers(UUID.fromString(driverId)));
    }

    // Passager accepte une offre
    @PatchMapping("/{id}/accept")
    public ResponseEntity<BookingResponse> acceptOffer(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                driverOfferService.acceptOffer(
                        id,
                        UUID.fromString(passengerId)));
    }

    // Passager refuse une offre
    @PatchMapping("/{id}/reject")
    public ResponseEntity<DriverOfferResponse> rejectOffer(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                driverOfferService.rejectOffer(id, UUID.fromString(passengerId)));
    }
}