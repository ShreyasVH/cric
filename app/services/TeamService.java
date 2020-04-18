package services;

import models.Team;
import requests.teams.CreateRequest;
import requests.teams.UpdateRequest;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TeamService
{
    CompletionStage<List<Team>> getAll();

    CompletionStage<Team> get(Long id);

    CompletionStage<List<Team>> get(String keyword);

    CompletionStage<Team> create(CreateRequest createRequest);

    CompletionStage<Team> update(Long id, UpdateRequest updateRequest);
}
