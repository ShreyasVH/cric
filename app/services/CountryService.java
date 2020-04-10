package services;

import java.util.List;
import models.Country;
import java.util.concurrent.CompletionStage;

public interface CountryService
{
	CompletionStage<List<Country>> getAll();
}