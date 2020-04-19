package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import requests.players.CreateRequest;
import services.PlayerService;
import utils.Utils;

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
        return this.playerService.get(id).thenApplyAsync(player -> ok(Json.toJson(player)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getByKeyword(String keyword)
    {
        return this.playerService.get(keyword).thenApplyAsync(player -> ok(Json.toJson(player)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> create(Http.Request request)
    {
        CreateRequest createRequest = null;
        try
        {
            createRequest = Utils.convertObject(request.body().asJson(), CreateRequest.class);
        }
        catch(Exception ex)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        return this.playerService.create(createRequest).thenApplyAsync(player -> ok(Json.toJson(player)), this.httpExecutionContext.current());
    }
}
