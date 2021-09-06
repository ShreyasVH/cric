package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import requests.CreateCountryRequest;
import requests.stats.FilterRequest;
import services.CountryService;
import services.StatsService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class StatsController extends BaseController
{
    private final StatsService statsService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public StatsController
    (
        StatsService statsService,
        HttpExecutionContext httpExecutionContext
    )
    {
        this.statsService = statsService;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> getStats(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            FilterRequest filterRequest;
            try
            {
                filterRequest = Utils.convertObject(request.body().asJson(), FilterRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.statsService.getStats(filterRequest);
        }).thenApplyAsync(stats -> ok(Json.toJson(stats)), this.httpExecutionContext.current());
    }
}
