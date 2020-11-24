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
import requests.tours.FilterRequest;
import requests.tours.UpdateRequest;
import responses.TourResponse;
import services.TourService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

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

            return this.tourService.update(id, updateRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(updatedTour -> ok(Json.toJson(updatedTour)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> filter(Http.Request request)
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
            return this.tourService.filter(filterRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(tours -> ok(Json.toJson(tours)), this.httpExecutionContext.current());
    }
}
