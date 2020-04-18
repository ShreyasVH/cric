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

    public CompletionStage<Result> get(Long id)
    {
        return this.teamService.get(id).thenApplyAsync(team -> ok(Json.toJson(team)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getByKeyword(String keyword)
    {
        return this.teamService.get(keyword).thenApplyAsync(list -> ok(Json.toJson(list)), this.httpExecutionContext.current());
    }
}
