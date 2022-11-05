package services;

import models.Player;
import requests.players.BattingScoreRequest;
import requests.players.CreateRequest;
import requests.players.UpdateRequest;
import responses.BattingScoreMiniResponse;
import responses.PlayerResponse;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PlayerService
{
    PlayerResponse get(Long id);

    Player getRaw(Long id);

    List<Player> get(String keyword);

    Player create(CreateRequest createRequest);

    Player update(Long id, UpdateRequest updateRequest);

    List<Player> getAll(int offset, int count);

    List<BattingScoreMiniResponse> getScores(BattingScoreRequest request);
}
