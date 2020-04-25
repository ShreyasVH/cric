package services;

import models.Match;

import java.util.concurrent.CompletionStage;

public interface MatchService
{
    CompletionStage<Match> get(Long id);
}
