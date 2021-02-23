package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import models.Tour;
import org.springframework.util.StringUtils;
import repositories.TourRepository;
import requests.tours.CreateRequest;
import requests.tours.FilterRequest;
import requests.tours.UpdateRequest;
import responses.TourResponse;
import services.SeriesService;
import services.TourService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TourServiceImpl implements TourService
{
    private final TourRepository tourRepository;

    private final SeriesService seriesService;

    @Inject
    public TourServiceImpl
    (
        SeriesService seriesService,

        TourRepository tourRepository
    )
    {
        this.seriesService = seriesService;

        this.tourRepository = tourRepository;
    }

    public TourResponse tourResponse(Tour tour)
    {
        TourResponse tourResponse = new TourResponse(tour);
        tourResponse.setSeriesList(this.seriesService.getSeriesForTour(tour.getId()));

        return tourResponse;
    }

    @Override
    public TourResponse get(Long id)
    {
        Tour tour = this.tourRepository.get(id);
        if(null == tour)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Tour"));
        }
        return tourResponse(tour);
    }

    @Override
    public Tour create(CreateRequest createRequest) {
        createRequest.validate();

        Tour existingTour = this.tourRepository.get(createRequest.getName(), createRequest.getStartTime());
        if(null != existingTour)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
        }

        Tour tour = new Tour();
        tour.setName(createRequest.getName());
        tour.setStartTime(createRequest.getStartTime());

        return this.tourRepository.save(tour);
    }

    @Override
    public Tour update(Long id, UpdateRequest updateRequest)
    {
        updateRequest.validate();

        Tour existingTour = this.tourRepository.get(id);
        if(null == existingTour)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Tour"));
        }

        boolean isUpdateRequired = false;

        if((!StringUtils.isEmpty(updateRequest.getName())) && (!updateRequest.getName().equals(existingTour.getName())))
        {
            isUpdateRequired = true;
            existingTour.setName(updateRequest.getName());
        }

        if((null != updateRequest.getStartTime()) && !existingTour.getStartTime().equals(updateRequest.getStartTime()))
        {
            isUpdateRequired = true;
            existingTour.setStartTime(updateRequest.getStartTime());
        }

        if(isUpdateRequired)
        {
            return this.tourRepository.save(existingTour);
        }
        else
        {
            return existingTour;
        }
    }

    @Override
    public List<Tour> filter(FilterRequest filterRequest)
    {
        filterRequest.validate();
        return this.tourRepository.filter(filterRequest.getYear(), filterRequest.getOffset(), filterRequest.getCount());
    }

    @Override
    public List<Integer> getYears()
    {
        return this.tourRepository.getYears();
    }
}
