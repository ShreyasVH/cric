package services.impl;

import com.google.inject.Inject;

import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import models.Country;
import models.Team;
import org.springframework.util.StringUtils;
import repositories.CountryRepository;
import repositories.TeamRepository;
import requests.UpdateCountryRequest;
import requests.teams.CreateRequest;
import requests.teams.UpdateRequest;
import services.TeamService;
import utils.Utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class TeamServiceImpl implements TeamService
{
    private final CountryRepository countryRepository;
    private final TeamRepository teamRepository;

    @Inject
    public TeamServiceImpl
    (
        CountryRepository countryRepository,
        TeamRepository teamRepository
    )
    {
        this.countryRepository = countryRepository;
        this.teamRepository = teamRepository;
    }

    public CompletionStage<List<Team>> getAll()
    {
        return this.teamRepository.getAll();
    }

    public Team get(Long id)
    {
        Team team = this.teamRepository.get(id);
        if(null == team)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team"));
        }
        return team;
    }

    public List<Team> get(String keyword)
    {
        return this.teamRepository.get(keyword);
    }

    public Team create(CreateRequest createRequest)
    {
        createRequest.validate();

        Team existingTeam = this.teamRepository.get(createRequest.getName(), createRequest.getCountryId());
        if(null != existingTeam)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
        }

        Country country = this.countryRepository.get(createRequest.getCountryId());
        if(null == country)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
        }

        Team team = new Team(createRequest);
        team.setCountry(country);
        return this.teamRepository.save(team);
    }

    public Team update(Long id, UpdateRequest updateRequest)
    {
        updateRequest.validate();

        Team existingTeam = this.teamRepository.get(id);
        if(null == existingTeam)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team"));
        }

        boolean isUpdateRequired = false;

        if(!StringUtils.isEmpty(updateRequest.getName()) && (!existingTeam.getName().equals(updateRequest.getName())))
        {
            existingTeam.setName(updateRequest.getName());
            isUpdateRequired = true;
        }

        if((null != updateRequest.getTeamType()) && (!existingTeam.getTeamType().equals(updateRequest.getTeamType())))
        {
            existingTeam.setTeamType(updateRequest.getTeamType());
            isUpdateRequired = true;
        }

        if((null != updateRequest.getCountryId()) && (!updateRequest.getCountryId().equals(existingTeam.getCountry().getId())))
        {
            Country country = this.countryRepository.get(updateRequest.getCountryId());
            if(null == country)
            {
                throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
            }

            isUpdateRequired = true;
            existingTeam.setCountry(country);

        }

        if(isUpdateRequired)
        {
            return this.teamRepository.save(existingTeam);
        }
        else
        {
            return existingTeam;
        }
    }
}
