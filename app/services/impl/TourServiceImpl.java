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
import services.TourService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

        if(!StringUtils.isEmpty(updateRequest.getStartTime()))
        {
            try
            {
                Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updateRequest.getStartTime()));
                if(startTime.getTime() != existingTour.getStartTime().getTime())
                {
                    isUpdateRequired = true;
                    existingTour.setStartTime(startTime);
                }
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }
        }

        if(!StringUtils.isEmpty(updateRequest.getEndTime()))
        {
            try
            {
                Date endTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updateRequest.getEndTime()));
                if(endTime.getTime() != existingTour.getEndTime().getTime())
                {
                    isUpdateRequired = true;
                    existingTour.setEndTime(endTime);
                }
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }
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
}
