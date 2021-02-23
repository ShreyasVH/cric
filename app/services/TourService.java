package services;

import models.Tour;
import requests.tours.CreateRequest;
import requests.tours.FilterRequest;
import requests.tours.UpdateRequest;
import responses.TourResponse;

import java.util.List;

public interface TourService
{
    TourResponse get(Long id);

    Tour create(CreateRequest createRequest);

    Tour update(Long id, UpdateRequest updateRequest);

    List<Tour> filter(FilterRequest filterRequest);

    List<Integer> getYears();
}
