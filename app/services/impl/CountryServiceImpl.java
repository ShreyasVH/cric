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
    public Country get(Long id)
	{
		Country country = this.countryRepository.get(id);
		if(null == country)
		{
			throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
		}

		return country;
    }

	@Override
	public Country get(String name)
	{
		Country country = this.countryRepository.get(name);
		if(null == country)
		{
			throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
		}

		return country;
	}

	@Override
	public Country create(CreateCountryRequest createCountryRequest)
	{
		createCountryRequest.validate();

		Country existingCountry = this.countryRepository.get(createCountryRequest.getName());
		if(null != existingCountry)
		{
			throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
		}

		Country country = new Country(createCountryRequest);
		return this.countryRepository.save(country);
	}

    @Override
    public Country update(Long id, UpdateCountryRequest updateCountryRequest)
	{
		updateCountryRequest.validate();

		Country existingCountry = this.countryRepository.get(id);
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
			existingCountry = this.countryRepository.save(existingCountry);
		}

		return existingCountry;
    }
}