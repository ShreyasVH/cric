package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.PlayerService;

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
}
