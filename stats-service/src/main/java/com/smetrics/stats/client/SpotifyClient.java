package com.smetrics.stats.client;

import com.smetrics.stats.dto.SpotifyRecentlyPlayedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente Feign para a Spotify Web API (fora do Eureka - URL fixa).
 * Endpoint oficial: GET /v1/me/player/recently-played
 * https://developer.spotify.com/documentation/web-api/reference/get-recently-played
 */
@FeignClient(name = "spotify-api", url = "${spotify.api.base-url}")
public interface SpotifyClient {

    @GetMapping("/me/player/recently-played")
    SpotifyRecentlyPlayedResponse getRecentlyPlayed(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("limit") int limit
    );
}
