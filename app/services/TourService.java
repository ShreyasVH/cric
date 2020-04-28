package services;

import models.Tour;
import requests.tours.CreateRequest;
import requests.tours.UpdateRequest;

public interface TourService
{
    Tour get(Long id);

    Tour create(CreateRequest createRequest);

    Tour update(Long id, UpdateRequest updateRequest);
}
