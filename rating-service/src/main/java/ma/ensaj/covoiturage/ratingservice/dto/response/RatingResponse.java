package ma.ensaj.covoiturage.ratingservice.dto.response;

import lombok.*;
import ma.ensaj.covoiturage.ratingservice.entity.enums.RatedSide;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {

    private UUID id;
    private UUID bookingId;
    private UUID raterId;
    private UUID ratedId;
    private RatedSide ratedSide;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;

    // Stats mises à jour de l'utilisateur noté
    private Double newAverageRating;
    private Long newTotalRatings;
}