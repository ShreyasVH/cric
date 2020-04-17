package services;

import models.Stadium;
import requests.stadiums.CreateRequest;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface StadiumService
{
    CompletionStage<List<Stadium>> getAll();

    CompletionStage<Stadium> create(CreateRequest createRequest);

    CompletionStage<Stadium> get(Long id);
}
