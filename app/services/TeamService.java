package services;

import models.Team;
import requests.teams.CreateRequest;
import requests.teams.UpdateRequest;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TeamService
{
    CompletionStage<List<Team>> getAll();

    Team get(Long id);

    List<Team> get(String keyword);

    Team create(CreateRequest createRequest);

    Team update(Long id, UpdateRequest updateRequest);
}
