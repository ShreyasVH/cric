package services;

import requests.stats.FilterRequest;
import responses.StatsResponse;

import java.util.List;
import java.util.Map;

public interface StatsService
{
    StatsResponse getStats(FilterRequest filterRequest);
}
