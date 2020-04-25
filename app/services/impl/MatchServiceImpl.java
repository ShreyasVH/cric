package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.NotFoundException;
import models.Match;
import repositories.MatchRepository;
import services.MatchService;

import java.util.concurrent.CompletionStage;

public class MatchServiceImpl implements MatchService
{
    private final MatchRepository matchRepository;

    @Inject
    public MatchServiceImpl
    (
        MatchRepository matchRepository
    )
    {
        this.matchRepository = matchRepository;
    }

    public CompletionStage<Match> get(Long id)
    {
        CompletionStage<Match> response = this.matchRepository.get(id);
        return response.thenApplyAsync(match -> {
            if(null == match)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Match"));
            }

            return match;
        });
    }
}
