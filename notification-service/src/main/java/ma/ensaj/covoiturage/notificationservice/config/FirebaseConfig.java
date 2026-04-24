package ma.ensaj.covoiturage.notificationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        try {
            // Vérifier si Firebase est déjà initialisé
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = getClass()
                        .getResourceAsStream(
                                credentialsPath.replace("classpath:", "/"));

                if (serviceAccount == null) {
                    System.err.println(
                            "⚠ Firebase credentials non trouvés : "
                                    + credentialsPath);
                    System.err.println(
                            "⚠ Les notifications push seront désactivées");
                    return;
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials
                                .fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("✓ Firebase initialisé avec succès");
            }
        } catch (Exception e) {
            System.err.println(
                    "⚠ Erreur initialisation Firebase : " + e.getMessage());
            System.err.println(
                    "⚠ Les notifications push seront désactivées");
        }
    }
}