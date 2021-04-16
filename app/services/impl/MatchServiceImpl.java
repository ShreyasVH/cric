package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import enums.ExtrasType;
import exceptions.BadRequestException;
import exceptions.DBInteractionException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.*;
import org.springframework.util.StringUtils;
import repositories.*;
import requests.matches.CreateRequest;
import requests.matches.UpdateRequest;
import responses.BattingScoreResponse;
import responses.MatchResponse;
import services.MatchService;
import services.TeamService;

import java.util.*;
import java.util.stream.Collectors;

public class MatchServiceImpl implements MatchService
{
    private final DismissalRepository dismissalRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final SeriesRepository seriesRepository;
    private final StadiumRepository stadiumRepository;
    private final TeamRepository teamRepository;

    private final TeamService teamService;

    @Inject
    public MatchServiceImpl
    (
        DismissalRepository dismissalRepository,
        MatchRepository matchRepository,
        PlayerRepository playerRepository,
        SeriesRepository seriesRepository,
        StadiumRepository stadiumRepository,
        TeamRepository teamRepository,

        TeamService teamService
    )
    {
        this.dismissalRepository = dismissalRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.seriesRepository = seriesRepository;
        this.stadiumRepository = stadiumRepository;
        this.teamRepository = teamRepository;

        this.teamService = teamService;
    }

    public MatchResponse matchResponse(Match match)
    {
        MatchResponse matchResponse = new MatchResponse(match);
        List<BattingScoreResponse> battingScoreResponses = this.matchRepository.getBattingScores(match.getId()).stream().map(BattingScoreResponse::new).collect(Collectors.toList());
        List<Long> bowlerDismissalIds = battingScoreResponses.stream().filter(battingScoreResponse -> battingScoreResponse.getBowlerDismissalId() != null).map(BattingScoreResponse::getBowlerDismissalId).collect(Collectors.toList());
        List<BowlerDismissal> bowlerDismissals = this.matchRepository.getBowlingDismissals(bowlerDismissalIds);
        Map<Long, BowlerDismissal> bowlerDismissalMap = bowlerDismissals.stream().collect(Collectors.toMap(BowlerDismissal::getId, bowlerDismissal -> bowlerDismissal));
        battingScoreResponses = battingScoreResponses.stream().peek(battingScoreResponse -> {
            if((battingScoreResponse.getBowlerDismissalId() != null) && bowlerDismissalMap.containsKey(battingScoreResponse.getBowlerDismissalId()))
            {
                battingScoreResponse.setBowler(bowlerDismissalMap.get(battingScoreResponse.getBowlerDismissalId()));
            }
        }).collect(Collectors.toList());

        List<Long> scoreIds = battingScoreResponses.stream().map(BattingScoreResponse::getId).collect(Collectors.toList());
        List<FielderDismissal> fielderDismissals = this.matchRepository.getFielderDismissals(scoreIds);
        Map<Long, List<FielderDismissal>> fielderDismissalMap = new HashMap<>();
        fielderDismissals.forEach(fielderDismissal -> {
            List<FielderDismissal> dismissals = new ArrayList<>();
            if(fielderDismissalMap.containsKey(fielderDismissal.getScoreId()))
            {
                dismissals = fielderDismissalMap.get(fielderDismissal.getScoreId());
            }
            dismissals.add(fielderDismissal);
            fielderDismissalMap.put(fielderDismissal.getScoreId(), dismissals);
        });
        battingScoreResponses = battingScoreResponses.stream().peek(battingScoreResponse -> {
            if(fielderDismissalMap.containsKey(battingScoreResponse.getId()))
            {
                battingScoreResponse.setFielders(fielderDismissalMap.get(battingScoreResponse.getId()));
            }
        }).collect(Collectors.toList());

        matchResponse.setBattingScores(battingScoreResponses);

        matchResponse.setBowlingFigures(this.matchRepository.getBowlingFigures(matchResponse.getId()));
        matchResponse.setExtras(this.matchRepository.getExtras(matchResponse.getId()));
        matchResponse.setPlayers(this.matchRepository.getPlayers(matchResponse.getId()));
        matchResponse.setManOfTheMatchList(this.matchRepository.getManOfTheMatchList(match.getId()));
        matchResponse.setCaptains(this.matchRepository.getCaptainsForMatch(match.getId()));
        matchResponse.setWicketKeepers(this.matchRepository.getWicketKeepersForMatch(match.getId()));

        return matchResponse;
    }

    public MatchResponse get(Long id)
    {
        Match match = this.matchRepository.get(id);
        if(null == match)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Match"));
        }

