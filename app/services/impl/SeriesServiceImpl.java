package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.DBInteractionException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.Country;
import models.Series;
import models.Team;
import repositories.CountryRepository;
import repositories.SeriesRepository;
import repositories.TeamRepository;
import requests.series.CreateRequest;
import services.SeriesService;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class SeriesServiceImpl implements SeriesService
{
    private final CountryRepository countryRepository;
    private final SeriesRepository seriesRepository;
    private final TeamRepository teamRepository;

    @Inject
    public SeriesServiceImpl
    (
        CountryRepository countryRepository,
        SeriesRepository seriesRepository,
        TeamRepository teamRepository
    )
    {
        this.countryRepository = countryRepository;
        this.seriesRepository = seriesRepository;
        this.teamRepository = teamRepository;
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

    @Override
    public CompletionStage<Series> create(CreateRequest createRequest)
    {
        createRequest.validate();

        CompletionStage<Series> response = this.seriesRepository.get(createRequest.getName(), createRequest.getGameType());
        return response.thenComposeAsync(existingSeries -> {
            if(null != existingSeries)
            {
                throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
            }

            CompletionStage<Country> countryResponse = this.countryRepository.get(createRequest.getHomeCountryId());
            return countryResponse.thenComposeAsync(homeCountry -> {
                if(null == homeCountry)
                {
                    throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
                }

                Transaction transaction = Ebean.beginTransaction();
                try
                {
                    Series series = new Series();
                    series.setHomeCountry(homeCountry);
                    series.setName(createRequest.getName());
                    series.setType(createRequest.getType());
                    series.setGameType(createRequest.getGameType());
                    try
                    {

                        series.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createRequest.getStartTime()));
                        series.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createRequest.getEndTime()));

                    }
                    catch(Exception ex)
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
                    }

                    Date now = Utils.getCurrentDate();
                    series.setCreatedAt(now);
                    series.setUpdatedAt(now);

                    CompletionStage<List<Team>> teamResponse = this.teamRepository.get(createRequest.getTeams());
                    return teamResponse.thenComposeAsync(teams -> {
                        if(createRequest.getTeams().size() != teams.size())
                        {
                            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
                        }

                        series.setTeams(teams);
                        CompletionStage<Series> createResponse = this.seriesRepository.save(series);
                        return createResponse.thenApplyAsync(createdSeries -> {
                            transaction.commit();
                            transaction.end();
                            return createdSeries;
                        });
                    });
                }
                catch(Exception ex)
                {
                    transaction.rollback();
                    transaction.end();
                    throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), ErrorCode.DB_INTERACTION_FAILED.getDescription());
                }
            });
        });
    }
}
