package ma.ensaj.covoiturage.negotiationservice.repository;

import ma.ensaj.covoiturage.negotiationservice.entity.NegotiationOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NegotiationOfferRepository
        extends JpaRepository<NegotiationOffer, UUID> {

    List<NegotiationOffer> findByNegotiationIdOrderByCreatedAtAsc(
            UUID negotiationId);
}