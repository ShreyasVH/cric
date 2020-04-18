package services;

import models.Team;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TeamService
{
    CompletionStage<List<Team>> getAll();

    CompletionStage<Team> get(Long id);

    CompletionStage<List<Team>> get(String keyword);
}
