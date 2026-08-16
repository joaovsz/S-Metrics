package com.smetrics.user.dto;

import com.smetrics.user.model.User;

public record UserResponse(
        Long id,
        String name,
        String spotifyId,
        boolean hasSpotifyToken
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getSpotifyId(),
                user.getSpotifyAccessToken() != null && !user.getSpotifyAccessToken().isBlank()
        );
    }
}
