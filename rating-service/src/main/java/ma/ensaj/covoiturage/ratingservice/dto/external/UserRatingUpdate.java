package ma.ensaj.covoiturage.ratingservice.dto.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRatingUpdate {
    private Double averageRating;
    private Integer totalRatings;
}