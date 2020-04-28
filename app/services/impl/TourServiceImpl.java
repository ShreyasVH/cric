package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import models.Tour;
import repositories.TourRepository;
import requests.tours.CreateRequest;
import services.TourService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TourServiceImpl implements TourService
{
    private final TourRepository tourRepository;

    @Inject
    public TourServiceImpl
    (
        TourRepository tourRepository
    )
    {
        this.tourRepository = tourRepository;
    }

    @Override
    public Tour get(Long id)
    {
        Tour tour = this.tourRepository.get(id);
        if(null == tour)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Tour"));
        }
        return tour;
    }

    @Override
    public Tour create(CreateRequest createRequest) {
        createRequest.validate();

        Tour existingTour = this.tourRepository.get(createRequest.getName());
        if(null != existingTour)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
        }

        Tour tour = new Tour();
        tour.setName(createRequest.getName());
        try
        {
            Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createRequest.getStartTime()));
            Date endTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createRequest.getEndTime()));

            tour.setStartTime(startTime);
            tour.setEndTime(endTime);

            return this.tourRepository.save(tour);
        }
        catch(Exception ex)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }
    }
}
