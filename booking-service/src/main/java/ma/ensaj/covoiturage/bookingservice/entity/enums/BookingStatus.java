package ma.ensaj.covoiturage.bookingservice.entity.enums;

public enum BookingStatus {
    PENDING,      // réservation créée, en attente confirmation driver
    CONFIRMED,    // driver a confirmé
    IN_PROGRESS,  // trajet en cours
    COMPLETED,    // trajet terminé avec succès
    CANCELLED,    // annulée (passager ou driver)
    REJECTED      // refusée par le driver
}