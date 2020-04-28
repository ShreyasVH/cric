package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import models.Tour;
import repositories.TourRepository;
import services.TourService;

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
}
