package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import models.Country;
import models.Stadium;
import repositories.CountryRepository;
import repositories.StadiumRepository;
import requests.stadiums.CreateRequest;
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

    public CompletionStage<Stadium> create(CreateRequest createRequest)
    {
        createRequest.validate();

        CompletionStage<Stadium> response = this.stadiumRepository.get(createRequest.getName(), createRequest.getCountryId());

        return response.thenComposeAsync(existingStadium -> {
            if(null != existingStadium)
            {
                throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
            }

            CompletionStage<Country> countryResponse = this.countryRepository.get(createRequest.getCountryId());
            return countryResponse.thenComposeAsync(country -> {
                Stadium stadium = new Stadium(createRequest);
                stadium.setCountry(country);
                return this.stadiumRepository.save(stadium);
            });
        });
    }
}
