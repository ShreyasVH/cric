package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.DBInteractionException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.*;
import org.springframework.util.StringUtils;
import repositories.*;
import requests.series.CreateRequest;
import requests.series.UpdateRequest;
import responses.ManOfTheSeriesResponse;
import responses.SeriesResponse;
import responses.TeamResponse;
import services.CountryService;
import services.PlayerService;
import services.SeriesService;
import services.TeamService;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class SeriesServiceImpl implements SeriesService
{
    private final CountryService countryService;
    private final PlayerService playerService;
    private final TeamService teamService;

    private final PlayerRepository playerRepository;
    private final SeriesRepository seriesRepository;
    private final TourRepository tourRepository;

    @Inject
    public SeriesServiceImpl
    (
        CountryService countryService,
        PlayerService playerService,
        TeamService teamService,

        PlayerRepository playerRepository,
        SeriesRepository seriesRepository,
        TourRepository tourRepository
    )
    {
        this.countryService = countryService;
        this.playerService = playerService;
        this.teamService = teamService;

        this.playerRepository = playerRepository;
        this.seriesRepository = seriesRepository;
        this.tourRepository = tourRepository;
    }

    public SeriesResponse seriesResponse(Series series)
    {
        SeriesResponse seriesResponse = new SeriesResponse(series);

        seriesResponse.setHomeCountry(this.countryService.get(series.getHomeCountryId()));
        seriesResponse.setTeams(this.teamService.get(this.seriesRepository.getTeamsForSeries(series.getId()).stream().map(SeriesTeamsMap::getTeamId).collect(Collectors.toList())));
        List<ManOfTheSeries> manOfTheSeriesList = this.seriesRepository.getManOfTheSeriesForSeries(series.getId());
        for(ManOfTheSeries mots: manOfTheSeriesList)
        {
            ManOfTheSeriesResponse motsResponse = new ManOfTheSeriesResponse(mots);
            Team team = this.teamService.getRaw(mots.getTeamId());
            if(null == team)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team"));
            }
            motsResponse.setTeamName(team.getName());

            Player player = this.playerService.getRaw(mots.getPlayerId());
            motsResponse.setPlayerName(player.getName());

            seriesResponse.getManOfTheSeriesList().add(motsResponse);
        }

        return seriesResponse;
    }

    @Override
    public CompletionStage<List<Series>> getAll()
    {
        return this.seriesRepository.getAll();
    }

    @Override
    public SeriesResponse get(Long id)
    {
        Series series = this.seriesRepository.get(id);
        if(null == series)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Series"));
        }

        return seriesResponse(series);
    }

    @Override
    public List<Series> get(String keyword) {
        return this.seriesRepository.get(keyword);
    }

    @Override
    public Series create(CreateRequest createRequest)
    {
        createRequest.validate();
        Series existingSeries = this.seriesRepository.get(createRequest.getName(), createRequest.getGameType(), createRequest.getStartTime());
        if(null != existingSeries)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
        }

        Country homeCountry = this.countryService.get(createRequest.getHomeCountryId());
        if(null == homeCountry)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
        }

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            Series series = new Series();
            series.setHomeCountryId(homeCountry.getId());
            series.setName(createRequest.getName());
            series.setType(createRequest.getType());
            series.setGameType(createRequest.getGameType());
            series.setStartTime(createRequest.getStartTime());

            Tour tour = this.tourRepository.get(createRequest.getTourId());
            if(null == tour)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Tour"));
            }
            series.setTourId(tour.getId());

            List<Team> teams = this.teamService.get(createRequest.getTeams());
            if(createRequest.getTeams().size() != teams.size())
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            Series createdSeries = this.seriesRepository.save(series);

            this.seriesRepository.addTeamsToSeries(createRequest.getTeams(), createdSeries.getId());

            transaction.commit();
            transaction.end();
            return createdSeries;
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), ErrorCode.DB_INTERACTION_FAILED.getDescription());
        }
    }

    @Override
    public Series update(Long id, UpdateRequest updateRequest)
    {
        Series existingSeries = this.seriesRepository.get(id);
        if(null == existingSeries)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Series"));
        }
        List<SeriesTeamsMap> existingTeams = this.seriesRepository.getTeamsForSeries(id);
        updateRequest.validate(existingSeries, existingTeams);

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            boolean isUpdateRequired = false;

            if(!StringUtils.isEmpty(updateRequest.getName()) && !existingSeries.getName().equals(updateRequest.getName()))
            {
                isUpdateRequired = true;
                existingSeries.setName(updateRequest.getName());
            }

            if((null != updateRequest.getType()) && !existingSeries.getType().equals(updateRequest.getType()))
            {
                isUpdateRequired = true;
                existingSeries.setType(updateRequest.getType());
            }

            if((null != updateRequest.getGameType()) && !existingSeries.getGameType().equals(updateRequest.getGameType()))
            {
                isUpdateRequired = true;
                existingSeries.setGameType(updateRequest.getGameType());
            }

            if((null != updateRequest.getStartTime()) && !existingSeries.getStartTime().equals(updateRequest.getStartTime()))
            {
                isUpdateRequired = true;
                existingSeries.setStartTime(updateRequest.getStartTime());
            }

            if((null != updateRequest.getHomeCountryId()) && (!updateRequest.getHomeCountryId().equals(existingSeries.getHomeCountryId())))
            {
                Country country = this.countryService.get(updateRequest.getHomeCountryId());
                if(null == country)
                {
                    throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
                }

                isUpdateRequired = true;
                existingSeries.setHomeCountryId(country.getId());
            }

            if((null != updateRequest.getTourId()) && !updateRequest.getTourId().equals(existingSeries.getTourId()))
            {
                Tour tour = this.tourRepository.get(updateRequest.getTourId());
                if(null == tour)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Tour"));
                }
                isUpdateRequired = true;
                existingSeries.setTourId(tour.getId());
            }

            if((null != updateRequest.getTeams()) && (updateRequest.getTeams().size() > 0))
            {
                if(existingTeams.size() != updateRequest.getTeams().size())
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
                }

                List<Long> existingTeamIds = existingTeams.stream().map(SeriesTeamsMap::getTeamId).collect(Collectors.toList());

                List<Long> teamsToDelete = new ArrayList<>();
                List<Long> teamsToAdd = new ArrayList<>();

                for(Long team: updateRequest.getTeams())
                {
                    if(!existingTeamIds.contains(team))
                    {
                        teamsToAdd.add(team);
                    }
                }

                for(Long team: existingTeamIds)
                {
                    if(!updateRequest.getTeams().contains(team))
                    {
                        teamsToDelete.add(team);
                    }
                }

                this.seriesRepository.addTeamsToSeries(teamsToAdd, existingSeries.getId());
                this.seriesRepository.removeTeamsFromSeries(teamsToDelete, existingSeries.getId());

                isUpdateRequired = ((!teamsToAdd.isEmpty()) || (!teamsToDelete.isEmpty()));
            }

            if((null != updateRequest.getManOfTheSeriesList()) && (!updateRequest.getManOfTheSeriesList().isEmpty()))
            {
                List<ManOfTheSeries> manOfTheSeriesToAdd = new ArrayList<>();
                List<ManOfTheSeries> existingManOfTheSeriesList = this.seriesRepository.getManOfTheSeriesForSeries(id);
                List<String> existingManOfTheSeriesLookup = existingManOfTheSeriesList.stream().map(mots -> mots.getPlayerId() + "_" + mots.getTeamId()).collect(Collectors.toList());
                List<ManOfTheSeries> manOfTheSeriesToDelete = new ArrayList<>();
                List<String> motsLookup = new ArrayList<>();
                for(Map<String, Long> manOfTheSeriesRaw: updateRequest.getManOfTheSeriesList())
                {
                    Long playerId = manOfTheSeriesRaw.get("playerId");
                    Long teamId = manOfTheSeriesRaw.get("teamId");
                    ManOfTheSeries manOfTheSeries = new ManOfTheSeries();
                    String key = playerId + "_" + teamId;
                    if(!existingManOfTheSeriesLookup.contains(key))
                    {
                        Player player = this.playerRepository.get(playerId);
                        if(null == player)
                        {
                            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Player"));
                        }

                        manOfTheSeries.setPlayerId(player.getId());

                        TeamResponse team = this.teamService.get(teamId);
                        if(null == team)
                        {
                            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team"));
                        }
                        manOfTheSeries.setTeamId(team.getId());
                        manOfTheSeries.setSeriesId(existingSeries.getId());

                        manOfTheSeriesToAdd.add(manOfTheSeries);
                    }

                    motsLookup.add(key);
                }

                for(ManOfTheSeries mots: existingManOfTheSeriesList)
                {
                    Long playerId = mots.getPlayerId();
                    Long teamId = mots.getTeamId();
                    String key = playerId + "_" + teamId;
                    if(!motsLookup.contains(key))
                    {
                        manOfTheSeriesToDelete.add(mots);
                    }
                }


                isUpdateRequired = ((!manOfTheSeriesToAdd.isEmpty()) || (!manOfTheSeriesToDelete.isEmpty()));

                if(!manOfTheSeriesToAdd.isEmpty())
                {
                    this.seriesRepository.addManOfTheSeriesToSeries(manOfTheSeriesToAdd);
                }

                if(!manOfTheSeriesToDelete.isEmpty())
                {
                    this.seriesRepository.removeManOfTheSeriesToSeries(manOfTheSeriesToDelete);
                }
            }

            Series updatedSeries;
            if(isUpdateRequired)
            {
                updatedSeries = this.seriesRepository.save(existingSeries);
                transaction.commit();
                transaction.end();
            }
            else
            {
                updatedSeries = existingSeries;
            }
            return updatedSeries;
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), ErrorCode.DB_INTERACTION_FAILED.getDescription());
        }
    }

    @Override
    public List<Series> getSeriesForTour(Long tourId)
    {
        return this.seriesRepository.getSeriesListForTour(tourId);
    }
}
