package services;

import java.util.List;

import models.Country;
import requests.CreateCountryRequest;
import requests.UpdateCountryRequest;

import java.util.concurrent.CompletionStage;

public interface CountryService
{
	CompletionStage<List<Country>> getAll();

	Country get(Long id);

	Country create(CreateCountryRequest createCountryRequest);

	Country update(Long id, UpdateCountryRequest updateCountryRequest);
}