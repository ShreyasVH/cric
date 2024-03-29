package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import requests.players.BattingScoreRequest;
import requests.players.CreateRequest;
import requests.players.UpdateRequest;
import services.PlayerService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class PlayerController extends Controller
{
    private final PlayerService playerService;

    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public PlayerController
    (
        PlayerService playerService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.playerService = playerService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.playerService.get(id)).thenApplyAsync(player -> ok(Json.toJson(player)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getByKeyword(String keyword)
    {
        return CompletableFuture.supplyAsync(() -> this.playerService.get(keyword)).thenApplyAsync(player -> ok(Json.toJson(player)), this.httpExecutionContext.current());
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

            return this.playerService.create(createRequest);
        }).thenApplyAsync(player -> ok(Json.toJson(player)), this.httpExecutionContext.current());
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

            return this.playerService.update(id, updateRequest);
        }).thenApplyAsync(player -> ok(Json.toJson(player)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getAll(Integer offset, Integer count)
    {
        return CompletableFuture.supplyAsync(() -> this.playerService.getAll(offset, count)).thenApplyAsync(players -> ok(Json.toJson(players)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getScores(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            BattingScoreRequest scoreRequest;
            try
            {
                scoreRequest = Utils.convertObject(request.body().asJson(), BattingScoreRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.playerService.getScores(scoreRequest);
        }).thenApplyAsync(player -> ok(Json.toJson(player)), this.httpExecutionContext.current());
    }
}
