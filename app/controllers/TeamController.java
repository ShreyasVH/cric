package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.TeamService;

import java.util.concurrent.CompletionStage;

public class TeamController extends Controller
{
    private final TeamService teamService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public TeamController
    (
        TeamService teamService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.teamService = teamService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> getAll()
    {
        return this.teamService.getAll().thenApplyAsync(list -> ok(Json.toJson(list)), this.httpExecutionContext.current());
    }
}
