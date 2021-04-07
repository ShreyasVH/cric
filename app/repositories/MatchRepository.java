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

    public BowlerDismissal getBowlingDismissal(Long id)
    {
        BowlerDismissal bowlerDismissal = null;

        try
        {
            bowlerDismissal = this.db.find(BowlerDismissal.class).where().eq("id", id).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return bowlerDismissal;
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
        if(!players.isEmpty())
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
    }

    public void addExtrasForMatch(List<Extras> extras)
    {
        if(!extras.isEmpty())
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
    }

    public void removeExtrasForMatch(List<Extras> extras)
    {
        if(!extras.isEmpty())
        {
            try
            {
                this.db.deleteAll(extras);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public void addBowlingFigures(List<BowlingFigure> bowlingFigures)
    {
        if(!bowlingFigures.isEmpty())
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
    }

    public void removeBowlingFigures(List<BowlingFigure> bowlingFigures)
    {
        if(!bowlingFigures.isEmpty())
        {
            try
            {
                this.db.deleteAll(bowlingFigures);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public void addManOfTheMatchList(List<ManOfTheMatch> manOfTheMatchList)
    {
        if(!manOfTheMatchList.isEmpty())
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
    }

    public void removeManOfTheMatchList(List<ManOfTheMatch> manOfTheMatchList)
    {
        if(!manOfTheMatchList.isEmpty())
        {
            try
            {
                this.db.deleteAll(manOfTheMatchList);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
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

    public void removeBattingScores(List<BattingScore> battingScores)
    {
        if(!battingScores.isEmpty())
        {
            try
            {
                this.db.deleteAll(battingScores);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
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

    public void removeBowlerDismissal(BowlerDismissal bowlerDismissal)
    {
        try
        {
            this.db.delete(bowlerDismissal);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public void removeBowlerDismissals(List<BowlerDismissal> bowlerDismissals)
    {
        if(!bowlerDismissals.isEmpty())
        {
            try
            {
                this.db.deleteAll(bowlerDismissals);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public void addFielderDismissals(List<FielderDismissal> fielderDismissals)
    {
        if(!fielderDismissals.isEmpty())
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

    public void removeFielderDismissals(List<FielderDismissal> fielderDismissals)
    {
        if(!fielderDismissals.isEmpty())
        {
            try
            {
                this.db.deleteAll(fielderDismissals);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public void removePlayers(List<MatchPlayerMap> players)
    {
        if(!players.isEmpty())
        {
            try
            {
                this.db.deleteAll(players);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public List<Match> getMatchesForSeries(Long seriesId)
    {
        List<Match> matches = new ArrayList<>();

        try
        {
            matches = this.db.find(Match.class).where().eq("series", seriesId).eq("isOfficial", 1).orderBy("startTime ASC, id ASC").findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return matches;
    }

    public void addCaptainsForMatch(List<Captain> captains)
    {
        if(!captains.isEmpty())
        {
            try
            {
                this.db.saveAll(captains);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public List<Captain> getCaptainsForMatch(Long matchId)
    {
        List<Captain> captains = new ArrayList<>();
        try
        {
            captains = this.db.find(Captain.class).where().eq("matchId", matchId).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return captains;
    }

    public void removeCaptains(List<Captain> captains)
    {
        if(!captains.isEmpty())
        {
            try
            {
                this.db.deleteAll(captains);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public void addWicketKeepersForMatch(List<WicketKeeper> wicketKeepers)
    {
        if(!wicketKeepers.isEmpty())
        {
            try
            {
                this.db.saveAll(wicketKeepers);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public List<WicketKeeper> getWicketKeepersForMatch(Long matchId)
    {
        List<WicketKeeper> wicketKeepers = new ArrayList<>();
        try
        {
            wicketKeepers = this.db.find(WicketKeeper.class).where().eq("matchId", matchId).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return wicketKeepers;
    }
}
