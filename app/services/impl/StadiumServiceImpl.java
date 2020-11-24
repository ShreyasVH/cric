package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import models.Country;
import models.Stadium;
import org.springframework.util.StringUtils;
import repositories.StadiumRepository;
import requests.stadiums.CreateRequest;
import requests.stadiums.UpdateRequest;
import responses.StadiumResponse;
import services.CountryService;
import services.StadiumService;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class StadiumServiceImpl implements StadiumService
{
    private final CountryService countryService;

    private final StadiumRepository stadiumRepository;

    @Inject
    public StadiumServiceImpl
    (
        CountryService countryService,

        StadiumRepository stadiumRepository
    )
    {
        this.countryService = countryService;

        this.stadiumRepository = stadiumRepository;
    }

    public StadiumResponse stadiumResponse(Stadium stadium)
    {
        StadiumResponse stadiumResponse = new StadiumResponse(stadium);
        stadiumResponse.setCountry(this.countryService.get(stadium.getCountryId()));

        return stadiumResponse;
    }

    public CompletionStage<List<Stadium>> getAll()
    {
        return this.stadiumRepository.getAll();
    }

    public Stadium create(CreateRequest createRequest)
    {
        createRequest.validate();

        Stadium existingStadium = this.stadiumRepository.get(createRequest.getName(), createRequest.getCountryId());

        if(null != existingStadium)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
        }

        Country country = this.countryService.get(createRequest.getCountryId());
        if(null == country)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
        }

        return this.stadiumRepository.save(new Stadium(createRequest));
    }

    public StadiumResponse get(Long id)
    {
        Stadium stadium = this.stadiumRepository.get(id);
        if(null == stadium)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Stadium"));
        }

        return stadiumResponse(stadium);
    }

    @Override
    public List<Stadium> get(String keyword) {
        return this.stadiumRepository.get(keyword);
    }

    @Override
    public Stadium update(Long id, UpdateRequest updateRequest)
    {
        updateRequest.validate();

        Stadium existingStadium = this.stadiumRepository.get(id);
        if(null == existingStadium)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Stadium"));
        }

        boolean isUpdateRequired = false;

        if(!StringUtils.isEmpty(updateRequest.getName()) && (!existingStadium.getName().equals(updateRequest.getName())))
        {
            existingStadium.setName(updateRequest.getName());
            isUpdateRequired = true;
        }

        if((!StringUtils.isEmpty(updateRequest.getCity())) && (!existingStadium.getCity().equals(updateRequest.getCity())))
        {
            existingStadium.setCity(updateRequest.getCity());
            isUpdateRequired = true;
        }

        if((!StringUtils.isEmpty(updateRequest.getState())) && (!existingStadium.getState().equals(updateRequest.getState())))
        {
            existingStadium.setState(updateRequest.getState());
            isUpdateRequired = true;
        }

        if(null != updateRequest.getCountryId() && (!updateRequest.getCountryId().equals(existingStadium.getCountryId())))
        {
            isUpdateRequired = true;
            Country country = this.countryService.get(updateRequest.getCountryId());
            if(null == country)
            {
                throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
            }

            existingStadium.setCountryId(country.getId());
        }

        if(isUpdateRequired)
        {
            return this.stadiumRepository.save(existingStadium);
        }
        else
        {
            return existingStadium;
        }
    }
}