        return matchResponse(match);
    }

    public Match create(CreateRequest createRequest)
    {
        createRequest.validate();

        Match existingMatch = this.matchRepository.get(createRequest.getStadium(), createRequest.getStartTime());
        if(null != existingMatch)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
        }

        Match match = new Match();

        match.setOfficial(createRequest.isOfficial());
        match.setStartTime(createRequest.getStartTime());

        match.setTag(createRequest.getTag());

        Series series = this.seriesRepository.get(createRequest.getSeriesId());
        if(null == series)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Series"));
        }
        match.setSeries(series.getId());

        Team team1 = this.teamRepository.get(createRequest.getTeam1());
        if(null == team1)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team 1"));
        }
        match.setTeam1(team1.getId());

        Team team2 = this.teamRepository.get(createRequest.getTeam2());
        if(null == team2)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team 2"));
        }
        match.setTeam2(team2.getId());

        Stadium stadium = this.stadiumRepository.get(createRequest.getStadium());
        if(null == stadium)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Stadium"));
        }
        match.setStadium(stadium.getId());

        match.setResult(createRequest.getResult());

        if(null != createRequest.getTossWinner())
        {
            Team tossWinner = this.teamRepository.get(createRequest.getTossWinner());
            if(null == tossWinner)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Toss Winner Team"));
            }
            match.setTossWinner(tossWinner.getId());

            Team battingFirst = this.teamRepository.get(createRequest.getBatFirst());
            if(null == battingFirst)
            {
                throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Batting First Team"));
            }
            match.setBatFirst(battingFirst.getId());

            if(null != createRequest.getWinner())
            {
                Team winner = this.teamRepository.get(createRequest.getWinner());
                if(null == winner)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Winner Team"));
                }
                match.setWinner(winner.getId());

                match.setWinMargin(createRequest.getWinMargin());
                match.setWinMarginType(createRequest.getWinMarginType());
            }

        }

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            Match createdMatch = this.matchRepository.save(match);

            Map<Long, Player> playerIdPlayerMap = new HashMap<>();
            Map<Long, Team> playerIdTeamMap = new HashMap<>();

            if(null != createRequest.getPlayers())
            {
                List<MatchPlayerMap> matchPlayerMaps = new ArrayList<>();
                for (Map<String, String> matchPlayerMapRaw : createRequest.getPlayers())
                {
                    MatchPlayerMap matchPlayerMap = new MatchPlayerMap();

                    matchPlayerMap.setMatchId(createdMatch.getId());

                    Team team = this.teamRepository.get(Long.parseLong(matchPlayerMapRaw.get("teamId")));
                    if (null == team)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Player's Team"));
                    }
                    matchPlayerMap.setTeamId(team.getId());

                    Player player = this.playerRepository.get(Long.parseLong(matchPlayerMapRaw.get("playerId")));
                    if (null == player)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Player"));
                    }
                    matchPlayerMap.setPlayerId(player.getId());

                    playerIdPlayerMap.put(player.getId(), player);
                    playerIdTeamMap.put(player.getId(), team);

                    matchPlayerMaps.add(matchPlayerMap);
                }
                this.matchRepository.addPlayersForMatch(matchPlayerMaps);

                if (null != createRequest.getBench())
                {
                    for (Map<String, String> matchPlayerMapRaw : createRequest.getBench())
                    {
                        Player player = this.playerRepository.get(Long.parseLong(matchPlayerMapRaw.get("playerId")));
                        if (null == player)
                        {
                            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Player"));
                        }

                        playerIdPlayerMap.put(player.getId(), player);
                    }
                }
            }

            List<Extras> extras = new ArrayList<>();
            for(Map<String, String> extraRaw: createRequest.getExtras())
            {
                try
                {
                    Extras extra = new Extras();
                    extra.setRuns(Integer.parseInt(extraRaw.get("runs")));
                    extra.setType(ExtrasType.valueOf(extraRaw.get("type")));
                    extra.setMatchId(createdMatch.getId());
                    Team battingTeam = this.teamRepository.get(Long.parseLong(extraRaw.get("battingTeam")));
                    if(null == battingTeam)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Batting Team"));
                    }
                    extra.setBattingTeam(battingTeam.getId());
                    Team bowlingTeam = this.teamRepository.get(Long.parseLong(extraRaw.get("bowlingTeam")));
                    if(null == bowlingTeam)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowling Team"));
                    }
                    extra.setBowlingTeam(bowlingTeam.getId());
                    extra.setInnings(Integer.parseInt(extraRaw.get("innings")));
                    extra.setTeamInnings(Integer.parseInt(extraRaw.get("teamInnings")));
                    extras.add(extra);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
                }
            }
            this.matchRepository.addExtrasForMatch(extras);

            List<BowlingFigure> bowlingFigures = new ArrayList<>();
            for(Map<String, String> bowlingFigureRaw: createRequest.getBowlingFigures())
            {
                BowlingFigure bowlingFigure = new BowlingFigure();
                bowlingFigure.setMatchId(createdMatch.getId());

                Player player = playerIdPlayerMap.get(Long.parseLong(bowlingFigureRaw.get("playerId")));
                if(null == player)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowler"));
                }
                bowlingFigure.setPlayerId(player.getId());

                Team team = playerIdTeamMap.get(Long.parseLong(bowlingFigureRaw.get("playerId")));
                if(null == team)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowler's Team"));
                }
                bowlingFigure.setTeamId(team.getId());

                bowlingFigure.setBalls(Integer.parseInt(bowlingFigureRaw.get("balls")));
                bowlingFigure.setMaidens(Integer.parseInt(bowlingFigureRaw.get("maidens")));
                bowlingFigure.setRuns(Integer.parseInt(bowlingFigureRaw.get("runs")));
                bowlingFigure.setWickets(Integer.parseInt(bowlingFigureRaw.get("wickets")));
                bowlingFigure.setInnings(Integer.parseInt(bowlingFigureRaw.get("innings")));
                bowlingFigure.setTeamInnings(Integer.parseInt(bowlingFigureRaw.get("teamInnings")));

                bowlingFigures.add(bowlingFigure);
            }
            this.matchRepository.addBowlingFigures(bowlingFigures);

            List<ManOfTheMatch> manOfTheMatchList = new ArrayList<>();
            List<Long> motmPlayerIds = new ArrayList<>();
            for(Long playerId: createRequest.getManOfTheMatchList())
            {
                if(!motmPlayerIds.contains(playerId))
                {
                    ManOfTheMatch manOfTheMatch = new ManOfTheMatch();
                    manOfTheMatch.setMatchId(createdMatch.getId());
                    manOfTheMatch.setPlayerId(playerId);
                    manOfTheMatch.setTeamId(playerIdTeamMap.get(playerId).getId());

                    manOfTheMatchList.add(manOfTheMatch);
                    motmPlayerIds.add(playerId);
                }
            }
            this.matchRepository.addManOfTheMatchList(manOfTheMatchList);

            for(Map<String, String> battingScoreRaw: createRequest.getBattingScores())
            {
                boolean isBowlerPresent = false;
                BowlerDismissal bowlerDismissal = new BowlerDismissal();
                if(null != battingScoreRaw.get("bowlerId") && !StringUtils.isEmpty(battingScoreRaw.get("bowlerId")))
                {
                    Long bowlerId = Long.parseLong(battingScoreRaw.get("bowlerId"));
                    bowlerDismissal.setPlayerId(bowlerId);
                    bowlerDismissal.setTeamId(playerIdTeamMap.get(bowlerId).getId());
                    this.matchRepository.addBowlerDismissal(bowlerDismissal);
                    isBowlerPresent = true;
                }


                BattingScore battingScore = new BattingScore();

                battingScore.setMatchId(createdMatch.getId());
                if(isBowlerPresent)
                {
                    battingScore.setBowlerDismissalId(bowlerDismissal.getId());
                }
                battingScore.setPlayerId(Long.parseLong(battingScoreRaw.get("playerId")));
                Long battingTeamId = playerIdTeamMap.get(Long.parseLong(battingScoreRaw.get("playerId"))).getId();
                Long bowlingTeamId = ((battingTeamId.equals(createRequest.getTeam1())) ? createRequest.getTeam2() : createRequest.getTeam1());
                battingScore.setTeamId(battingTeamId);
                battingScore.setRuns(Integer.parseInt(battingScoreRaw.get("runs")));
                battingScore.setBalls(Integer.parseInt(battingScoreRaw.get("balls")));
                battingScore.setFours(Integer.parseInt(battingScoreRaw.get("fours")));
                battingScore.setSixes(Integer.parseInt(battingScoreRaw.get("sixes")));

                if(null != battingScoreRaw.get("dismissalMode") && !StringUtils.isEmpty(battingScoreRaw.get("dismissalMode")))
                {
                    DismissalMode dismissalMode = this.dismissalRepository.get(Long.parseLong(battingScoreRaw.get("dismissalMode")));
                    if(null == dismissalMode)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Dismissal Mode"));
                    }

                    battingScore.setDismissalMode(dismissalMode.getId().intValue());
                }
                battingScore.setInnings(Integer.parseInt(battingScoreRaw.get("innings")));
                battingScore.setTeamInnings(Integer.parseInt(battingScoreRaw.get("teamInnings")));

                this.matchRepository.addBattingScore(battingScore);

                if(null != battingScoreRaw.get("fielders") && !StringUtils.isEmpty(battingScoreRaw.get("fielders")))
                {
                    String[] fielderIds = battingScoreRaw.get("fielders").split(", ");
                    List<FielderDismissal> fielders = new ArrayList<>();
                    for(String fielderIdString: fielderIds)
                    {
                        Long fielderId = Long.parseLong(fielderIdString);
                        FielderDismissal fielderDismissal = new FielderDismissal();
                        fielderDismissal.setScoreId(battingScore.getId());
                        fielderDismissal.setPlayerId(fielderId);
                        fielderDismissal.setTeamId(bowlingTeamId);

                        fielders.add(fielderDismissal);
                    }

                    this.matchRepository.addFielderDismissals(fielders);
                }
            }

            List<Captain> captains = new ArrayList<>();
            List<Long> processedCaptains = new ArrayList<>();
            for(Long playerId: createRequest.getCaptains())
            {
                Team team = playerIdTeamMap.get(playerId);
                if(!processedCaptains.contains(playerId) && (null != team))
                {
                    Captain captain = new Captain();
                    captain.setMatchId(createdMatch.getId());
                    captain.setPlayerId(playerId);
                    captain.setTeamId(team.getId());

                    captains.add(captain);

                    processedCaptains.add(playerId);
                }
            }
            this.matchRepository.addCaptainsForMatch(captains);

            List<WicketKeeper> wicketKeepers = new ArrayList<>();
            List<Long> processedWicketKeepers = new ArrayList<>();
            for(Long playerId: createRequest.getWicketKeepers())
            {
                Team team = playerIdTeamMap.get(playerId);
                if(!processedWicketKeepers.contains(playerId) && (null != team))
                {
                    WicketKeeper wicketKeeper = new WicketKeeper();
                    wicketKeeper.setMatchId(createdMatch.getId());
                    wicketKeeper.setPlayerId(playerId);
                    wicketKeeper.setTeamId(team.getId());

                    wicketKeepers.add(wicketKeeper);

                    processedWicketKeepers.add(playerId);
                }
            }
            this.matchRepository.addWicketKeepersForMatch(wicketKeepers);

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

    @Override
    public Match update(Long id, UpdateRequest updateRequest)
    {
        updateRequest.validate();

        Match existingMatch = this.matchRepository.get(id);
        if(null == existingMatch)
        {
            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Match"));
        }

        boolean isUpdateRequired = false;

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            if((null != updateRequest.getStartTime()) && (!existingMatch.getStartTime().equals(updateRequest.getStartTime())))
            {
                isUpdateRequired = true;
                existingMatch.setStartTime(updateRequest.getStartTime());
            }

            if(null != updateRequest.getTag() && (!existingMatch.getTag().equals(updateRequest.getTag())))
            {
                isUpdateRequired = true;
                existingMatch.setTag(updateRequest.getTag());
            }

            List<Long> teamIds = new ArrayList<>();
            if((null != updateRequest.getTeam1()) && (!updateRequest.getTeam1().equals(existingMatch.getTeam1())))
            {
                isUpdateRequired = true;

                Team team1 = this.teamService.getRaw(updateRequest.getTeam1());
                if(null == team1)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team 1"));
                }

                existingMatch.setTeam1(team1.getId());
            }
            teamIds.add(existingMatch.getTeam1());


            if((null != updateRequest.getTeam2()) && (!updateRequest.getTeam2().equals(existingMatch.getTeam2())))
            {
                isUpdateRequired = true;

                Team team2 = this.teamService.getRaw(updateRequest.getTeam2());
                if(null == team2)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Team 2"));
                }

                existingMatch.setTeam2(team2.getId());
            }
            teamIds.add(existingMatch.getTeam2());

            if(existingMatch.getTeam1().equals(existingMatch.getTeam2()))
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Teams need to be different");
            }

            if((null != updateRequest.getStadium()) && (!updateRequest.getStadium().equals(existingMatch.getStadium())))
            {
                Stadium stadium = this.stadiumRepository.get(updateRequest.getStadium());
                if(null == stadium)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Stadium"));
                }

                isUpdateRequired = true;
                existingMatch.setStadium(stadium.getId());
            }

            if((null != updateRequest.getTossWinner()) && (!updateRequest.getTossWinner().equals(existingMatch.getTossWinner())))
            {
                isUpdateRequired = true;

                Team tossWinner = this.teamService.getRaw(updateRequest.getTossWinner());
                if(null == tossWinner)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Toss Winner"));
                }

                existingMatch.setTossWinner(tossWinner.getId());
            }
            if((existingMatch.getTossWinner() != null) && !teamIds.contains(existingMatch.getTossWinner()))
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid toss winner");
            }

            if((null != updateRequest.getBatFirst()) && (!updateRequest.getBatFirst().equals(existingMatch.getBatFirst())))
            {
                isUpdateRequired = true;

                Team battingFirst = this.teamService.getRaw(updateRequest.getBatFirst());
                if(null == battingFirst)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Batting Team"));
                }

                existingMatch.setBatFirst(battingFirst.getId());
            }
            if((existingMatch.getBatFirst() != null) && !teamIds.contains(existingMatch.getBatFirst()))
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid bat first team");
            }

            if((null != updateRequest.getResult()) && (!updateRequest.getResult().equals(existingMatch.getResult())))
            {
                isUpdateRequired = true;
                existingMatch.setResult(updateRequest.getResult());
            }

            if((null != updateRequest.getWinner()) && (!updateRequest.getWinner().equals(existingMatch.getWinner())))
            {
                isUpdateRequired = true;

                Team winner = this.teamService.getRaw(updateRequest.getWinner());
                if(null == winner)
                {
                    throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Winner"));
                }

                existingMatch.setWinner(winner.getId());
            }
            if((existingMatch.getWinner() != null) && !teamIds.contains(existingMatch.getWinner()))
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid winner");
            }

            if((null != updateRequest.getWinMargin()) && (!updateRequest.getWinMargin().equals(existingMatch.getWinMargin())))
            {
                isUpdateRequired = true;
                existingMatch.setWinMargin(updateRequest.getWinMargin());
            }

            if((null != updateRequest.getWinMarginType()) && (!updateRequest.getWinMarginType().equals(existingMatch.getWinMarginType())))
            {
                isUpdateRequired = true;
                existingMatch.setWinMarginType(updateRequest.getWinMarginType());
            }

            if((null != updateRequest.getExtras()) && (!updateRequest.getExtras().isEmpty()))
            {
                List<Extras> existingExtras = this.matchRepository.getExtras(id);
                Map<String, Extras> existingExtrasMap = existingExtras.stream().collect(Collectors.toMap(extras -> (extras.getType().name() + "_" + extras.getInnings()), extras -> extras));

                List<Extras> extrasToAdd = new ArrayList<>();
                List<String> processedExtras = new ArrayList<>();
                for(Map<String, String> extraRaw: updateRequest.getExtras())
                {
                    ExtrasType extrasType = ExtrasType.valueOf(extraRaw.get("type"));
                    int innings = Integer.parseInt(extraRaw.get("innings"));
                    String key = extrasType.name() + "_" + innings;
                    int teamInnings = Integer.parseInt(extraRaw.get("teamInnings"));
                    Long battingTeamId = Long.parseLong(extraRaw.get("battingTeam"));
                    Long bowlingTeamId = Long.parseLong(extraRaw.get("bowlingTeam"));

                    int runs = Integer.parseInt(extraRaw.get("runs"));

                    if(processedExtras.contains(key))
                    {
                        continue;
                    }
                    processedExtras.add(key);

                    if(
                            existingExtrasMap.containsKey(key)
                                    &&
                                    (existingExtrasMap.get(key).getRuns() == runs)
                                    &&
                                    (existingExtrasMap.get(key).getBattingTeam().equals(battingTeamId))
                                    &&
                                    (existingExtrasMap.get(key).getBowlingTeam().equals(bowlingTeamId))
                                    &&
                                    (existingExtrasMap.get(key).getTeamInnings() == teamInnings)
                    )
                    {
                        continue;
                    }


                    if(!teamIds.contains(battingTeamId))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid team for batting team");
                    }

                    if(!teamIds.contains(bowlingTeamId))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid team for bowling team");
                    }

                    if(battingTeamId.equals(bowlingTeamId))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Same team for batting and bowling team");
                    }

                    Team battingTeam = this.teamService.getRaw(battingTeamId);
                    if(null == battingTeam)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Batting Team"));
                    }

                    Team bowlingTeam = this.teamService.getRaw(bowlingTeamId);
                    if(null == bowlingTeam)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowling Team"));
                    }


                    Extras extra = new Extras();
                    if(existingExtrasMap.containsKey(key))
                    {
                        extra = existingExtrasMap.get(key);
                    }

                    extra.setRuns(runs);
                    extra.setType(extrasType);
                    extra.setMatchId(id);
                    extra.setBattingTeam(battingTeamId);
                    extra.setBowlingTeam(bowlingTeamId);
                    extra.setInnings(innings);
                    extra.setTeamInnings(teamInnings);

                    extrasToAdd.add(extra);
                }

                this.matchRepository.addExtrasForMatch(extrasToAdd);
                if(extrasToAdd.size() > 0)
                {
                    isUpdateRequired = true;
                }

                List<Extras> extrasToRemove = existingExtras.stream().filter(extras -> {
                    String key = extras.getType().name() + "_" + extras.getInnings();
                    return !processedExtras.contains(key);
                }).collect(Collectors.toList());
                this.matchRepository.removeExtrasForMatch(extrasToRemove);
                if(extrasToRemove.size() > 0)
                {
                    isUpdateRequired = true;
                }
            }

            List<MatchPlayerMap> existingPlayers = this.matchRepository.getPlayers(id);
            Map<Long, MatchPlayerMap> existingPlayerMap = existingPlayers.stream().collect(Collectors.toMap(MatchPlayerMap::getPlayerId, matchPlayerMap -> matchPlayerMap));

            if((null != updateRequest.getPlayers()))
            {
                List<MatchPlayerMap> playersToAdd = new ArrayList<>();
                List<Long> processedPlayers = new ArrayList<>();
                for(Map<String, String> matchPlayerMapRaw: updateRequest.getPlayers())
                {
                    Long playerId = Long.parseLong(matchPlayerMapRaw.get("playerId"));

                    if(processedPlayers.contains(playerId))
                    {
                        continue;
                    }
                    processedPlayers.add(playerId);

                    Long teamId = Long.parseLong(matchPlayerMapRaw.get("teamId"));
                    if(
                            existingPlayerMap.containsKey(playerId)
                                    &&
                                    (teamId.equals(existingPlayerMap.get(playerId).getTeamId()))
                    )
                    {
                        continue;
                    }

                    if(!teamIds.contains(teamId))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid team for player");
                    }

                    Team team = this.teamService.getRaw(teamId);
                    if(null == team)
                    {
                        throw new NotFoundException("Player's Team");
                    }


                    if(existingPlayerMap.containsKey(playerId))
                    {
                        MatchPlayerMap existingPlayer = existingPlayerMap.get(playerId);
                        existingPlayer.setTeamId(teamId);

                        playersToAdd.add(existingPlayer);
                    }
                    else
                    {
                        MatchPlayerMap matchPlayerMap = new MatchPlayerMap();

                        matchPlayerMap.setMatchId(id);

                        matchPlayerMap.setTeamId(team.getId());

                        Player player = this.playerRepository.get(playerId);
                        if(null == player)
                        {
                            throw new NotFoundException("Player");
                        }
                        matchPlayerMap.setPlayerId(player.getId());

                        playersToAdd.add(matchPlayerMap);
                        existingPlayerMap.put(playerId, matchPlayerMap);
                    }
                }

                this.matchRepository.addPlayersForMatch(playersToAdd);
                if(playersToAdd.size() > 0)
                {
                    isUpdateRequired = true;
                }

                List<MatchPlayerMap> playersToDelete = existingPlayers.stream().filter(matchPlayerMap -> (!processedPlayers.contains(matchPlayerMap.getPlayerId()))).collect(Collectors.toList());
                this.matchRepository.removePlayers(playersToDelete);
                if(playersToDelete.size() > 0)
                {
                    isUpdateRequired = true;
                }
            }

            if(null != updateRequest.getBattingScores())
            {
                List<BattingScore> existingBattingScores = this.matchRepository.getBattingScores(id);
                Map<String, BattingScore> existingScoresMap = existingBattingScores.stream().collect(Collectors.toMap(battingScore -> (battingScore.getPlayerId() + "_" + battingScore.getInnings()), battingScore -> battingScore));
                List<String> processedScores = new ArrayList<>();
                for(Map<String, String> battingScoreRaw: updateRequest.getBattingScores())
                {
                    BattingScore battingScore = new BattingScore();

                    Long playerId = Long.parseLong(battingScoreRaw.get("playerId"));

                    Long teamId = existingPlayerMap.get(playerId).getTeamId();
                    int runs = Integer.parseInt(battingScoreRaw.get("runs"));
                    int balls = Integer.parseInt(battingScoreRaw.get("balls"));
                    int fours = Integer.parseInt(battingScoreRaw.get("fours"));
                    int sixes = Integer.parseInt(battingScoreRaw.get("sixes"));
                    int innings = Integer.parseInt(battingScoreRaw.get("innings"));
                    int teamInnings = Integer.parseInt(battingScoreRaw.get("teamInnings"));
                    String key = playerId + "_" + innings;

                    if(processedScores.contains(key))
                    {
                        continue;
                    }
                    processedScores.add(key);

                    boolean isAddRequired = !(
                            existingScoresMap.containsKey(key)
                                    &&
                                    teamId.equals(existingScoresMap.get(key).getTeamId())
                                    &&
                                    (existingScoresMap.get(key).getRuns() == runs)
                                    &&
                                    (existingScoresMap.get(key).getBalls() == balls)
                                    &&
                                    (existingScoresMap.get(key).getFours() == fours)
                                    &&
                                    (existingScoresMap.get(key).getSixes() == sixes)
                                    &&
                                    (existingScoresMap.get(key).getInnings() == innings)
                                    &&
                                    (existingScoresMap.get(key).getTeamInnings() == teamInnings)
                    );

                    if(existingScoresMap.containsKey(key))
                    {
                        battingScore = existingScoresMap.get(key);
                    }

                    battingScore.setMatchId(id);
                    battingScore.setPlayerId(playerId);
                    battingScore.setTeamId(teamId);
                    battingScore.setRuns(runs);
                    battingScore.setBalls(balls);
                    battingScore.setFours(fours);
                    battingScore.setSixes(sixes);

                    if(null != battingScoreRaw.get("dismissalMode"))
                    {
                        DismissalMode dismissalMode = this.dismissalRepository.get(Long.parseLong(battingScoreRaw.get("dismissalMode")));
                        if(null == dismissalMode)
                        {
                            throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Dismissal Mode"));
                        }

                        isAddRequired = (isAddRequired ||
                                (
                                        null == battingScore.getDismissalMode()
                                                ||
                                                dismissalMode.getId().intValue() != battingScore.getDismissalMode())
                        );
                        battingScore.setDismissalMode(dismissalMode.getId().intValue());

                        if(null != battingScoreRaw.get("bowlerId"))
                        {
                            Long bowlerId = Long.parseLong(battingScoreRaw.get("bowlerId"));
                            if(!existingPlayerMap.containsKey(bowlerId))
                            {
                                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid bowler");
                            }
                            Long bowlerTeamId = existingPlayerMap.get(bowlerId).getTeamId();

                            BowlerDismissal bowlerDismissal = new BowlerDismissal();

                            if(null != battingScore.getBowlerDismissalId())
                            {
                                bowlerDismissal = this.matchRepository.getBowlingDismissal(battingScore.getBowlerDismissalId());
                            }

                            boolean isBowlerAddRequired = (
                                    (bowlerDismissal.getId() == null)
                                            ||
                                            (
                                                    !bowlerId.equals(bowlerDismissal.getPlayerId())
                                                            ||
                                                            !bowlerTeamId.equals(bowlerDismissal.getTeamId())
                                            )
                            );

                            if(isBowlerAddRequired)
                            {
                                bowlerDismissal.setPlayerId(bowlerId);
                                bowlerDismissal.setTeamId(bowlerTeamId);


                                this.matchRepository.addBowlerDismissal(bowlerDismissal);

                                battingScore.setBowlerDismissalId(bowlerDismissal.getId());
                                isAddRequired = true;
                            }
                        }
                        else
                        {
                            if(null != battingScore.getBowlerDismissalId())
                            {
                                this.matchRepository.removeBowlerDismissal(this.matchRepository.getBowlingDismissal(battingScore.getBowlerDismissalId()));
                                battingScore.setBowlerDismissalId(null);
                                isAddRequired = true;
                            }
                        }
                    }

                    battingScore.setInnings(innings);
                    battingScore.setTeamInnings(teamInnings);

                    if(isAddRequired)
                    {
                        this.matchRepository.addBattingScore(battingScore);
                        isUpdateRequired = true;
                    }

                    if((null != battingScoreRaw.get("fielders")) && !StringUtils.isEmpty(battingScoreRaw.get("fielders")))
                    {
                        List<FielderDismissal> existingFielders = this.matchRepository.getFielderDismissals(Collections.singletonList(battingScore.getId()));
                        Map<Long, FielderDismissal> existingFielderMap = existingFielders.stream().collect(Collectors.toMap(FielderDismissal::getPlayerId, fielderDismissal -> fielderDismissal));

                        String[] fielderIds = battingScoreRaw.get("fielders").split(", ");
                        List<FielderDismissal> fieldersToAdd = new ArrayList<>();
                        List<Long> processedFielders = new ArrayList<>();
                        for(String fielderIdString: fielderIds)
                        {
                            Long fielderId = Long.parseLong(fielderIdString);
                            if(processedFielders.contains(fielderId))
                            {
                                continue;
                            }
                            processedFielders.add(fielderId);

                            if(!existingPlayerMap.containsKey(fielderId))
                            {
                                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid player for fielder");
                            }

                            Long fielderTeamId = existingPlayerMap.get(fielderId).getTeamId();

                            if(existingFielderMap.containsKey(fielderId) && existingFielderMap.get(fielderId).getTeamId().equals(fielderTeamId))
                            {
                                continue;
                            }

                            FielderDismissal fielderDismissal = new FielderDismissal();
                            if(existingFielderMap.containsKey(fielderId))
                            {
                                fielderDismissal = existingFielderMap.get(fielderId);
                            }


                            fielderDismissal.setScoreId(battingScore.getId());
                            fielderDismissal.setPlayerId(fielderId);
                            fielderDismissal.setTeamId(fielderTeamId);

                            fieldersToAdd.add(fielderDismissal);
                        }

                        this.matchRepository.addFielderDismissals(fieldersToAdd);
                        if(fieldersToAdd.size() > 0)
                        {
                            isUpdateRequired = true;
                        }

                        List<FielderDismissal> fieldersToDelete = existingFielders.stream().filter(fielderDismissal -> !processedFielders.contains(fielderDismissal.getPlayerId())).collect(Collectors.toList());
                        this.matchRepository.removeFielderDismissals(fieldersToDelete);
                        if(fieldersToDelete.size() > 0)
                        {
                            isUpdateRequired = true;
                        }
                    }
                    else
                    {
                        this.matchRepository.removeFielderDismissals(this.matchRepository.getFielderDismissals(Collections.singletonList(battingScore.getId())));
                        isUpdateRequired = true;
                    }
                }

                List<BattingScore> scoresToDelete = existingBattingScores.stream().filter(battingScore -> !processedScores.contains(battingScore.getPlayerId() + "_" + battingScore.getInnings())).collect(Collectors.toList());
                List<Long> scoreIdsToDelete = scoresToDelete.stream().map(BattingScore::getId).collect(Collectors.toList());
                List<Long> bowlerIdsToDelete = scoresToDelete.stream().filter(battingScore -> (null != battingScore.getBowlerDismissalId())).map(BattingScore::getBowlerDismissalId).collect(Collectors.toList());

                this.matchRepository.removeFielderDismissals(this.matchRepository.getFielderDismissals(scoreIdsToDelete));
                this.matchRepository.removeBattingScores(scoresToDelete);
                if(scoresToDelete.size() > 0)
                {
                    isUpdateRequired = true;
                }
                this.matchRepository.removeBowlerDismissals(this.matchRepository.getBowlingDismissals(bowlerIdsToDelete));
            }

            if(null != updateRequest.getBowlingFigures())
            {
                List<BowlingFigure> existingBowlingFigures = this.matchRepository.getBowlingFigures(id);
                Map<String, BowlingFigure> existingFigureMap = existingBowlingFigures.stream().collect(Collectors.toMap(bowlingFigure -> (bowlingFigure.getPlayerId() + "_" + bowlingFigure.getInnings()), bowlingFigure -> bowlingFigure));

                List<BowlingFigure> figuresToAdd = new ArrayList<>();
                List<String> processedFigures = new ArrayList<>();
                for(Map<String, String> bowlingFigureRaw: updateRequest.getBowlingFigures())
                {
                    Long playerId = Long.parseLong(bowlingFigureRaw.get("playerId"));
                    int balls = Integer.parseInt(bowlingFigureRaw.get("balls"));
                    int maidens = Integer.parseInt(bowlingFigureRaw.get("maidens"));
                    int runs = Integer.parseInt(bowlingFigureRaw.get("runs"));
                    int wickets = Integer.parseInt(bowlingFigureRaw.get("wickets"));
                    int innings = Integer.parseInt(bowlingFigureRaw.get("innings"));
                    int teamInnings = Integer.parseInt(bowlingFigureRaw.get("teamInnings"));
                    BowlingFigure bowlingFigure = new BowlingFigure();
                    String key = playerId + "_" + innings;

                    if(processedFigures.contains(key))
                    {
                        continue;
                    }
                    processedFigures.add(key);

                    if(
                            existingFigureMap.containsKey(key)
                                    &&
                                    (existingFigureMap.get(key).getBalls() == balls)
                                    &&
                                    (existingFigureMap.get(key).getRuns() == runs)
                                    &&
                                    (existingFigureMap.get(key).getMaidens() == maidens)
                                    &&
                                    (existingFigureMap.get(key).getWickets() == wickets)
                                    &&
                                    (existingFigureMap.get(key).getInnings() == innings)
                                    &&
                                    (existingFigureMap.get(key).getTeamInnings() == teamInnings)
                    )
                    {
                        continue;
                    }

                    if(existingFigureMap.containsKey(key))
                    {
                        bowlingFigure = existingFigureMap.get(key);
                    }

                    bowlingFigure.setMatchId(id);

                    Player player = this.playerRepository.get(playerId);
                    if(null == player)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowler"));
                    }
                    bowlingFigure.setPlayerId(playerId);

                    Long teamId = existingPlayerMap.get(playerId).getTeamId();
                    Team team = this.teamService.getRaw(teamId);
                    if(null == team)
                    {
                        throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Bowler's Team"));
                    }
                    bowlingFigure.setTeamId(teamId);

                    bowlingFigure.setBalls(balls);
                    bowlingFigure.setMaidens(maidens);
                    bowlingFigure.setRuns(runs);
                    bowlingFigure.setWickets(wickets);
                    bowlingFigure.setInnings(innings);
                    bowlingFigure.setTeamInnings(teamInnings);

                    figuresToAdd.add(bowlingFigure);
                }

                this.matchRepository.addBowlingFigures(figuresToAdd);
                if(figuresToAdd.size() > 0)
                {
                    isUpdateRequired = true;
                }

                List<BowlingFigure> figuresToDelete = existingBowlingFigures.stream().filter(bowlingFigure -> !processedFigures.contains(bowlingFigure.getPlayerId() + "_" + bowlingFigure.getInnings())).collect(Collectors.toList());
                this.matchRepository.removeBowlingFigures(figuresToDelete);
                if(figuresToDelete.size() > 0)
                {
                    isUpdateRequired = true;
                }
            }

            if(null != updateRequest.getManOfTheMatchList())
            {
                List<ManOfTheMatch> existingMOTS = this.matchRepository.getManOfTheMatchList(id);
                Map<Long, ManOfTheMatch> existingMOTSMap = existingMOTS.stream().collect(Collectors.toMap(ManOfTheMatch::getPlayerId, manOfTheMatch -> manOfTheMatch));

                List<ManOfTheMatch> motsToAdd = new ArrayList<>();
                List<Long> processedMOTS = new ArrayList<>();
                for(Long playerId: updateRequest.getManOfTheMatchList())
                {
                    if(!existingPlayerMap.containsKey(playerId))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid player for man of the match");
                    }

                    if(processedMOTS.contains(playerId))
                    {
                        continue;
                    }
                    processedMOTS.add(playerId);

                    if(!existingMOTSMap.containsKey(playerId))
                    {
                        ManOfTheMatch manOfTheMatch = new ManOfTheMatch();
                        manOfTheMatch.setMatchId(id);
                        manOfTheMatch.setPlayerId(playerId);
                        manOfTheMatch.setTeamId(existingPlayerMap.get(playerId).getTeamId());

                        motsToAdd.add(manOfTheMatch);
                    }
                }

                this.matchRepository.addManOfTheMatchList(motsToAdd);
                if(motsToAdd.size() > 0)
                {
                    isUpdateRequired = true;
                }

                List<ManOfTheMatch> motsToDelete = existingMOTS.stream().filter(manOfTheMatch -> (!processedMOTS.contains(manOfTheMatch.getPlayerId()))).collect(Collectors.toList());
                this.matchRepository.removeManOfTheMatchList(motsToDelete);
                if(motsToDelete.size() > 0)
                {
                    isUpdateRequired = true;
                }
            }

            if(null != updateRequest.getCaptains())
            {
                List<Captain> existingCaptains = this.matchRepository.getCaptainsForMatch(id);
                Map<Long, Captain> existingCaptainsMap = existingCaptains.stream().collect(Collectors.toMap(Captain::getPlayerId, captain -> captain));

                List<Captain> captainsToAdd = new ArrayList<>();
                List<Long> processedCaptains = new ArrayList<>();
                for(Long playerId: updateRequest.getCaptains())
                {
                    if(!existingPlayerMap.containsKey(playerId))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid player for captain");
                    }

                    if(processedCaptains.contains(playerId))
                    {
                        continue;
                    }
                    processedCaptains.add(playerId);

                    Long teamId = existingPlayerMap.get(playerId).getTeamId();
                    if(!existingCaptainsMap.containsKey(playerId))
                    {
                        Captain captain = new Captain();
                        captain.setMatchId(id);
                        captain.setPlayerId(playerId);
                        captain.setTeamId(teamId);

                        captainsToAdd.add(captain);
                    }
                    else if(!existingCaptainsMap.get(playerId).getTeamId().equals(teamId))
                    {
                        Captain captain = existingCaptainsMap.get(playerId);
                        captain.setTeamId(teamId);

                        captainsToAdd.add(captain);
                    }
                }

                this.matchRepository.addCaptainsForMatch(captainsToAdd);
                if(captainsToAdd.size() > 0)
                {
                    isUpdateRequired = true;
                }

                List<Captain> captainsToDelete = existingCaptains.stream().filter(captain -> (!processedCaptains.contains(captain.getPlayerId()))).collect(Collectors.toList());
                this.matchRepository.removeCaptains(captainsToDelete);
                if(captainsToDelete.size() > 0)
                {
                    isUpdateRequired = true;
                }
            }

            if(null != updateRequest.getWicketKeepers())
            {
                List<WicketKeeper> existingWicketKeepers = this.matchRepository.getWicketKeepersForMatch(id);
                Map<Long, WicketKeeper> existingWicketKeepersMap = existingWicketKeepers.stream().collect(Collectors.toMap(WicketKeeper::getPlayerId, wicketKeeper -> wicketKeeper));

                List<WicketKeeper> wicketKeepersToAdd = new ArrayList<>();
                List<Long> processedWicketKeepers = new ArrayList<>();
                for(Long playerId: updateRequest.getWicketKeepers())
                {
                    if(!existingPlayerMap.containsKey(playerId))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid player for wicket keeper");
                    }

                    if(processedWicketKeepers.contains(playerId))
                    {
                        continue;
                    }
                    processedWicketKeepers.add(playerId);

                    Long teamId = existingPlayerMap.get(playerId).getTeamId();
                    if(!existingWicketKeepersMap.containsKey(playerId))
                    {
                        WicketKeeper wicketKeeper = new WicketKeeper();
                        wicketKeeper.setMatchId(id);
                        wicketKeeper.setPlayerId(playerId);
                        wicketKeeper.setTeamId(teamId);

                        wicketKeepersToAdd.add(wicketKeeper);
                    }
                    else if(!existingWicketKeepersMap.get(playerId).getTeamId().equals(teamId))
                    {
                        WicketKeeper wicketKeeper = existingWicketKeepersMap.get(playerId);
                        wicketKeeper.setTeamId(teamId);

                        wicketKeepersToAdd.add(wicketKeeper);
                    }
                }

                this.matchRepository.addWicketKeepersForMatch(wicketKeepersToAdd);
                if(wicketKeepersToAdd.size() > 0)
                {
                    isUpdateRequired = true;
                }

                List<WicketKeeper> wicketKeepersToDelete = existingWicketKeepers.stream().filter(wicketKeeper -> (!processedWicketKeepers.contains(wicketKeeper.getPlayerId()))).collect(Collectors.toList());
                this.matchRepository.removeWicketKeepers(wicketKeepersToDelete);
                if(wicketKeepersToDelete.size() > 0)
                {
                    isUpdateRequired = true;
                }
            }

            if(isUpdateRequired)
            {
                Match updatedMatch = this.matchRepository.save(existingMatch);
                transaction.commit();
                transaction.end();
                return updatedMatch;
            }
            else
            {
                return existingMatch;
            }
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), ErrorCode.DB_INTERACTION_FAILED.getDescription());
        }
    }

    @Override
    public boolean delete(Long id)
    {
        Match existingMatch = this.matchRepository.get(id);
        if(null == existingMatch)
        {
            throw new BadRequestException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Match"));
        }

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            this.matchRepository.removeWicketKeepers(this.matchRepository.getWicketKeepersForMatch(id));
            this.matchRepository.removeCaptains(this.matchRepository.getCaptainsForMatch(id));
            this.matchRepository.removeExtrasForMatch(this.matchRepository.getExtras(id));
            this.matchRepository.removeManOfTheMatchList(this.matchRepository.getManOfTheMatchList(id));
            this.matchRepository.removeBowlingFigures(this.matchRepository.getBowlingFigures(id));

            List<BattingScore> battingScores = this.matchRepository.getBattingScores(id);

            this.matchRepository.removeFielderDismissals(this.matchRepository.getFielderDismissals(battingScores.stream().map(BattingScore::getId).collect(Collectors.toList())));
            this.matchRepository.removeBattingScores(battingScores);
            this.matchRepository.removeBowlerDismissals(this.matchRepository.getBowlingDismissals(battingScores.stream().filter(battingScore -> (null != battingScore.getBowlerDismissalId())).map(BattingScore::getBowlerDismissalId).collect(Collectors.toList())));
            this.matchRepository.removePlayers(this.matchRepository.getPlayers(id));
            this.matchRepository.delete(existingMatch);
            transaction.commit();
            transaction.end();
            return true;
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), ErrorCode.DB_INTERACTION_FAILED.getDescription());
        }
    }
}
