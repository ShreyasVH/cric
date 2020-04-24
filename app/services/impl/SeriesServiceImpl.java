package services.impl;

import com.google.inject.Inject;
import models.Series;
import repositories.SeriesRepository;
import services.SeriesService;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class SeriesServiceImpl implements SeriesService
{
    private final SeriesRepository seriesRepository;

    @Inject
    public SeriesServiceImpl
    (
        SeriesRepository seriesRepository
    )
    {
        this.seriesRepository = seriesRepository;
    }

    @Override
    public CompletionStage<List<Series>> getAll()
    {
        return this.seriesRepository.getAll();
    }
}
