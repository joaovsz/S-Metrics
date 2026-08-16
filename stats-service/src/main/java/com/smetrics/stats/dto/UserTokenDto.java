package com.smetrics.stats.dto;

public record UserTokenDto(
        Long id,
        String name,
        String spotifyAccessToken
) {
}
