package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.StadiumService;

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

}
