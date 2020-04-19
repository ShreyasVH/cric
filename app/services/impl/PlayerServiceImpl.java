package services.impl;

import com.google.inject.Inject;
import enums.GameType;
import models.Player;
import repositories.PlayerRepository;
import responses.BattingStats;
import responses.BowlingStats;
import responses.FieldingStats;
import responses.PlayerResponse;
import services.PlayerService;

import java.util.HashMap;
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

            CompletionStage<Map<GameType, Map<String, Integer>>> dismissalResponse = this.playerRepository.getDismissalStats(id);
            return dismissalResponse.thenComposeAsync(dismissalStats -> {
                playerResponse.setDismissalStats(dismissalStats);

                Map<GameType, Integer> dismissalCountMap = new HashMap<>();
                for(GameType gameType: GameType.values())
                {
                    Integer dismissalCount = 0;
                    if(dismissalStats.containsKey(gameType))
                    {
                        for(String key: dismissalStats.get(gameType).keySet())
                        {
                            dismissalCount += dismissalStats.get(gameType).get(key);
                        }
                    }
                    dismissalCountMap.put(gameType, dismissalCount);
                }

                final Map<GameType, Integer> dismissalCountMapFinal = dismissalCountMap;

                CompletionStage<Map<GameType, Map<String, Integer>>> basicBattingStatsResponse = this.playerRepository.getBasicBattingStats(id);
                return basicBattingStatsResponse.thenComposeAsync(basicStatsMap -> {
                    if(!basicStatsMap.keySet().isEmpty())
                    {
                        Map<GameType, BattingStats> battingStatsMap = new HashMap<>();

                        for(GameType gameType: basicStatsMap.keySet())
                        {
                            BattingStats battingStats = new BattingStats(basicStatsMap.get(gameType));
                            battingStats.setNotOuts(battingStats.getInnings() - dismissalCountMapFinal.get(gameType));

                            if(dismissalCountMapFinal.get(gameType) > 0)
                            {
                                battingStats.setAverage(battingStats.getRuns() * 1.0 / dismissalCountMapFinal.get(gameType));
                            }

                            if(battingStats.getBalls() > 0)
                            {
                                battingStats.setStrikeRate(battingStats.getRuns() * 100.0 / battingStats.getBalls());
                            }

                            battingStatsMap.put(gameType, battingStats);
                        }

                        playerResponse.setBattingStats(battingStatsMap);
                    }

                    CompletionStage<Map<GameType, Map<String, Integer>>> basicBowlingStatsResponse = this.playerRepository.getBasicBowlingStats(id);
                    return basicBowlingStatsResponse.thenComposeAsync(basicBowlingStatsMap -> {
                        if(!basicBowlingStatsMap.keySet().isEmpty())
                        {
                            Map<GameType, BowlingStats> bowlingStatsFinal = new HashMap<>();

                            for(GameType gameType: basicBowlingStatsMap.keySet())
                            {
                                BowlingStats bowlingStats = new BowlingStats(basicBowlingStatsMap.get(gameType));

                                if(bowlingStats.getBalls() > 0)
                                {
                                    bowlingStats.setEconomy(bowlingStats.getRuns() * 6.0 / bowlingStats.getBalls());

                                    if(bowlingStats.getWickets() > 0)
                                    {
                                        bowlingStats.setAverage(bowlingStats.getRuns() * 1.0 / bowlingStats.getWickets());

                                        bowlingStats.setStrikeRate(bowlingStats.getBalls() * 1.0 / bowlingStats.getWickets());
                                    }
                                }

                                bowlingStatsFinal.put(gameType, bowlingStats);
                            }

                            playerResponse.setBowlingStats(bowlingStatsFinal);
                        }

                        CompletionStage<Map<GameType, Map<String, Integer>>> fieldingStatsResponse = this.playerRepository.getFieldingStats(id);
                        return fieldingStatsResponse.thenApplyAsync(fieldingStatsMap -> {
                            if(!fieldingStatsMap.keySet().isEmpty())
                            {
                                Map<GameType, FieldingStats> fieldingStatsMapFinal = new HashMap<>();
                                for(GameType gameType: fieldingStatsMap.keySet())
                                {
                                    FieldingStats fieldingStats = new FieldingStats(fieldingStatsMap.get(gameType));
                                    fieldingStatsMapFinal.put(gameType, fieldingStats);
                                }

                                playerResponse.setFieldingStats(fieldingStatsMapFinal);
                            }
                            return playerResponse;
                        });
                    });
                });
            });
        });
    }

    @Override
    public CompletionStage<List<Player>> get(String keyword)
    {
        return this.playerRepository.get(keyword);
    }
}
