package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import enums.GameType;
import exceptions.BadRequestException;
import models.Country;
import models.Player;
import org.springframework.util.StringUtils;
import repositories.PlayerRepository;
import requests.players.BattingScoreRequest;
import requests.players.CreateRequest;
import requests.players.UpdateRequest;
import responses.*;
import services.CountryService;
import services.PlayerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerServiceImpl implements PlayerService
{
    private final CountryService countryService;

    private final PlayerRepository playerRepository;

    @Inject
    public PlayerServiceImpl
    (
        CountryService countryService,

        PlayerRepository playerRepository
    )
    {
        this.countryService = countryService;

        this.playerRepository = playerRepository;
    }


    @Override
    public PlayerResponse get(Long id)
    {
        Player basicDetails = this.getRaw(id);
        PlayerResponse playerResponse = new PlayerResponse(basicDetails);

        playerResponse.setCountry(this.countryService.get(basicDetails.getCountryId()));

        Map<GameType, Map<String, Integer>> dismissalStats = this.playerRepository.getDismissalStats(id);
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

        Map<GameType, Map<String, Integer>> basicStatsMap = this.playerRepository.getBasicBattingStats(id);
        if(!basicStatsMap.keySet().isEmpty())
        {
            Map<GameType, BattingStats> battingStatsMap = new HashMap<>();

            for(GameType gameType: basicStatsMap.keySet())
            {
                BattingStats battingStats = new BattingStats(basicStatsMap.get(gameType));
                battingStats.setNotOuts(battingStats.getInnings() - dismissalCountMap.get(gameType));

                if(dismissalCountMap.get(gameType) > 0)
                {
                    battingStats.setAverage(battingStats.getRuns() * 1.0 / dismissalCountMap.get(gameType));
                }

                if(battingStats.getBalls() > 0)
                {
                    battingStats.setStrikeRate(battingStats.getRuns() * 100.0 / battingStats.getBalls());
                }

                battingStatsMap.put(gameType, battingStats);
            }

            playerResponse.setBattingStats(battingStatsMap);
        }

        Map<GameType, Map<String, Integer>> basicBowlingStatsMap = this.playerRepository.getBasicBowlingStats(id);
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

        Map<GameType, Map<String, Integer>> fieldingStatsMap = this.playerRepository.getFieldingStats(id);
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
    }

    @Override
    public Player getRaw(Long id)
    {
        return this.playerRepository.get(id);
    }

    @Override
    public List<Player> get(String keyword)
    {
        return this.playerRepository.get(keyword);
    }

    @Override
    public Player create(CreateRequest createRequest)
    {
        createRequest.validate();

        Player existingPlayer = this.playerRepository.get(createRequest.getName(), createRequest.getCountryId(), createRequest.getDateOfBirth());
        if(null != existingPlayer)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
        }

        Country country = this.countryService.get(createRequest.getCountryId());
        if(null == country)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
        }

        return this.playerRepository.save(new Player(createRequest));
    }

    @Override
    public Player update(Long id, UpdateRequest updateRequest)
    {
        updateRequest.validate();

        Player existingPlayer = this.playerRepository.get(id);
        if(null == existingPlayer)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Player"));
        }
        Player updatedPlayer = existingPlayer;

        boolean isUpdateRequired = false;

        if(!StringUtils.isEmpty(updateRequest.getName()) && !existingPlayer.getName().equals(updateRequest.getName()))
        {
            isUpdateRequired = true;
            existingPlayer.setName(updateRequest.getName());
        }

        if(!StringUtils.isEmpty(updateRequest.getImage()) && !existingPlayer.getImage().equals(updateRequest.getImage()))
        {
            isUpdateRequired = true;
            existingPlayer.setImage(updateRequest.getImage());
        }

        if((null != updateRequest.getCountryId()) && (!updateRequest.getCountryId().equals(existingPlayer.getCountryId())))
        {
            Country country = this.countryService.get(updateRequest.getCountryId());
            if(null == country)
            {
                throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
            }

            isUpdateRequired = true;
            existingPlayer.setCountryId(country.getId());
        }

        if(null != updateRequest.getDateOfBirth() && !updatedPlayer.getDateOfBirth().equals(updateRequest.getDateOfBirth()))
        {
            isUpdateRequired = true;
            existingPlayer.setDateOfBirth(updateRequest.getDateOfBirth());
        }

        if(isUpdateRequired)
        {
            updatedPlayer = this.playerRepository.save(existingPlayer);
        }

        return updatedPlayer;
    }

    @Override
    public List<Player> getAll(int offset, int count)
    {
        return this.playerRepository.getAll(offset, count);
    }

    @Override
    public List<BattingScoreMiniResponse> getScores(BattingScoreRequest request)
    {
        return this.playerRepository.getBattingScores(request);
    }
}
