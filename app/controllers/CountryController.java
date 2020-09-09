package controllers;

import enums.ErrorCode;
import exceptions.BadRequestException;
import play.mvc.Http;
import play.mvc.Result;
import play.libs.Json;
import java.net.URLDecoder;

import com.google.inject.Inject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;

import requests.CreateCountryRequest;
import requests.UpdateCountryRequest;
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
		return CompletableFuture.supplyAsync(() -> this.countryService.get(id)).thenApplyAsync(country -> ok(Json.toJson(country)), this.httpExecutionContext.current());
	}

    public CompletionStage<Result> getByName(String name)
    {
        return CompletableFuture.supplyAsync(() -> this.countryService.get(URLDecoder.decode(name))).thenApplyAsync(country -> ok(Json.toJson(country)), this.httpExecutionContext.current());
    }

	public CompletionStage<Result> create(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            CreateCountryRequest createCountryRequest;
            try
            {
                createCountryRequest = Utils.convertObject(request.body().asJson(), CreateCountryRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.countryService.create(createCountryRequest);
        }).thenApplyAsync(country -> ok(Json.toJson(country)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Long id, Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            UpdateCountryRequest updateCountryRequest = null;
            try
            {
                updateCountryRequest = Utils.convertObject(request.body().asJson(), UpdateCountryRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.countryService.update(id, updateCountryRequest);
        }).thenApplyAsync(country -> ok(Json.toJson(country)), this.httpExecutionContext.current());
    }
}