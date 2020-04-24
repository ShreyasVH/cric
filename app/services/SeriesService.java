package services;

import models.Series;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SeriesService
{
    CompletionStage<List<Series>> getAll();

    CompletionStage<Series> get(Long id);
}
