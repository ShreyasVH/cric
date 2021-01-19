package services;

import models.Stadium;
import requests.stadiums.CreateRequest;
import requests.stadiums.UpdateRequest;
import responses.StadiumResponse;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface StadiumService
{
    CompletionStage<List<Stadium>> getAll();

    Stadium create(CreateRequest createRequest);

    StadiumResponse get(Long id);

    List<Stadium> get(String keyword);

    Stadium update(Long id, UpdateRequest updateRequest);
}
