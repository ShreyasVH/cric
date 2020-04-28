package services;

import models.Match;
import requests.matches.CreateRequest;
import requests.matches.UpdateRequest;


public interface MatchService
{
    Match get(Long id);

    Match create(CreateRequest createRequest);

    Match update(Long id, UpdateRequest updateRequest);
}
