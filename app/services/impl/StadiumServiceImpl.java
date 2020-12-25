package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import models.Country;
import models.Stadium;
import org.springframework.util.StringUtils;
import repositories.CountryRepository;
import repositories.StadiumRepository;
import requests.stadiums.CreateRequest;
import requests.stadiums.UpdateRequest;
import services.StadiumService;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class StadiumServiceImpl implements StadiumService
{
    private final CountryRepository countryRepository;
    private final StadiumRepository stadiumRepository;

    @Inject
    public StadiumServiceImpl
    (
        CountryRepository countryRepository,
        StadiumRepository stadiumRepository
    )
    {
        this.countryRepository = countryRepository;
        this.stadiumRepository = stadiumRepository;
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

        Country country = this.countryRepository.get(createRequest.getCountryId());
        if(null == country)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
        }

        Stadium stadium = new Stadium(createRequest);
        stadium.setCountry(country);
        return this.stadiumRepository.save(stadium);
    }

    public Stadium get(Long id)
    {
        Stadium stadium = this.stadiumRepository.get(id);
        if(null == stadium)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Stadium"));
        }

        return stadium;
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

        if((!StringUtils.isEmpty(updateRequest.getCity())) && (!updateRequest.getCity().equals(existingStadium.getCity())))
        {
            existingStadium.setCity(updateRequest.getCity());
            isUpdateRequired = true;
        }

        if((!StringUtils.isEmpty(updateRequest.getState())) && (!updateRequest.getState().equals(existingStadium.getState())))
        {
            existingStadium.setState(updateRequest.getState());
            isUpdateRequired = true;
        }

        if(null != updateRequest.getCountryId() && (!updateRequest.getCountryId().equals(existingStadium.getCountry().getId())))
        {
            isUpdateRequired = true;
            Country country = this.countryRepository.get(updateRequest.getCountryId());
            if(null == country)
            {
                throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
            }

            existingStadium.setCountry(country);
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
