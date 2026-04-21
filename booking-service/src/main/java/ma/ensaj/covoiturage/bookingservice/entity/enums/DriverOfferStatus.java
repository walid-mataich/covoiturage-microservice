package ma.ensaj.covoiturage.bookingservice.entity.enums;

public enum DriverOfferStatus {
    PENDING,   // offre soumise, en attente réponse passager
    ACCEPTED,  // passager a accepté → déclenche création Booking
    REJECTED,  // passager a refusé
    EXPIRED    // passager n'a pas répondu dans le délai
}