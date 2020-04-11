package controllers;

import enums.ErrorCode;
import exceptions.BadRequestException;
import play.mvc.Http;
import play.mvc.Result;
import play.libs.Json;

import com.google.inject.Inject;
import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;

import requests.CountryRequest;
import services.CountryService;
import utils.Utils;

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

	public CompletionStage<Result> create(Http.Request request)
    {
        CountryRequest countryRequest = null;
        try
        {
			countryRequest = Utils.convertObject(request.body().asJson(), CountryRequest.class);
        }
        catch(Exception ex)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        return this.countryService.create(countryRequest).thenApplyAsync(country -> ok(Json.toJson(country)), this.httpExecutionContext.current());
    }
}