package repositories;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.*;
import modules.DatabaseExecutionContext;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MatchRepository
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public MatchRepository
    (
        EbeanConfig ebeanConfig,
        EbeanDynamicEvolutions ebeanDynamicEvolutions,
        DatabaseExecutionContext databaseExecutionContext
    )
    {
        this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
        this.db = Ebean.getServer(ebeanConfig.defaultServer());
        this.databaseExecutionContext = databaseExecutionContext;
    }

    public Match get(Long id)
    {
        Match match;

        try
        {
            match = this.db.find(Match.class).where().eq("id", id).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return match;
    }

    public Match save(Match match)
    {
        try
        {
            this.db.save(match);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return match;
    }

    public boolean delete(Match match)
    {
        boolean success = false;

        try
        {
            this.db.delete(match);
            success = true;
        }
        catch (Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return success;
    }

    public Match get(Long stadiumId, Long startTime)
    {
        Match match = null;

        try
        {
            match = this.db.find(Match.class).where().eq("stadium", stadiumId).eq("startTime", startTime).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return match;
    }

    public List<BattingScore> getBattingScores(Long matchId)
    {
        List<BattingScore> battingScores = new ArrayList<>();

        try
        {
            battingScores = this.db.find(BattingScore.class).where().eq("matchId", matchId).orderBy("id ASC").findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return battingScores;
    }

    public List<BowlerDismissal> getBowlingDismissals(List<Long> ids)
    {
        List<BowlerDismissal> bowlerDismissals = new ArrayList<>();

        try
        {
            bowlerDismissals = this.db.find(BowlerDismissal.class).where().in("id", ids).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return bowlerDismissals;
    }

    public List<FielderDismissal> getFielderDismissals(List<Long> ids)
    {
        List<FielderDismissal> fielderDismissals = new ArrayList<>();

        try
        {
            fielderDismissals = this.db.find(FielderDismissal.class).where().in("scoreId", ids).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return fielderDismissals;
    }

    public List<BowlingFigure> getBowlingFigures(Long matchId)
    {
        List<BowlingFigure> bowlingFigures = new ArrayList<>();

        try
        {
            bowlingFigures = this.db.find(BowlingFigure.class).where().in("matchId", matchId).orderBy("id ASC").findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return bowlingFigures;
    }

    public List<Extras> getExtras(Long matchId)
    {
        List<Extras> extras = new ArrayList<>();

        try
        {
            extras = this.db.find(Extras.class).where().in("matchId", matchId).orderBy("id ASC").findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return extras;
    }

    public List<MatchPlayerMap> getPlayers(Long matchId)
    {
        List<MatchPlayerMap> players = new ArrayList<>();

        try
        {
            players = this.db.find(MatchPlayerMap.class).where().in("matchId", matchId).orderBy("id ASC").findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return players;
    }

    public List<ManOfTheMatch> getManOfTheMatchList(Long matchId)
    {
        List<ManOfTheMatch> manOfTheMatchList = new ArrayList<>();

        try
        {
            manOfTheMatchList = this.db.find(ManOfTheMatch.class).where().in("matchId", matchId).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return manOfTheMatchList;
    }

    public void addPlayersForMatch(List<MatchPlayerMap> players)
    {
        try
        {
            this.db.saveAll(players);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public void addExtrasForMatch(List<Extras> extras)
    {
        try
        {
            this.db.saveAll(extras);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public void addBowlingFigures(List<BowlingFigure> bowlingFigures)
    {
        try
        {
            this.db.saveAll(bowlingFigures);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public void addManOfTheMatchList(List<ManOfTheMatch> manOfTheMatchList)
    {
        try
        {
            this.db.saveAll(manOfTheMatchList);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public void addBattingScore(BattingScore battingScore)
    {
        try
        {
            this.db.save(battingScore);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public void addBowlerDismissal(BowlerDismissal bowlerDismissal)
    {
        try
        {
            this.db.save(bowlerDismissal);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public void addFielderDismissals(List<FielderDismissal> fielderDismissals)
    {
        try
        {
            this.db.saveAll(fielderDismissals);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

}
