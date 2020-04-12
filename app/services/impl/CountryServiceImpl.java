package services.impl;

import com.google.inject.Inject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import models.Country;

import org.springframework.util.StringUtils;
import repositories.CountryRepository;

import requests.CreateCountryRequest;
import requests.UpdateCountryRequest;
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
	public CompletionStage<Country> create(CreateCountryRequest createCountryRequest)
	{
		createCountryRequest.validate();

		CompletionStage<Country> response = this.countryRepository.get(createCountryRequest.getName());
		return response.thenComposeAsync(existingCountry -> {
			if(null != existingCountry)
			{
				throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
			}

			Country country = new Country(createCountryRequest);
			country.setCreatedAt(Utils.getCurrentDate());
			country.setUpdatedAt(Utils.getCurrentDate());
			return this.countryRepository.save(country);
		});
	}

    @Override
    public CompletionStage<Country> update(Long id, UpdateCountryRequest updateCountryRequest)
	{
		updateCountryRequest.validate();

		CompletionStage<Country> response = this.countryRepository.get(id);
		return response.thenComposeAsync(existingCountry -> {
			if(null == existingCountry)
			{
				throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
			}

			boolean isUpdateRequired = false;

			if((!StringUtils.isEmpty(updateCountryRequest.getName())) && (!updateCountryRequest.getName().equals(existingCountry.getName())))
			{
				existingCountry.setName(updateCountryRequest.getName());
				isUpdateRequired = true;
			}

			if(isUpdateRequired)
			{
				existingCountry.setUpdatedAt(Utils.getCurrentDate());
				return this.countryRepository.save(existingCountry);
			}
			else
			{
				return CompletableFuture.supplyAsync(() -> existingCountry);
			}
		});
    }
}