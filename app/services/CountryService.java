package services;

import java.util.List;
import models.Country;
import requests.CountryRequest;

import java.util.concurrent.CompletionStage;

public interface CountryService
{
	CompletionStage<List<Country>> getAll();

	CompletionStage<Country> get(Long id);

	CompletionStage<Country> create(CountryRequest countryRequest);
}