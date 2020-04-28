package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import requests.tours.CreateRequest;
import services.TourService;
import utils.Utils;

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

    public CompletionStage<Result> create(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            CreateRequest createRequest;
            try
            {
                createRequest = Utils.convertObject(request.body().asJson(), CreateRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.tourService.create(createRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(tour -> ok(Json.toJson(tour)), this.httpExecutionContext.current());
    }
}
