package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.NotFoundException;
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

    @Override
    public CompletionStage<Series> get(Long id)
    {
        CompletionStage<Series> response = this.seriesRepository.get(id);
        return response.thenApplyAsync(series -> {
            if(null == series)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Series"));
            }

            return series;
        });
    }
}
