package services;

import models.Player;
import requests.players.CreateRequest;
import requests.players.UpdateRequest;
import responses.PlayerResponse;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PlayerService
{
    CompletionStage<PlayerResponse> get(Long id);

    CompletionStage<List<Player>> get(String keyword);

    CompletionStage<Player> create(CreateRequest createRequest);

    CompletionStage<Player> update(Long id, UpdateRequest updateRequest);
}
