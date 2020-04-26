package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import enums.ExtrasType;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.*;
import repositories.*;
import requests.matches.CreateRequest;
import services.MatchService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionStage;

public class MatchServiceImpl implements MatchService
{
    private final DismissalRepository dismissalRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final SeriesRepository seriesRepository;
    private final StadiumRepository stadiumRepository;
    private final TeamRepository teamRepository;

    @Inject
    public MatchServiceImpl
    (
        DismissalRepository dismissalRepository,
        MatchRepository matchRepository,
        PlayerRepository playerRepository,
        SeriesRepository seriesRepository,
        StadiumRepository stadiumRepository,
        TeamRepository teamRepository
    )
    {
        this.dismissalRepository = dismissalRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.seriesRepository = seriesRepository;
        this.stadiumRepository = stadiumRepository;
        this.teamRepository = teamRepository;
    }

    public Match get(Long id)
    {
        Match match = this.matchRepository.get(id);
        if(null == match)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Match"));
        }

        return match;
    }

    public Match create(CreateRequest createRequest)
    {
        createRequest.validate();

        Match match = new Match();
        match.setResult(createRequest.getResult());
        match.setWinMargin(createRequest.getWinMargin());
        match.setWinMarginType(createRequest.getWinMarginType());

        try
        {
            Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(createRequest.getStartTime());
            Date endTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(createRequest.getEndTime());
            match.setStartTime(startTime);
            match.setEndTime(endTime);
        }
        catch(Exception ex)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        match.setTag(createRequest.getTag());

        Series series = this.seriesRepository.get(createRequest.getSeriesId());
        if(null == series)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Series"));
        }
        match.setSeries(series);

        Team team1 = this.teamRepository.get(createRequest.getTeam1());
        if(null == team1)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team 1"));
        }
        match.setTeam1(team1);

        Team team2 = this.teamRepository.get(createRequest.getTeam2());
        if(null == team2)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team 2"));
        }
        match.setTeam2(team2);

        Team tossWinner = this.teamRepository.get(createRequest.getTossWinner());
        if(null == tossWinner)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Toss Winner Team"));
        }
        match.setTossWinner(tossWinner);

        Team battingFirst = this.teamRepository.get(createRequest.getBatFirst());
        if(null == battingFirst)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Batting First Team"));
        }
        match.setBattingFirst(battingFirst);

        Team winner = this.teamRepository.get(createRequest.getWinner());
        if(null == winner)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Winner Team"));
        }
        match.setWinner(winner);

        Stadium stadium = this.stadiumRepository.get(createRequest.getStadium());
        if(null == stadium)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Stadium"));
        }
        match.setStadium(stadium);

        Map<Long, Player> playerIdPlayerMap = new HashMap<>();
        Map<Long, Team> playerIdTeamMap = new HashMap<>();

        List<MatchPlayerMap> matchPlayerMaps = new ArrayList<>();
        for(Map<String, String> matchPlayerMapRaw: createRequest.getPlayers())
        {
            MatchPlayerMap matchPlayerMap = new MatchPlayerMap();

            matchPlayerMap.setMatch(match);

            Team team = this.teamRepository.get(Long.parseLong(matchPlayerMapRaw.get("teamId")));
            if(null == team)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Player's Team"));
            }
            matchPlayerMap.setTeam(team);

            Player player = this.playerRepository.get(Long.parseLong(matchPlayerMapRaw.get("playerId")));
            if(null == player)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Player"));
            }
            matchPlayerMap.setPlayer(player);

            playerIdPlayerMap.put(player.getId(), player);
            playerIdTeamMap.put(player.getId(), team);

            matchPlayerMaps.add(matchPlayerMap);
        }

        match.setPlayers(matchPlayerMaps);

        List<Extras> extras = new ArrayList<>();
        for(Map<String, String> extraRaw: createRequest.getExtras())
        {
            try
            {
                Extras extra = new Extras();
                extra.setRuns(Integer.parseInt(extraRaw.get("runs")));
                extra.setType(ExtrasType.valueOf(extraRaw.get("type")));
                extra.setMatch(match);
                Team battingTeam = this.teamRepository.get(Long.parseLong(extraRaw.get("battingTeam")));
                if(null == battingTeam)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Batting Team"));
                }
                extra.setBattingTeam(battingTeam);
                Team bowlingTeam = this.teamRepository.get(Long.parseLong(extraRaw.get("bowlingTeam")));
                if(null == bowlingTeam)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowling Team"));
                }
                extra.setBowlingTeam(bowlingTeam);
                extra.setInnings(Integer.parseInt(extraRaw.get("innings")));
                extra.setTeamInnings(Integer.parseInt(extraRaw.get("teamInnings")));
                extras.add(extra);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

        }
        match.setExtras(extras);

        List<BattingScore> battingScores = new ArrayList<>();
        for(Map<String, String> battingScoreRaw: createRequest.getBattingScores())
        {
            BattingScore battingScore = new BattingScore();

            battingScore.setMatch(match);
            battingScore.setPlayer(playerIdPlayerMap.get(Long.parseLong(battingScoreRaw.get("playerId"))));
            battingScore.setTeam(playerIdTeamMap.get(Long.parseLong(battingScoreRaw.get("playerId"))));
            battingScore.setRuns(Integer.parseInt(battingScoreRaw.get("runs")));
            battingScore.setBalls(Integer.parseInt(battingScoreRaw.get("balls")));
            battingScore.setFours(Integer.parseInt(battingScoreRaw.get("fours")));
            battingScore.setSixes(Integer.parseInt(battingScoreRaw.get("sixes")));

            if(null != battingScoreRaw.get("dismissalMode"))
            {
                DismissalMode dismissalMode = this.dismissalRepository.get(Long.parseLong(battingScoreRaw.get("dismissalMode")));
                if(null == dismissalMode)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Dismissal Mode"));
                }

                battingScore.setDismissalMode(dismissalMode);

                if(null != battingScoreRaw.get("bowlerId"))
                {
                    Long bowlerId = Long.parseLong(battingScoreRaw.get("bowlerId"));
                    BowlerDismissal bowlerDismissal = new BowlerDismissal();
                    bowlerDismissal.setPlayer(playerIdPlayerMap.get(bowlerId));
                    bowlerDismissal.setTeam(playerIdTeamMap.get(bowlerId));

                    battingScore.setBowler(bowlerDismissal);
                }

                if(null != battingScoreRaw.get("fielders"))
                {
                    String[] fielderIds = battingScoreRaw.get("fielders").split(", ");
                    List<FielderDismissal> fielders = new ArrayList<>();
                    for(String fielderIdString: fielderIds)
                    {
                        Long fielderId = Long.parseLong(fielderIdString);
                        FielderDismissal fielderDismissal = new FielderDismissal();
                        fielderDismissal.setScore(battingScore);
                        fielderDismissal.setPlayer(playerIdPlayerMap.get(fielderId));
                        fielderDismissal.setTeam(playerIdTeamMap.get(fielderId));

                        fielders.add(fielderDismissal);
                    }

                    battingScore.setFielders(fielders);
                }
            }

            battingScore.setInnings(Integer.parseInt(battingScoreRaw.get("innings")));
            battingScore.setTeamInnings(Integer.parseInt(battingScoreRaw.get("teamInnings")));

            battingScores.add(battingScore);
        }
        match.setBattingScores(battingScores);

        List<BowlingFigure> bowlingFigures = new ArrayList<>();
        for(Map<String, String> bowlingFigureRaw: createRequest.getBowlingFigures())
        {
            BowlingFigure bowlingFigure = new BowlingFigure();
            bowlingFigure.setMatch(match);

            Player player = playerIdPlayerMap.get(Long.parseLong(bowlingFigureRaw.get("playerId")));
            if(null == player)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowler"));
            }
            bowlingFigure.setPlayer(player);

            Team team = playerIdTeamMap.get(Long.parseLong(bowlingFigureRaw.get("playerId")));
            if(null == team)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowler's Team"));
            }
            bowlingFigure.setTeam(team);

            bowlingFigure.setBalls(Integer.parseInt(bowlingFigureRaw.get("balls")));
            bowlingFigure.setMaidens(Integer.parseInt(bowlingFigureRaw.get("maidens")));
            bowlingFigure.setRuns(Integer.parseInt(bowlingFigureRaw.get("runs")));
            bowlingFigure.setWickets(Integer.parseInt(bowlingFigureRaw.get("wickets")));
            bowlingFigure.setInnings(Integer.parseInt(bowlingFigureRaw.get("innings")));
            bowlingFigure.setTeamInnings(Integer.parseInt(bowlingFigureRaw.get("teamInnings")));

            bowlingFigures.add(bowlingFigure);
        }

        match.setBowlingFigures(bowlingFigures);


        List<ManOfTheMatch> manOfTheMatchList = new ArrayList<>();
        for(Long playerId: createRequest.getManOfTheMatchList())
        {
            ManOfTheMatch manOfTheMatch = new ManOfTheMatch();
            manOfTheMatch.setMatch(match);
            manOfTheMatch.setPlayer(playerIdPlayerMap.get(playerId));
            manOfTheMatch.setTeam(playerIdTeamMap.get(playerId));

            manOfTheMatchList.add(manOfTheMatch);
        }
        match.setManOfTheMatchList(manOfTheMatchList);

        List<ManOfTheSeries> manOfTheSeriesList = new ArrayList<>();
        for(Map<String, Long> manOfTheSeriesRaw: createRequest.getManOfTheSeriesList())
        {
            Long playerId = manOfTheSeriesRaw.get("playerId");
            Long teamId = manOfTheSeriesRaw.get("teamId");
            ManOfTheSeries manOfTheSeries = new ManOfTheSeries();

            Player player;
            if(playerIdPlayerMap.containsKey(playerId))
            {
                player = playerIdPlayerMap.get(playerId);
            }
            else
            {
                player = this.playerRepository.get(playerId);
                if(null == player)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Player"));
                }
            }
            manOfTheSeries.setPlayer(player);

            Team team;
            if(playerIdTeamMap.containsKey(playerId))
            {
                team = playerIdTeamMap.get(playerId);
            }
            else
            {
                team = this.teamRepository.get(teamId);
                if(null == team)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team"));
                }
            }
            manOfTheSeries.setTeam(team);
            manOfTheSeries.setSeries(series);

            manOfTheSeriesList.add(manOfTheSeries);
        }
        series.getManOfTheSeriesList().clear();
        series.getManOfTheSeriesList().addAll(manOfTheSeriesList);

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            Match createdMatch = this.matchRepository.save(match);
            transaction.commit();
            transaction.end();
            return createdMatch;
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }
    }
}
