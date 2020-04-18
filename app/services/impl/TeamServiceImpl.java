package services.impl;

import com.google.inject.Inject;

import enums.ErrorCode;
import exceptions.BadRequestException;
import models.Country;
import models.Team;
import repositories.CountryRepository;
import repositories.TeamRepository;
import requests.teams.CreateRequest;
import services.TeamService;
import utils.Utils;

import java.util.List;
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

    public CompletionStage<Team> get(Long id)
    {
        return this.teamRepository.get(id);
    }

    public CompletionStage<List<Team>> get(String keyword)
    {
        return this.teamRepository.get(keyword);
    }

    public CompletionStage<Team> create(CreateRequest createRequest)
    {
        createRequest.validate();

        CompletionStage<Team> response = this.teamRepository.get(createRequest.getName(), createRequest.getCountryId());
        return response.thenComposeAsync(existingTeam -> {
            if(null != existingTeam)
            {
                throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
            }

            CompletionStage<Country> countryResponse = this.countryRepository.get(createRequest.getCountryId());
            return countryResponse.thenComposeAsync(country -> {
                if(null == country)
                {
                    throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team"));
                }

                Team team = new Team(createRequest);
                team.setCountry(country);
                team.setCreatedAt(Utils.getCurrentDate());
                team.setUpdatedAt(Utils.getCurrentDate());
                return this.teamRepository.save(team);
            });
        });
    }
}
