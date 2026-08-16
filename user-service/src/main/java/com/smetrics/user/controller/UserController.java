package com.smetrics.user.controller;

import com.smetrics.user.dto.CreateUserRequest;
import com.smetrics.user.dto.UpdateTokensRequest;
import com.smetrics.user.dto.UserResponse;
import com.smetrics.user.dto.UserTokenResponse;
import com.smetrics.user.model.User;
import com.smetrics.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User user = new User(request.name(), request.spotifyId());
        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    @PutMapping("/{id}/tokens")
    public UserResponse updateTokens(@PathVariable Long id, @Valid @RequestBody UpdateTokensRequest request) {
        User user = findOrThrow(id);
        user.setSpotifyAccessToken(request.accessToken());
        user.setSpotifyRefreshToken(request.refreshToken());
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Consumido internamente pelo stats-service via Feign para obter o token do Spotify.
     */
    @GetMapping("/{id}/token")
    public UserTokenResponse getToken(@PathVariable Long id) {
        return UserTokenResponse.from(findOrThrow(id));
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found"));
    }
}
