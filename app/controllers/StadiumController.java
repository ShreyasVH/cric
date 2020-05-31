package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import requests.stadiums.CreateRequest;
import requests.stadiums.UpdateRequest;
import services.StadiumService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class StadiumController extends Controller
{
    private final StadiumService stadiumService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public StadiumController
    (
        StadiumService stadiumService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.stadiumService = stadiumService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> getAll()
    {
        return this.stadiumService.getAll().thenApplyAsync(list -> ok(Json.toJson(list)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> create(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            CreateRequest createStadiumRequest;
            try
            {
                createStadiumRequest = Utils.convertObject(request.body().asJson(), CreateRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.stadiumService.create(createStadiumRequest);
        }).thenApplyAsync(stadium -> ok(Json.toJson(stadium)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.stadiumService.get(id)).thenApplyAsync(stadium -> ok(Json.toJson(stadium)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getByKeyword(String keyword)
    {
        return CompletableFuture.supplyAsync(() -> this.stadiumService.get(keyword)).thenApplyAsync(stadiumList -> ok(Json.toJson(stadiumList)), this.httpExecutionContext.current());
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

            return this.stadiumService.update(id, updateRequest);
        }).thenApplyAsync(stadium -> ok(Json.toJson(stadium)), this.httpExecutionContext.current());
    }
}
