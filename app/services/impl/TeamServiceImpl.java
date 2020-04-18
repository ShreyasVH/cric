package services.impl;

import com.google.inject.Inject;

import models.Team;
import repositories.TeamRepository;
import services.TeamService;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class TeamServiceImpl implements TeamService
{
    private final TeamRepository teamRepository;

    @Inject
    public TeamServiceImpl
    (
        TeamRepository teamRepository
    )
    {
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
}
