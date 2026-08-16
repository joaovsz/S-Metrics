package com.smetrics.user.dto;

import com.smetrics.user.model.User;

/**
 * Exposto apenas para consumo interno via Feign pelo stats-service.
 */
public record UserTokenResponse(
        Long id,
        String name,
        String spotifyAccessToken
) {
    public static UserTokenResponse from(User user) {
        return new UserTokenResponse(user.getId(), user.getName(), user.getSpotifyAccessToken());
    }
}
