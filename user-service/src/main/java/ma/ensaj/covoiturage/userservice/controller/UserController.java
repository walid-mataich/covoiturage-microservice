package ma.ensaj.covoiturage.userservice.controller;


import lombok.RequiredArgsConstructor;
import ma.ensaj.covoiturage.userservice.dto.request.UpdateProfileRequest;
import ma.ensaj.covoiturage.userservice.dto.response.UserResponse;
import ma.ensaj.covoiturage.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable UUID id,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }
}
