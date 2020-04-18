package services;

import models.Team;
import requests.teams.CreateRequest;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TeamService
{
    CompletionStage<List<Team>> getAll();

    CompletionStage<Team> get(Long id);

    CompletionStage<List<Team>> get(String keyword);

    CompletionStage<Team> create(CreateRequest createRequest);
}
