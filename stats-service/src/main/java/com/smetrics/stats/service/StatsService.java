package com.smetrics.stats.service;

import com.smetrics.stats.client.SpotifyClient;
import com.smetrics.stats.client.UserClient;
import com.smetrics.stats.dto.SpotifyRecentlyPlayedResponse;
import com.smetrics.stats.dto.SyncResultDto;
import com.smetrics.stats.dto.TopArtistDto;
import com.smetrics.stats.dto.UserTokenDto;
import com.smetrics.stats.model.ListenEvent;
import com.smetrics.stats.repository.ListenEventRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class StatsService {

    private final UserClient userClient;
    private final SpotifyClient spotifyClient;
    private final ListenEventRepository listenEventRepository;
    private final MongoTemplate mongoTemplate;
    private final int recentlyPlayedLimit;

    public StatsService(UserClient userClient,
                         SpotifyClient spotifyClient,
                         ListenEventRepository listenEventRepository,
                         MongoTemplate mongoTemplate,
                         @Value("${spotify.api.recently-played-limit:50}") int recentlyPlayedLimit) {
        this.userClient = userClient;
        this.spotifyClient = spotifyClient;
        this.listenEventRepository = listenEventRepository;
        this.mongoTemplate = mongoTemplate;
        this.recentlyPlayedLimit = recentlyPlayedLimit;
    }

    public SyncResultDto sync(Long userId) {
        UserTokenDto user = userClient.getToken(userId);
        if (user.spotifyAccessToken() == null || user.spotifyAccessToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "User " + userId + " has no Spotify access token saved yet");
        }

        SpotifyRecentlyPlayedResponse response = fetchRecentlyPlayed(user.spotifyAccessToken());

        List<ListenEvent> events = response.items().stream()
                .map(item -> toListenEvent(userId, item))
                .toList();

        listenEventRepository.saveAll(events);
        return new SyncResultDto(userId, events.size());
    }

    private SpotifyRecentlyPlayedResponse fetchRecentlyPlayed(String accessToken) {
        try {
            return spotifyClient.getRecentlyPlayed("Bearer " + accessToken, recentlyPlayedLimit);
        } catch (FeignException.TooManyRequests e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Spotify rate limit atingido, tente novamente mais tarde");
        } catch (FeignException.Unauthorized e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Token do Spotify expirado ou inválido para este usuário");
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Falha ao consultar a Spotify API: " + e.getMessage());
        }
    }

    private ListenEvent toListenEvent(Long userId, SpotifyRecentlyPlayedResponse.Item item) {
        String artistName = item.track().artists().isEmpty() ? "Unknown" : item.track().artists().get(0).name();
        LocalDateTime playedAt = LocalDateTime.ofInstant(Instant.parse(item.played_at()), ZoneOffset.UTC);
        return new ListenEvent(userId, item.track().id(), item.track().name(), artistName, playedAt);
    }

    public List<TopArtistDto> topArtists(Long userId, int limit) {
        Aggregation aggregation = newAggregation(
                match(org.springframework.data.mongodb.core.query.Criteria.where("userId").is(userId)),
                group("artistName").count().as("playCount"),
                project("playCount").and("_id").as("artistName"),
                sort(org.springframework.data.domain.Sort.Direction.DESC, "playCount"),
                limit(limit)
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "listen_events", Map.class);
        return results.getMappedResults().stream()
                .map(row -> new TopArtistDto((String) row.get("artistName"), ((Number) row.get("playCount")).longValue()))
                .toList();
    }
}
