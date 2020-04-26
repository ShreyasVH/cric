package services;

import models.Stadium;
import requests.stadiums.CreateRequest;
import requests.stadiums.UpdateRequest;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface StadiumService
{
    CompletionStage<List<Stadium>> getAll();

    Stadium create(CreateRequest createRequest);

    Stadium get(Long id);

    Stadium update(Long id, UpdateRequest updateRequest);
}
