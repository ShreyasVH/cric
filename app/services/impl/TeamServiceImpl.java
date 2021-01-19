package services.impl;

import com.google.inject.Inject;

import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import models.Country;
import models.Team;
import org.springframework.util.StringUtils;
import repositories.TeamRepository;
import requests.teams.CreateRequest;
import requests.teams.UpdateRequest;
import responses.TeamResponse;
import services.CountryService;
import services.TeamService;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class TeamServiceImpl implements TeamService
{
    private final CountryService countryService;

    private final TeamRepository teamRepository;

    @Inject
    public TeamServiceImpl
    (
        CountryService countryService,

        TeamRepository teamRepository
    )
    {
        this.countryService = countryService;

        this.teamRepository = teamRepository;
    }

    public TeamResponse teamResponse(Team team)
    {
        TeamResponse teamResponse = new TeamResponse(team);
        teamResponse.setCountry(this.countryService.get(team.getCountryId()));
        return teamResponse;
    }

    public CompletionStage<List<Team>> getAll()
    {
        return this.teamRepository.getAll();
    }

    public TeamResponse get(Long id)
    {
        Team team = this.getRaw(id);
        if(null == team)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team"));
        }
        return teamResponse(team);
    }

    @Override
    public Team getRaw(Long id)
    {
        return this.teamRepository.get(id);
    }

    @Override
    public List<Team> get(List<Long> ids)
    {
        return this.teamRepository.get(ids);
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

        Country country = this.countryService.get(createRequest.getCountryId());
        if(null == country)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
        }

        return this.teamRepository.save(new Team(createRequest));
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

        if((null != updateRequest.getCountryId()) && (!updateRequest.getCountryId().equals(existingTeam.getCountryId())))
        {
            Country country = this.countryService.get(updateRequest.getCountryId());
            if(null == country)
            {
                throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
            }

            isUpdateRequired = true;
            existingTeam.setCountryId(country.getId());

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
