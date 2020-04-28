package services;

import models.Tour;
import requests.tours.CreateRequest;
import requests.tours.FilterRequest;
import requests.tours.UpdateRequest;

import java.util.List;

public interface TourService
{
    Tour get(Long id);

    Tour create(CreateRequest createRequest);

    Tour update(Long id, UpdateRequest updateRequest);

    List<Tour> filter(FilterRequest filterRequest);
}
