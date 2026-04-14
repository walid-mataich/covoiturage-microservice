package ma.ensaj.covoiturage.userservice.service;


import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.userservice.dto.request.UpdateProfileRequest;
import ma.ensaj.covoiturage.userservice.dto.response.UserResponse;
import ma.ensaj.covoiturage.userservice.entity.User;
import ma.ensaj.covoiturage.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return mapToResponse(user);
    }

    public UserResponse updateProfile(UUID id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null)
            user.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfilePicture() != null)
            user.setProfilePicture(request.getProfilePicture());
        if (request.getVehicleBrand() != null)
            user.setVehicleBrand(request.getVehicleBrand());
        if (request.getVehicleModel() != null)
            user.setVehicleModel(request.getVehicleModel());
        if (request.getVehiclePlate() != null)
            user.setVehiclePlate(request.getVehiclePlate());
        if (request.getVehicleSeats() != null)
            user.setVehicleSeats(request.getVehicleSeats());

        userRepository.save(user);
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .averageRating(user.getAverageRating())
                .totalRatings(user.getTotalRatings())
                .isVerified(user.getIsVerified())
                .vehicleBrand(user.getVehicleBrand())
                .vehicleModel(user.getVehicleModel())
                .vehiclePlate(user.getVehiclePlate())
                .vehicleSeats(user.getVehicleSeats())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
