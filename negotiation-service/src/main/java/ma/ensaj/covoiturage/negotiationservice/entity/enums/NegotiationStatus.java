package ma.ensaj.covoiturage.negotiationservice.entity.enums;

public enum NegotiationStatus {
    OPEN,     // négociation en cours — allers-retours d'offres
    AGREED,   // accord trouvé → booking créé automatiquement
    REJECTED, // l'une des parties a refusé
    EXPIRED   // délai dépassé sans accord
}