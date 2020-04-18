package services.impl;

import com.google.inject.Inject;
import models.Player;
import repositories.PlayerRepository;
import responses.BattingStats;
import responses.BowlingStats;
import responses.FieldingStats;
import responses.PlayerResponse;
import services.PlayerService;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class PlayerServiceImpl implements PlayerService
{
    private final PlayerRepository playerRepository;

    @Inject
    public PlayerServiceImpl
    (
        PlayerRepository playerRepository
    )
    {
        this.playerRepository = playerRepository;
    }


    @Override
    public CompletionStage<PlayerResponse> get(Long id)
    {
        CompletionStage<Player> response = this.playerRepository.get(id);
        return response.thenComposeAsync(basicDetails -> {
            PlayerResponse playerResponse = new PlayerResponse(basicDetails);

            CompletionStage<Map<String, Integer>> dismissalResponse = this.playerRepository.getDismissalStats(id);
            return dismissalResponse.thenComposeAsync(dismissalStats -> {
                playerResponse.setDismissalStats(dismissalStats);

                Integer dismissalCount = 0;
                for(String key: dismissalStats.keySet())
                {
                    dismissalCount += dismissalStats.get(key);
                }
                final Integer dismissalCountFinal = dismissalCount;

                CompletionStage<Map<String, Integer>> basicBattingStatsResponse = this.playerRepository.getBasicBattingStats(id);
                return basicBattingStatsResponse.thenComposeAsync(basicStats -> {
                    if(!basicStats.keySet().isEmpty())
                    {
                        BattingStats battingStats = new BattingStats(basicStats);
                        battingStats.setNotOuts(battingStats.getInnings() - dismissalCountFinal);

                        if(dismissalCountFinal > 0)
                        {
                            battingStats.setAverage(battingStats.getRuns() * 1.0 / dismissalCountFinal);
                        }

                        if(battingStats.getBalls() > 0)
                        {
                            battingStats.setStrikeRate(battingStats.getRuns() * 100.0 / battingStats.getBalls());
                        }

                        playerResponse.setBattingStats(battingStats);
                    }

                    CompletionStage<Map<String, Integer>> basicBowlingStatsResponse = this.playerRepository.getBasicBowlingStats(id);
                    return basicBowlingStatsResponse.thenComposeAsync(basicBowlingStats -> {

                        if(!basicBowlingStats.keySet().isEmpty())
                        {
                            BowlingStats bowlingStats = new BowlingStats(basicBowlingStats);

                            if(bowlingStats.getBalls() > 0)
                            {
                                bowlingStats.setEconomy(bowlingStats.getRuns() * 6.0 / bowlingStats.getBalls());

                                if(bowlingStats.getWickets() > 0)
                                {
                                    bowlingStats.setAverage(bowlingStats.getRuns() * 1.0 / bowlingStats.getWickets());

                                    bowlingStats.setStrikeRate(bowlingStats.getBalls() * 1.0 / bowlingStats.getWickets());
                                }
                            }

                            playerResponse.setBowlingStats(bowlingStats);
                        }


                        CompletionStage<Map<String, Integer>> fieldingStatsResponse = this.playerRepository.getFieldingStats(id);
                        return fieldingStatsResponse.thenApplyAsync(fieldingStats -> {
                            if(!fieldingStats.keySet().isEmpty())
                            {
                                playerResponse.setFieldingStats(new FieldingStats(fieldingStats));
                            }
                            return playerResponse;
                        });
                    });
                });
            });
        });
    }
}
