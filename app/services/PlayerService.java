package services;

import responses.PlayerResponse;

import java.util.concurrent.CompletionStage;

public interface PlayerService
{
    CompletionStage<PlayerResponse> get(Long id);
}
