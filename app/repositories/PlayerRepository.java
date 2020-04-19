package repositories;

import com.google.inject.Inject;
import enums.ErrorCode;
import enums.GameType;
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

    public CompletionStage<Map<GameType, Map<String, Integer>>> getDismissalStats(Long playerId)
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<GameType, Map<String, Integer>> stats = new HashMap<>();

            try
            {
                String query = "SELECT dm.name AS dismissalMode, COUNT(*) AS count, s.match_type as gameType FROM `batting_scores` bs INNER JOIN match_player_map mpm ON mpm.id = bs.batsman_id AND mpm.player_id = " + playerId + " AND bs.mode_of_dismissal IS NOT NULL INNER JOIN dismissal_modes dm ON dm.id = bs.mode_of_dismissal inner join matches m on m.id = mpm.match_id inner join series s on s.id = m.series GROUP BY s.match_type, bs.mode_of_dismissal";
                SqlQuery sqlQuery = this.db.createSqlQuery(query);
                List<SqlRow> result = sqlQuery.findList();

                for(SqlRow row: result)
                {
                    Integer gameTypeId = row.getInteger("gameType");
                    GameType gameType = GameType.values()[gameTypeId];
                    if(stats.containsKey(gameType))
                    {
                        stats.get(gameType).put(row.getString("dismissalMode"), row.getInteger("count"));
                    }
                    else
                    {
                        Map<String, Integer> partStats = new HashMap<String, Integer>(){
                            {
                                put(row.getString("dismissalMode"), row.getInteger("count"));
                            }
                        };
                        stats.put(gameType, partStats);
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

    public CompletionStage<Map<GameType, Map<String, Integer>>> getBasicBattingStats(Long playerId)
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<GameType, Map<String, Integer>> statsFinal = new HashMap<>();

            try
            {
                String query = "SELECT COUNT(*) AS innings, SUM(runs) AS runs, SUM(balls) AS balls, SUM(fours) AS fours, SUM(sixes) AS sixes, MAX(runs) AS highest, s.match_type as gameType FROM `batting_scores` bs INNER JOIN match_player_map mpm ON mpm.id = bs.batsman_id AND mpm.player_id = " + playerId + " inner join matches m on m.id = mpm.match_id inner join series s on s.id = m.series group by s.match_type";
                SqlQuery sqlQuery = this.db.createSqlQuery(query);
                List<SqlRow> result = sqlQuery.findList();

                for(SqlRow row: result)
                {
                    Integer gameTypeId = row.getInteger("gameType");
                    GameType gameType = GameType.values()[gameTypeId];
                    Integer innings = row.getInteger("innings");
                    if(innings > 0)
                    {
                        Map<String, Integer> stats = new HashMap<>();

                        stats.put("innings", innings);
                        stats.put("runs", row.getInteger("runs"));
                        stats.put("balls", row.getInteger("balls"));
                        stats.put("fours", row.getInteger("fours"));
                        stats.put("sixes", row.getInteger("sixes"));
                        stats.put("highest", row.getInteger("highest"));

                        statsFinal.put(gameType, stats);
                    }
                }
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return statsFinal;
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Map<GameType, Map<String, Integer>>> getFieldingStats(Long playerId)
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<GameType, Map<String, Integer>> statsFinal = new HashMap<>();

            try
            {
                String query = "SELECT dm.name as dismissalMode, count(*) as count, s.match_type as gameType FROM `batting_scores` bs inner join match_player_map mpm on mpm.id = bs.fielder_id and mpm.player_id = " + playerId + " inner join dismissal_modes dm on dm.id = bs.mode_of_dismissal inner join matches m on m.id = mpm.match_id inner join series s on s.id = m.series group by s.match_type, bs.mode_of_dismissal";
                SqlQuery sqlQuery = this.db.createSqlQuery(query);
                List<SqlRow> result = sqlQuery.findList();

                for(SqlRow row: result)
                {
                    Integer gameTypeId = row.getInteger("gameType");
                    GameType gameType = GameType.values()[gameTypeId];
                    if(statsFinal.containsKey(gameType))
                    {
                        statsFinal.get(gameType).put(row.getString("dismissalMode"), row.getInteger("count"));
                    }
                    else
                    {
                        statsFinal.put(gameType, new HashMap<String, Integer>(){
                            {
                                put(row.getString("dismissalMode"), row.getInteger("count"));
                            }
                        });
                    }
                }
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return statsFinal;
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Map<GameType, Map<String, Integer>>> getBasicBowlingStats(Long playerId)
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<GameType, Map<String, Integer>> statsFinal = new HashMap<>();

            try
            {
                String query = "SELECT COUNT(*) AS innings, SUM(balls) AS balls, SUM(maidens) AS maidens, SUM(runs) AS runs, SUM(wickets) AS wickets, s.match_type AS gameType FROM bowling_figures bf INNER JOIN match_player_map mpm ON mpm.id = bf.bowler_id AND mpm.player_id = " + playerId + " INNER JOIN matches m ON m.id = mpm.match_id INNER JOIN series s ON s.id = m.series GROUP BY s.match_type";
                SqlQuery sqlQuery = this.db.createSqlQuery(query);
                List<SqlRow> result = sqlQuery.findList();

                for(SqlRow row: result)
                {
                    Integer gameTypeId = row.getInteger("gameType");
                    GameType gameType = GameType.values()[gameTypeId];
                    Integer innings = row.getInteger("innings");
                    if(innings > 0)
                    {
                        Map<String, Integer> stats = new HashMap<>();

                        stats.put("innings", innings);
                        stats.put("runs", row.getInteger("runs"));
                        stats.put("balls", row.getInteger("balls"));
                        stats.put("maidens", row.getInteger("maidens"));
                        stats.put("wickets", row.getInteger("wickets"));

                        statsFinal.put(gameType, stats);
                    }
                }
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return statsFinal;
        }, this.databaseExecutionContext);
    }
}
