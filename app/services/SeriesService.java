package services;

import models.Series;
import requests.series.CreateRequest;
import requests.series.UpdateRequest;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SeriesService
{
    CompletionStage<List<Series>> getAll();

    Series get(Long id);

    List<Series> get(String keyword);

    Series create(CreateRequest createRequest);

    Series update(Long id, UpdateRequest updateRequest);
}
