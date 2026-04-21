package ma.ensaj.covoiturage.messagingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class MessageRequest {

    @NotNull(message = "L'ID de la conversation est obligatoire")
    private UUID conversationId;

    @NotNull(message = "L'ID du destinataire est obligatoire")
    private UUID receiverId;

    @NotBlank(message = "Le message ne peut pas être vide")
    @Size(max = 1000, message = "Le message ne peut pas dépasser 1000 caractères")
    private String content;
}