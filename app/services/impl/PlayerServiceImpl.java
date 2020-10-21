package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import enums.GameType;
import exceptions.BadRequestException;
import exceptions.InternalServerError;
import models.Country;
import models.Player;
import org.springframework.util.StringUtils;
import repositories.CountryRepository;
import repositories.PlayerRepository;
import requests.players.CreateRequest;
import requests.players.UpdateRequest;
import responses.BattingStats;
import responses.BowlingStats;
import responses.FieldingStats;
import responses.PlayerResponse;
import services.PlayerService;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

public class PlayerServiceImpl implements PlayerService
{
    private final CountryRepository countryRepository;
    private final PlayerRepository playerRepository;

    @Inject
    public PlayerServiceImpl
    (
        CountryRepository countryRepository,
        PlayerRepository playerRepository
    )
    {
        this.countryRepository = countryRepository;
        this.playerRepository = playerRepository;
    }


    @Override
    public PlayerResponse get(Long id)
    {
        Player basicDetails = this.playerRepository.get(id);
        PlayerResponse playerResponse = new PlayerResponse(basicDetails);

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
    public List<Player> get(String keyword)
    {
        return this.playerRepository.get(keyword);
    }

    @Override
    public Player create(CreateRequest createRequest)
    {
        createRequest.validate();

        try
        {
            Date dateOfBirth = ((new SimpleDateFormat("yyyy-MM-dd")).parse(createRequest.getDateOfBirth()));
            Player existingPlayer = this.playerRepository.get(createRequest.getName(), createRequest.getCountryId(), dateOfBirth);
            if(null != existingPlayer)
            {
                throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
            }

            Country country = this.countryRepository.get(createRequest.getCountryId());
            if(null == country)
            {
                throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
            }

            Player player = new Player(createRequest);

            player.setCountry(country);

            return this.playerRepository.save(player);
        }
        catch(ParseException ex)
        {
            throw new InternalServerError(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
        }
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

        if((null != updateRequest.getCountryId()) && (!updateRequest.getCountryId().equals(existingPlayer.getCountry().getId())))
        {
            Country country = this.countryRepository.get(updateRequest.getCountryId());
            if(null == country)
            {
                throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
            }

            isUpdateRequired = true;
            existingPlayer.setCountry(country);
        }

        if(!StringUtils.isEmpty(updateRequest.getDateOfBirth()) && !((new SimpleDateFormat("yyyy-MM-dd")).format(existingPlayer.getDateOfBirth())).equals(updateRequest.getDateOfBirth()))
        {
            isUpdateRequired = true;
            try
            {
                Date dateOfBirth = (new SimpleDateFormat("yyyy-MM-dd")).parse(updateRequest.getDateOfBirth());
                existingPlayer.setDateOfBirth(dateOfBirth);
            }
            catch(ParseException exception)
            {
                throw new InternalServerError(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
            }
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
}
