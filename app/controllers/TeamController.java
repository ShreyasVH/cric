package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import requests.teams.CreateRequest;
import requests.teams.UpdateRequest;
import services.TeamService;
import utils.Utils;

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

    public CompletionStage<Result> create(Http.Request request)
    {
        CreateRequest createTeamRequest = null;
        try
        {
            createTeamRequest = Utils.convertObject(request.body().asJson(), CreateRequest.class);
        }
        catch(Exception ex)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        return this.teamService.create(createTeamRequest).thenApplyAsync(team -> ok(Json.toJson(team)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Long id, Http.Request request)
    {
        UpdateRequest updateRequest = null;
        try
        {
            updateRequest = Utils.convertObject(request.body().asJson(), UpdateRequest.class);
        }
        catch(Exception ex)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        return this.teamService.update(id, updateRequest).thenApplyAsync(team -> ok(Json.toJson(team)), this.httpExecutionContext.current());
    }
}
