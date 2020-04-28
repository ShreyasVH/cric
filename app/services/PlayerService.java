package services;

import models.Player;
import requests.players.CreateRequest;
import requests.players.UpdateRequest;
import responses.PlayerResponse;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PlayerService
{
    PlayerResponse get(Long id);

    List<Player> get(String keyword);

    Player create(CreateRequest createRequest);

    Player update(Long id, UpdateRequest updateRequest);
}
