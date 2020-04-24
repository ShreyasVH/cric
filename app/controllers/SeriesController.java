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
import services.SeriesService;
import utils.Utils;

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
        return this.seriesService.get(id).thenApplyAsync(series -> ok(Json.toJson(series)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> create(Http.Request request)
    {
        CreateRequest createTeamRequest = null;
        try
        {
            createTeamRequest = Utils.convertObject(request.body().asJson(), CreateRequest.class);
        }
        catch(Exception ex)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        return this.seriesService.create(createTeamRequest).thenApplyAsync(team -> ok(Json.toJson(team)), this.httpExecutionContext.current());
    }
}
