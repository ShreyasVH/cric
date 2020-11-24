package services;

import models.Team;
import requests.teams.CreateRequest;
import requests.teams.UpdateRequest;
import responses.TeamResponse;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TeamService
{
    CompletionStage<List<Team>> getAll();

    TeamResponse get(Long id);

    List<Team> get(String keyword);

    Team create(CreateRequest createRequest);

    Team update(Long id, UpdateRequest updateRequest);
}
