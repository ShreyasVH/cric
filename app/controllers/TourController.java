package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.TourService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class TourController extends Controller
{
    private final TourService tourService;

    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public TourController
    (
        TourService tourService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.tourService = tourService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.tourService.get(id), this.httpExecutionContext.current()).thenApplyAsync(tour -> ok(Json.toJson(tour)), this.httpExecutionContext.current());
    }
}
