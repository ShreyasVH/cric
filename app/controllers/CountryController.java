package controllers;

import play.mvc.Result;
import play.libs.Json;

import com.google.inject.Inject;
import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;

import services.CountryService;

public class CountryController extends BaseController
{
	private final CountryService countryService;
	private final HttpExecutionContext httpExecutionContext;

	@Inject
	public CountryController
	(
		CountryService countryService,
		HttpExecutionContext httpExecutionContext
	)
	{
		this.countryService = countryService;
		this.httpExecutionContext = httpExecutionContext;
	}

	public CompletionStage<Result> getAll()
	{
		return this.countryService.getAll().thenApplyAsync(list -> ok(Json.toJson(list)), this.httpExecutionContext.current());
	}

	public CompletionStage<Result> get(Long id)
	{
		return this.countryService.get(id).thenApplyAsync(country -> ok(Json.toJson(country)), this.httpExecutionContext.current());
	}
}