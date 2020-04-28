package services;

import models.Tour;
import requests.tours.CreateRequest;

public interface TourService
{
    public Tour get(Long id);

    public Tour create(CreateRequest createRequest);
}
