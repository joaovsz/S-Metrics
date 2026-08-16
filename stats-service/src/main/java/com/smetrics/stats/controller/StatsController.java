package com.smetrics.stats.controller;

import com.smetrics.stats.dto.SyncResultDto;
import com.smetrics.stats.dto.TopArtistDto;
import com.smetrics.stats.service.StatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/{userId}/sync")
    public SyncResultDto sync(@PathVariable Long userId) {
        return statsService.sync(userId);
    }

    @GetMapping("/{userId}/top-artists")
    public List<TopArtistDto> topArtists(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "10") int limit) {
        return statsService.topArtists(userId, limit);
    }
}
