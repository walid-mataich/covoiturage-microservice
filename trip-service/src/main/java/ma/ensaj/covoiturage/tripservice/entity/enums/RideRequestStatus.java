package ma.ensaj.covoiturage.tripservice.entity.enums;

public enum RideRequestStatus {
    PENDING,   // demande publiée, en attente d'offres driver
    MATCHED,   // un driver a proposé une offre acceptée
    COMPLETED, // trajet effectué
    CANCELLED  // annulée par le passager
}