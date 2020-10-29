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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.Date;
import java.text.SimpleDateFormat;

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

    public Player get(Long id)
    {
        Player player;

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
    }

    public List<Player> get(String keyword)
    {
        List<Player> players;

        try
        {
            players = this.db.find(Player.class).where().icontains("name", keyword).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return players;
    }

    public Player get(String name, Long countryId, Long dateOfBirth)
    {
        Player player;
        try
        {
            player = this.db.find(Player.class).where().eq("name", name).eq("country.id", countryId).eq("dateOfBirth", dateOfBirth).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return player;
    }

    public Map<GameType, Map<String, Integer>> getDismissalStats(Long playerId)
    {
        Map<GameType, Map<String, Integer>> stats = new HashMap<>();

        try
        {
            String query = "SELECT dm.name AS dismissalMode, COUNT(*) AS count, s.game_type as gameType FROM `batting_scores` bs INNER JOIN dismissal_modes dm ON bs.player_id = " + playerId + " AND bs.mode_of_dismissal IS NOT NULL and dm.id = bs.mode_of_dismissal inner join matches m on m.id = bs.match_id inner join series s on s.id = m.series GROUP BY s.game_type, bs.mode_of_dismissal";
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
    }

    public Map<GameType, Map<String, Integer>> getBasicBattingStats(Long playerId)
    {
        Map<GameType, Map<String, Integer>> statsFinal = new HashMap<>();

        try
        {
            String query = "SELECT COUNT(*) AS innings, SUM(runs) AS runs, SUM(balls) AS balls, SUM(fours) AS fours, SUM(sixes) AS sixes, MAX(runs) AS highest, s.game_type as gameType, count(CASE WHEN (bs.runs >= 50 and bs.runs < 100) then 1 end) as fifties, count(CASE WHEN (bs.runs >= 100 and bs.runs < 200) then 1 end) as hundreds, count(CASE WHEN (bs.runs >= 200 and bs.runs < 300) then 1 end) as twoHundreds, count(CASE WHEN (bs.runs >= 300 and bs.runs < 400) then 1 end) as threeHundreds, count(CASE WHEN (bs.runs >= 400 and bs.runs < 500) then 1 end) as fourHundreds FROM `batting_scores` bs inner join matches m on player_id = " + playerId + " and m.id = bs.match_id inner join series s on s.id = m.series group by s.game_type";
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
                    stats.put("fifties", row.getInteger("fifties"));
                    stats.put("hundreds", row.getInteger("hundreds"));
                    stats.put("twoHundreds", row.getInteger("twoHundreds"));
                    stats.put("threeHundreds", row.getInteger("threeHundreds"));
                    stats.put("fourHundreds", row.getInteger("fourHundreds"));

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
    }

    public Map<GameType, Map<String, Integer>> getFieldingStats(Long playerId)
    {
        Map<GameType, Map<String, Integer>> statsFinal = new HashMap<>();

        try
        {
            String query = "select dm.name as dismissalMode, count(*) as count, s.game_type as gameType from fielder_dismissals fd inner join batting_scores bs on bs.id = fd.score_id and fd.player_id = " + playerId + " inner join dismissal_modes dm on dm.id = bs.mode_of_dismissal inner join matches m on m.id = bs.match_id inner join series s on s.id = m.series group by s.game_type, bs.mode_of_dismissal";
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
    }

    public Map<GameType, Map<String, Integer>> getBasicBowlingStats(Long playerId)
    {
        Map<GameType, Map<String, Integer>> statsFinal = new HashMap<>();

        try
        {
            String query = "SELECT COUNT(*) AS innings, SUM(balls) AS balls, SUM(maidens) AS maidens, SUM(runs) AS runs, SUM(wickets) AS wickets, s.game_type AS gameType, COUNT(CASE WHEN (bf.wickets >= 5 and bf.wickets < 10) then 1 end) as fifers,  COUNT(CASE WHEN (bf.wickets = 10) then 1 end) as tenWickets FROM bowling_figures bf INNER JOIN matches m ON bf.player_id = " + playerId + " and m.id = bf.match_id INNER JOIN series s ON s.id = m.series GROUP BY s.game_type";
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
                    stats.put("fifers", row.getInteger("fifers"));
                    stats.put("tenWickets", row.getInteger("tenWickets"));

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
    }

    public Player save(Player player)
    {
        try
        {
            this.db.save(player);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return player;
    }

    public List<Player> getAll(int offset, int count)
    {
        List<Player> players;

        try
        {
            players = this.db.find(Player.class).setMaxRows(count).setFirstRow(offset).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return players;
    }
}
