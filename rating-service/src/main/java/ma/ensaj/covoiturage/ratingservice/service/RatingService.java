package ma.ensaj.covoiturage.ratingservice.service;

import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.ratingservice.client.BookingServiceClient;
import ma.ensaj.covoiturage.ratingservice.client.UserServiceClient;
import ma.ensaj.covoiturage.ratingservice.dto.external.BookingResponse;
import ma.ensaj.covoiturage.ratingservice.dto.external.UserRatingUpdate;
import ma.ensaj.covoiturage.ratingservice.dto.request.RatingRequest;
import ma.ensaj.covoiturage.ratingservice.dto.response.RatingResponse;
import ma.ensaj.covoiturage.ratingservice.entity.Rating;
import ma.ensaj.covoiturage.ratingservice.entity.enums.RatedSide;
import ma.ensaj.covoiturage.ratingservice.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BookingServiceClient bookingServiceClient;
    private final UserServiceClient userServiceClient;

    public RatingResponse submitRating(RatingRequest request, UUID raterId) {

        // 1. Récupérer le booking depuis booking-service
        BookingResponse booking = bookingServiceClient
                .getBooking(request.getBookingId());

        // 2. Vérifier que le trajet est terminé
        if (!"COMPLETED".equals(booking.getStatus())) {
            throw new RuntimeException(
                    "Vous ne pouvez noter qu'après la fin du trajet");
        }

        // 3. Vérifier que celui qui note fait partie du booking
        boolean isPassenger = booking.getPassengerId().equals(raterId);
        boolean isDriver = booking.getDriverId().equals(raterId);

        if (!isPassenger && !isDriver) {
            throw new RuntimeException(
                    "Vous ne faites pas partie de ce trajet");
        }

        // 4. Vérifier que celui noté fait aussi partie du booking
        boolean ratedIsPassenger = booking.getPassengerId()
                .equals(request.getRatedUserId());
        boolean ratedIsDriver = booking.getDriverId()
                .equals(request.getRatedUserId());

        if (!ratedIsPassenger && !ratedIsDriver) {
            throw new RuntimeException(
                    "L'utilisateur noté ne fait pas partie de ce trajet");
        }

        // 5. Vérifier qu'on ne note pas soi-même
        if (raterId.equals(request.getRatedUserId())) {
            throw new RuntimeException(
                    "Vous ne pouvez pas vous noter vous-même");
        }

        // 6. Vérifier qu'on n'a pas déjà noté
        if (ratingRepository.existsByBookingIdAndRaterId(
                request.getBookingId(), raterId)) {
            throw new RuntimeException(
                    "Vous avez déjà noté ce trajet");
        }

        // 7. Déterminer qui est noté (DRIVER ou PASSENGER)
        RatedSide ratedSide = ratedIsDriver
                ? RatedSide.DRIVER
                : RatedSide.PASSENGER;

        // 8. Sauvegarder la note
        Rating rating = Rating.builder()
                .bookingId(request.getBookingId())
                .raterId(raterId)
                .ratedId(request.getRatedUserId())
                .ratedSide(ratedSide)
                .score(request.getScore())
                .comment(request.getComment())
                .build();

        ratingRepository.save(rating);

        // 9. Calculer la nouvelle moyenne
        Double newAverage = ratingRepository
                .calculateAverageRating(request.getRatedUserId())
                .orElse(0.0);

        long totalRatings = ratingRepository
                .countByRatedId(request.getRatedUserId());

        // 10. Mettre à jour la moyenne dans user-service
        userServiceClient.updateUserRating(
                request.getRatedUserId(),
                UserRatingUpdate.builder()
                        .averageRating(newAverage)
                        .totalRatings((int) totalRatings)
                        .build()
        );

        return RatingResponse.builder()
                .id(rating.getId())
                .bookingId(rating.getBookingId())
                .raterId(rating.getRaterId())
                .ratedId(rating.getRatedId())
                .ratedSide(rating.getRatedSide())
                .score(rating.getScore())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .newAverageRating(Math.round(newAverage * 10.0) / 10.0)
                .newTotalRatings(totalRatings)
                .build();
    }

    // Toutes les notes reçues par un utilisateur
    public List<RatingResponse> getUserRatings(UUID userId) {
        return ratingRepository
                .findByRatedIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Notes reçues par un driver
    public List<RatingResponse> getDriverRatings(UUID driverId) {
        return ratingRepository
                .findByRatedIdAndRatedSide(driverId, RatedSide.DRIVER)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Notes reçues par un passager
    public List<RatingResponse> getPassengerRatings(UUID passengerId) {
        return ratingRepository
                .findByRatedIdAndRatedSide(passengerId, RatedSide.PASSENGER)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RatingResponse mapToResponse(Rating rating) {
        Double avg = ratingRepository
                .calculateAverageRating(rating.getRatedId())
                .orElse(0.0);

        long total = ratingRepository.countByRatedId(rating.getRatedId());

        return RatingResponse.builder()
                .id(rating.getId())
                .bookingId(rating.getBookingId())
                .raterId(rating.getRaterId())
                .ratedId(rating.getRatedId())
                .ratedSide(rating.getRatedSide())
                .score(rating.getScore())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .newAverageRating(Math.round(avg * 10.0) / 10.0)
                .newTotalRatings(total)
                .build();
    }
}