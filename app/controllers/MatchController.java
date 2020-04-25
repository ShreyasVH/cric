package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.MatchService;

import java.util.concurrent.CompletionStage;

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
        return this.matchService.get(id).thenApplyAsync(match -> ok(Json.toJson(match)), this.httpExecutionContext.current());
    }
}
