package ma.ensaj.covoiturage.userservice.dto.request;

import lombok.Data;

@Data
public class UserRatingUpdateRequest {
    private Double averageRating;
    private Integer totalRatings;
}