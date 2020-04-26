package services;

import models.Match;
import requests.matches.CreateRequest;


public interface MatchService
{
    Match get(Long id);

    Match create(CreateRequest createRequest);
}
