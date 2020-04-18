package repositories;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import models.Player;
import modules.DatabaseExecutionContext;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class PlayerRepository
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public PlayerRepository
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

    public CompletionStage<Player> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> {
            Player player = null;

            try
            {
                player = this.db.find(Player.class).where().eq("id", id).findOne();
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return player;
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Map<String, Integer>> getDismissalStats(Long playerId)
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> stats = new HashMap<>();

            try
            {
                String query = "SELECT dm.name AS dismissalMode, COUNT(*) AS count FROM `batting_scores` bs INNER JOIN match_player_map mpm ON mpm.id = bs.batsman_id AND mpm.player_id = " +  playerId + " AND bs.mode_of_dismissal IS NOT NULL INNER JOIN dismissal_modes dm ON dm.id = bs.mode_of_dismissal GROUP BY bs.mode_of_dismissal";
                SqlQuery sqlQuery = this.db.createSqlQuery(query);
                List<SqlRow> result = sqlQuery.findList();

                for(SqlRow row: result)
                {
                    stats.put(row.getString("dismissalMode"), row.getInteger("count"));
                }
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return stats;
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Map<String, Integer>> getBasicBattingStats(Long playerId)
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> stats = new HashMap<>();

            try
            {
                String query = "SELECT COUNT(*) AS innings, SUM(runs) AS runs, SUM(balls) AS balls, SUM(fours) AS fours, SUM(sixes) AS sixes, MAX(runs) AS highest FROM `batting_scores` bs INNER JOIN match_player_map mpm ON mpm.id = bs.batsman_id AND mpm.player_id = " + playerId;
                SqlQuery sqlQuery = this.db.createSqlQuery(query);
                List<SqlRow> result = sqlQuery.findList();

                for(SqlRow row: result)
                {
                    Integer innings = row.getInteger("innings");
                    if(innings > 0)
                    {
                        stats.put("innings", innings);
                        stats.put("runs", row.getInteger("runs"));
                        stats.put("balls", row.getInteger("balls"));
                        stats.put("fours", row.getInteger("fours"));
                        stats.put("sixes", row.getInteger("sixes"));
                        stats.put("highest", row.getInteger("highest"));
                    }
                }
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return stats;
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Map<String, Integer>> getFieldingStats(Long playerId)
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> stats = new HashMap<>();

            try
            {
                String query = "SELECT dm.name as dismissalMode, count(*) as count FROM `batting_scores` bs inner join match_player_map mpm on mpm.id = bs.fielder_id and mpm.player_id = " + playerId + " inner join dismissal_modes dm on dm.id = bs.mode_of_dismissal group by bs.mode_of_dismissal";
                SqlQuery sqlQuery = this.db.createSqlQuery(query);
                List<SqlRow> result = sqlQuery.findList();

                for(SqlRow row: result)
                {
                    stats.put(row.getString("dismissalMode"), row.getInteger("count"));
                }
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return stats;
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Map<String, Integer>> getBasicBowlingStats(Long playerId)
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> stats = new HashMap<>();

            try
            {
                String query = "SELECT COUNT(*) AS innings, SUM(balls) AS balls, SUM(maidens) AS maidens, SUM(runs) AS runs, SUM(wickets) AS wickets FROM bowling_figures bf INNER JOIN match_player_map mpm ON mpm.id = bf.bowler_id AND mpm.player_id = " + playerId;
                SqlQuery sqlQuery = this.db.createSqlQuery(query);
                List<SqlRow> result = sqlQuery.findList();

                for(SqlRow row: result)
                {
                    Integer innings = row.getInteger("innings");
                    if(innings > 0)
                    {
                        stats.put("innings", innings);
                        stats.put("runs", row.getInteger("runs"));
                        stats.put("balls", row.getInteger("balls"));
                        stats.put("maidens", row.getInteger("maidens"));
                        stats.put("wickets", row.getInteger("wickets"));
                    }
                }
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return stats;
        }, this.databaseExecutionContext);
    }
}
