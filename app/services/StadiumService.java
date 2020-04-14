package services;

import responses.StadiumResponse;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface StadiumService
{
    CompletionStage<List<StadiumResponse>> getAll();
}
