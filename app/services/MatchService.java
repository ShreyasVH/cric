package services;

import models.Match;
import requests.matches.CreateRequest;
import requests.matches.UpdateRequest;
import responses.MatchResponse;


public interface MatchService
{
    MatchResponse get(Long id);

    Match create(CreateRequest createRequest);

    Match update(Long id, UpdateRequest updateRequest);

    boolean delete(Long id);
}
