package services;

import models.Player;
import responses.PlayerResponse;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PlayerService
{
    CompletionStage<PlayerResponse> get(Long id);

    CompletionStage<List<Player>> get(String keyword);
}
