package services.impl;

import com.google.inject.Inject;

import java.util.List;
import java.util.concurrent.CompletionStage;

import models.Country;

import repositories.CountryRepository;

import services.CountryService;

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
}