package ma.ensaj.covoiturage.tripservice.entity.enums;


public enum TripStatus {
    OPEN,        // trajet publié, places disponibles
    FULL,        // toutes les places réservées
    IN_PROGRESS, // trajet en cours
    COMPLETED,   // trajet terminé
    CANCELLED    // trajet annulé par le driver
}