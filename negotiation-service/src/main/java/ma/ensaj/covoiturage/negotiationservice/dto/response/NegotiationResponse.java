package ma.ensaj.covoiturage.negotiationservice.dto.response;

import lombok.*;
import ma.ensaj.covoiturage.negotiationservice.entity.enums.NegotiationStatus;
import ma.ensaj.covoiturage.negotiationservice.entity.enums.OfferSide;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationResponse {

    private UUID id;
    private UUID driverId;
    private UUID passengerId;
    private UUID tripId;
    private Double initialPrice;
    private Double agreedPrice;
    private Integer seatsNeeded;
    private NegotiationStatus status;
    private UUID bookingId;
    private LocalDateTime createdAt;
    private LocalDateTime agreedAt;

    // Historique complet des offres et contre-offres
    private List<OfferDetail> offers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfferDetail {
        private UUID id;
        private OfferSide side;
        private UUID submittedBy;
        private Double proposedPrice;
        private String message;
        private LocalDateTime createdAt;
    }
}