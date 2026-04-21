package ma.ensaj.covoiturage.bookingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.bookingservice.dto.request.BookingRequest;
import ma.ensaj.covoiturage.bookingservice.dto.response.BookingResponse;
import ma.ensaj.covoiturage.bookingservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Passager crée une réservation
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(
                        request, UUID.fromString(passengerId)));
    }

    // Détail d'une réservation
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    // Réservations du passager connecté
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @RequestHeader("X-User-Id") String passengerId) {
        return ResponseEntity.ok(
                bookingService.getPassengerBookings(UUID.fromString(passengerId)));
    }

    // Réservations reçues par le driver connecté
    @GetMapping("/driver-bookings")
    public ResponseEntity<List<BookingResponse>> getDriverBookings(
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                bookingService.getDriverBookings(UUID.fromString(driverId)));
    }

    // Réservations en attente pour le driver
    @GetMapping("/pending")
    public ResponseEntity<List<BookingResponse>> getPendingBookings(
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                bookingService.getPendingDriverBookings(UUID.fromString(driverId)));
    }

    // Driver confirme
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                bookingService.confirmBooking(id, UUID.fromString(driverId)));
    }

    // Driver rejette
    @PatchMapping("/{id}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String driverId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                bookingService.rejectBooking(
                        id, UUID.fromString(driverId), body.get("reason")));
    }

    // Driver démarre le trajet
    @PatchMapping("/{id}/start")
    public ResponseEntity<BookingResponse> startTrip(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                bookingService.startTrip(id, UUID.fromString(driverId)));
    }

    // Driver termine le trajet
    @PatchMapping("/{id}/complete")
    public ResponseEntity<BookingResponse> completeTrip(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String driverId) {
        return ResponseEntity.ok(
                bookingService.completeTrip(id, UUID.fromString(driverId)));
    }

    // Passager annule
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String passengerId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                bookingService.cancelBooking(
                        id, UUID.fromString(passengerId), body.get("reason")));
    }
}