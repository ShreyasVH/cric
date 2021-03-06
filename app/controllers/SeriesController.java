package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import requests.series.CreateRequest;
import requests.series.UpdateRequest;
import services.SeriesService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SeriesController extends Controller
{
    private final SeriesService seriesService;

    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public SeriesController
    (
        SeriesService seriesService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.seriesService = seriesService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> getAll()
    {
        return this.seriesService.getAll().thenApplyAsync(list -> ok(Json.toJson(list)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.seriesService.get(id)).thenApplyAsync(series -> ok(Json.toJson(series)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getByKeyword(String keyword)
    {
        return CompletableFuture.supplyAsync(() -> this.seriesService.get(keyword)).thenApplyAsync(seriesList -> ok(Json.toJson(seriesList)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> create(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            CreateRequest createTeamRequest;
            try
            {
                createTeamRequest = Utils.convertObject(request.body().asJson(), CreateRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.seriesService.create(createTeamRequest);
        }).thenApplyAsync(team -> ok(Json.toJson(team)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Long id, Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            UpdateRequest updateRequest;
            try
            {
                updateRequest = Utils.convertObject(request.body().asJson(), UpdateRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.seriesService.update(id, updateRequest);
        }).thenApplyAsync(team -> ok(Json.toJson(team)), this.httpExecutionContext.current());
    }
}
