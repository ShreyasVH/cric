package services.impl;

import repositories.PlayerRepository;
import requests.stats.FilterRequest;
import responses.StatsResponse;
import services.StatsService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatsServiceImpl implements StatsService
{
    private final PlayerRepository playerRepository;

    @Inject
    public StatsServiceImpl(PlayerRepository playerRepository)
    {
        this.playerRepository = playerRepository;
    }


    public StatsResponse getStats(FilterRequest filterRequest)
    {
        StatsResponse statsResponse = new StatsResponse();
        if("batting".equals(filterRequest.getType()))
        {
            statsResponse = this.playerRepository.getBattingStats(filterRequest);
        }
        else if("bowling".equals(filterRequest.getType()))
        {
            statsResponse = this.playerRepository.getBowlingStats(filterRequest);
        }
        return statsResponse;
    }
}
