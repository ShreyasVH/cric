package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.SeriesService;

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
}
