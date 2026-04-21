package ma.ensaj.covoiturage.negotiationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.ensaj.covoiturage.negotiationservice.entity.enums.OfferSide;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "negotiation_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NegotiationOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negotiation_id", nullable = false)
    private Negotiation negotiation;

    // Qui a soumis cette offre
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferSide side;

    // UUID du driver ou passager qui a soumis
    @Column(nullable = false)
    private UUID submittedBy;

    // Prix proposé dans cette offre
    @Column(nullable = false)
    private Double proposedPrice;

    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}