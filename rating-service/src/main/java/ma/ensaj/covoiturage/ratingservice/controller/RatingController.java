package ma.ensaj.covoiturage.ratingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.ratingservice.dto.request.RatingRequest;
import ma.ensaj.covoiturage.ratingservice.dto.response.RatingResponse;
import ma.ensaj.covoiturage.ratingservice.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    // Passager note le driver OU driver note le passager
    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(
            @Valid @RequestBody RatingRequest request,
            @RequestHeader("X-User-Id") String raterId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ratingService.submitRating(
                        request, UUID.fromString(raterId)));
    }

    // Toutes les notes reçues par un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingResponse>> getUserRatings(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(ratingService.getUserRatings(userId));
    }

    // Notes reçues par un driver
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RatingResponse>> getDriverRatings(
            @PathVariable UUID driverId) {
        return ResponseEntity.ok(ratingService.getDriverRatings(driverId));
    }

    // Notes reçues par un passager
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<RatingResponse>> getPassengerRatings(
            @PathVariable UUID passengerId) {
        return ResponseEntity.ok(
                ratingService.getPassengerRatings(passengerId));
    }
}