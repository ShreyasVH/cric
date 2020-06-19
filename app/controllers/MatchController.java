package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import requests.matches.CreateRequest;
import requests.matches.UpdateRequest;
import services.MatchService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.Map;
import java.util.HashMap;

public class MatchController extends Controller
{
    private final MatchService matchService;

    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public MatchController
    (
        MatchService matchService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.matchService = matchService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.matchService.get(id)).thenApplyAsync(match -> ok(Json.toJson(match)), this.httpExecutionContext.current());
    }

    public CompletableFuture<Result> create(Http.Request request)
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

            return this.matchService.create(createRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(match -> ok(Json.toJson(match)), this.httpExecutionContext.current());
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

            return this.matchService.update(id, updateRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(updatedMatch -> ok(Json.toJson(updatedMatch)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> delete(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.matchService.delete(id)).thenApplyAsync(isSuccess -> {
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", isSuccess);
            return ok(Json.toJson(response));
        }, this.httpExecutionContext.current());
    }
}
