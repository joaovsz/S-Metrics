package com.smetrics.stats.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "listen_events")
public class ListenEvent {

    @Id
    private String id;

    private Long userId;
    private String spotifyTrackId;
    private String trackName;
    private String artistName;
    private LocalDateTime playedAt;

    public ListenEvent() {
    }

    public ListenEvent(Long userId, String spotifyTrackId, String trackName, String artistName, LocalDateTime playedAt) {
        this.userId = userId;
        this.spotifyTrackId = spotifyTrackId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.playedAt = playedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSpotifyTrackId() {
        return spotifyTrackId;
    }

    public void setSpotifyTrackId(String spotifyTrackId) {
        this.spotifyTrackId = spotifyTrackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}
