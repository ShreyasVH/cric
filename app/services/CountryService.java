package services;

import java.util.List;
import requests.CreateCountryRequest;
import requests.UpdateCountryRequest;
import responses.CountryResponse;

import java.util.concurrent.CompletionStage;

public interface CountryService
{
	CompletionStage<List<CountryResponse>> getAll();

	CompletionStage<CountryResponse> get(Long id);

	CompletionStage<CountryResponse> create(CreateCountryRequest createCountryRequest);

	CompletionStage<CountryResponse> update(Long id, UpdateCountryRequest updateCountryRequest);
}