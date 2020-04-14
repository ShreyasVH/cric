package services.impl;

import com.google.inject.Inject;
import repositories.StadiumRepository;
import responses.StadiumResponse;
import services.StadiumService;
import utils.Utils;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class StadiumServiceImpl implements StadiumService
{
    private final StadiumRepository stadiumRepository;

    @Inject
    public StadiumServiceImpl
    (
        StadiumRepository stadiumRepository
    )
    {
        this.stadiumRepository = stadiumRepository;
    }

    public CompletionStage<List<StadiumResponse>> getAll()
    {
        return this.stadiumRepository.getAll().thenApplyAsync(stadiums -> Utils.convertObjectList(stadiums, StadiumResponse.class));
    }
}
