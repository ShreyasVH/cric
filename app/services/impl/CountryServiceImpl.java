package services.impl;

import com.google.inject.Inject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import models.Country;

import repositories.CountryRepository;

import requests.CountryRequest;
import services.CountryService;
import utils.Utils;

public class CountryServiceImpl implements CountryService
{
	private final CountryRepository countryRepository;
	
	@Inject
	public CountryServiceImpl
	(
		CountryRepository countryRepository
	)
	{
		this.countryRepository = countryRepository;
	}

	public CompletionStage<List<Country>> getAll()
	{
		return this.countryRepository.getAll();
	}

    @Override
    public CompletionStage<Country> get(Long id)
	{
		CompletionStage<Country> response = this.countryRepository.get(id);
		return response.thenApplyAsync(country -> {
			if(null == country)
			{
				throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
			}

			return country;
		});
    }

	@Override
	public CompletionStage<Country> create(CountryRequest countryRequest)
	{
		countryRequest.validate();

		CompletionStage<Country> response = this.countryRepository.get(countryRequest.getName());
		return response.thenComposeAsync(existingCountry -> {
			if(null != existingCountry)
			{
				throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
			}

			Country country = new Country(countryRequest);
			country.setCreatedAt(Utils.getCurrentDate());
			country.setUpdatedAt(Utils.getCurrentDate());
			return this.countryRepository.create(country);
		});
	}
}