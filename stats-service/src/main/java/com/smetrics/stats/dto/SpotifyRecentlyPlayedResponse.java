package com.smetrics.stats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyRecentlyPlayedResponse(List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(Track track, String played_at) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Track(String id, String name, List<Artist> artists) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Artist(String name) {
    }
}
