package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import models.Country;
import models.Stadium;
import org.springframework.util.StringUtils;
import repositories.CountryRepository;
import repositories.StadiumRepository;
import requests.stadiums.CreateRequest;
import requests.stadiums.UpdateRequest;
import services.StadiumService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
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
                if(null == country)
                {
                    throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
                }

                Stadium stadium = new Stadium(createRequest);
                stadium.setCountry(country);
                return this.stadiumRepository.save(stadium);
            });
        });
    }

    public CompletionStage<Stadium> get(Long id)
    {
        return this.stadiumRepository.get(id);
    }

    @Override
    public CompletionStage<Stadium> update(Long id, UpdateRequest updateRequest)
    {
        updateRequest.validate();

        CompletionStage<Stadium> response = this.stadiumRepository.get(id);
        return response.thenComposeAsync(existingStadium -> {
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

            if((!StringUtils.isEmpty(updateRequest.getCity())) && ((null == existingStadium.getCity()) || (!existingStadium.getCity().equals(updateRequest.getCity()))))
            {
                existingStadium.setCity(updateRequest.getCity());
                isUpdateRequired = true;
            }

            if((!StringUtils.isEmpty(updateRequest.getState())) && ((null == existingStadium.getState()) || (!existingStadium.getState().equals(updateRequest.getState()))))
            {
                existingStadium.setState(updateRequest.getState());
                isUpdateRequired = true;
            }

            if(null != updateRequest.getCountryId())
            {
                CompletionStage<Country> countryResponse = this.countryRepository.get(updateRequest.getCountryId());
                return countryResponse.thenComposeAsync(country -> {
                    if(null == country)
                    {
                        throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
                    }

                    existingStadium.setCountry(country);
                    return this.stadiumRepository.save(existingStadium);
                });
            }
            else
            {
                if(isUpdateRequired)
                {
                    return this.stadiumRepository.save(existingStadium);
                }
                else
                {
                    return CompletableFuture.supplyAsync(() -> existingStadium);
                }
            }
        });
    }
}
