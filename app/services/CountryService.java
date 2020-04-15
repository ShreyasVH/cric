package services;

import java.util.List;

import models.Country;
import requests.CreateCountryRequest;
import requests.UpdateCountryRequest;

import java.util.concurrent.CompletionStage;

public interface CountryService
{
	CompletionStage<List<Country>> getAll();

	CompletionStage<Country> get(Long id);

	CompletionStage<Country> create(CreateCountryRequest createCountryRequest);

	CompletionStage<Country> update(Long id, UpdateCountryRequest updateCountryRequest);
}