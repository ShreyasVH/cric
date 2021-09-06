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
import requests.stats.FilterRequest;
import responses.StatsResponse;

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
            player = this.db.find(Player.class).where().eq("name", name).eq("countryId", countryId).eq("dateOfBirth", dateOfBirth).findOne();
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
            String query = "SELECT dm.name AS dismissalMode, COUNT(*) AS count, s.game_type as gameType FROM `batting_scores` bs INNER JOIN dismissal_modes dm ON bs.player_id = " + playerId + " AND bs.mode_of_dismissal IS NOT NULL and dm.id = bs.mode_of_dismissal and dm.name != 'Retired Hurt' inner join matches m on m.id = bs.match_id and m.is_official = 1 inner join series s on s.id = m.series inner join teams t on t.id = bs.team_id and t.team_type_id = 0 GROUP BY s.game_type, bs.mode_of_dismissal";
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
            String query = "SELECT COUNT(*) AS innings, SUM(runs) AS runs, SUM(balls) AS balls, SUM(fours) AS fours, SUM(sixes) AS sixes, MAX(runs) AS highest, s.game_type as gameType, count(CASE WHEN (bs.runs >= 50 and bs.runs < 100) then 1 end) as fifties, count(CASE WHEN (bs.runs >= 100 and bs.runs < 200) then 1 end) as hundreds, count(CASE WHEN (bs.runs >= 200 and bs.runs < 300) then 1 end) as twoHundreds, count(CASE WHEN (bs.runs >= 300 and bs.runs < 400) then 1 end) as threeHundreds, count(CASE WHEN (bs.runs >= 400 and bs.runs < 500) then 1 end) as fourHundreds FROM `batting_scores` bs inner join matches m on player_id = " + playerId + " and m.id = bs.match_id and m.is_official = 1 inner join series s on s.id = m.series  inner join teams t on t.id = bs.team_id and t.team_type_id = 0 group by s.game_type";
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
            String query = "select dm.name as dismissalMode, count(*) as count, s.game_type as gameType from fielder_dismissals fd inner join batting_scores bs on bs.id = fd.score_id and fd.player_id = " + playerId + " inner join dismissal_modes dm on dm.id = bs.mode_of_dismissal inner join matches m on m.id = bs.match_id and m.is_official = 1 inner join series s on s.id = m.series inner join teams t on t.id = fd.team_id and t.team_type_id = 0 group by s.game_type, bs.mode_of_dismissal";
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
            String query = "SELECT COUNT(*) AS innings, SUM(balls) AS balls, SUM(maidens) AS maidens, SUM(runs) AS runs, SUM(wickets) AS wickets, s.game_type AS gameType, COUNT(CASE WHEN (bf.wickets >= 5 and bf.wickets < 10) then 1 end) as fifers,  COUNT(CASE WHEN (bf.wickets = 10) then 1 end) as tenWickets FROM bowling_figures bf INNER JOIN matches m ON bf.player_id = " + playerId + " and m.id = bf.match_id INNER JOIN series s ON s.id = m.series and m.is_official = 1 inner join teams t on t.id = bf.team_id and t.team_type_id = 0 GROUP BY s.game_type";
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
            players = this.db.find(Player.class).orderBy("name ASC").setMaxRows(count).setFirstRow(offset).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return players;
    }

    public StatsResponse getBattingStats(FilterRequest filterRequest)
    {
        StatsResponse statsResponse = new StatsResponse();
        List<Map<String, String>> statList = new ArrayList<>();
        String query = "select p.id as playerId, p.name AS name, sum(bs.runs) AS `runs`, count(0) AS `innings`, sum(`bs`.`balls`) AS `balls`, sum(`bs`.`fours`) AS `fours`, sum(`bs`.`sixes`) AS `sixes`, max(`bs`.`runs`) AS `highest`, count((case when (`bs`.`mode_of_dismissal` is null) then 1 end)) AS `notouts`, count((case when ((`bs`.`runs` >= 50) and (`bs`.`runs` < 100)) then 1 end)) AS `fifties`, count((case when ((`bs`.`runs` >= 100)) then 1 end)) AS `hundreds` from batting_scores bs " +
                "inner join players p on p.id = bs.player_id " +
                "inner join matches m on m.id = bs.match_id " +
                "inner join series s on s.id = m.series " +
                "inner join stadiums st on st.id = m.stadium " +
                "inner join teams t on t.id = bs.team_id";

        String countQuery = "select count(distinct p.id) as count from batting_scores bs " +
                "inner join players p on p.id = bs.player_id " +
                "inner join matches m on m.id = bs.match_id " +
                "inner join series s on s.id = m.series " +
                "inner join stadiums st on st.id = m.stadium " +
                "inner join teams t on t.id = bs.team_id";

        //where
        List<String> whereQueryParts = new ArrayList<>();
        for(Map.Entry<String, List<String>> entry: filterRequest.getFilters().entrySet())
        {
            String field = entry.getKey();
            List<String> valueList = entry.getValue();

            String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
            if(!fieldNameWithTablePrefix.isEmpty() && !valueList.isEmpty())
            {
                whereQueryParts.add(fieldNameWithTablePrefix + " in (" + String.join(", ", valueList) + ")");
            }
        }

        for(Map.Entry<String, Map<String, String>> entry: filterRequest.getRangeFilters().entrySet())
        {
            String field = entry.getKey();
            Map<String, String> rangeValues = entry.getValue();

            String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
            if(!fieldNameWithTablePrefix.isEmpty() && !rangeValues.isEmpty())
            {
                if(rangeValues.containsKey("from"))
                {
                    whereQueryParts.add(fieldNameWithTablePrefix + " >= " +  rangeValues.get("from"));
                }
                if(rangeValues.containsKey("to"))
                {
                    whereQueryParts.add(fieldNameWithTablePrefix + " <= " +  rangeValues.get("to"));
                }

            }
        }

        if(!whereQueryParts.isEmpty())
        {
            query += " where " + String.join(" and ", whereQueryParts);
            countQuery += " where " + String.join(" and ", whereQueryParts);
        }

        query += " group by playerId";

        //sort
        List<String> sortList = new ArrayList<>();
        for(Map.Entry<String, String> entry: filterRequest.getSortMap().entrySet())
        {
            String field = entry.getKey();
            String value = entry.getValue();

            String sortFieldName = getFieldNameForDisplay(field);
            if(!sortFieldName.isEmpty())
            {
                sortList.add(sortFieldName + " " + value);
            }
        }
        if(sortList.isEmpty())
        {
            sortList.add(getFieldNameForDisplay("runs") + " desc");
        }
        query += " order by " + String.join(", ", sortList);

        //offset limit
        query += " limit " + Integer.min(30, filterRequest.getCount()) + " offset " + filterRequest.getOffset();

        try
        {
            SqlQuery sqlCountQuery = this.db.createSqlQuery(countQuery);
            List<SqlRow> countResult = sqlCountQuery.findList();
            statsResponse.setCount(countResult.get(0).getInteger("count"));

            SqlQuery sqlQuery = this.db.createSqlQuery(query);
            List<SqlRow> result = sqlQuery.findList();

            for(SqlRow row: result)
            {
                Integer innings = row.getInteger("innings");
                if(innings > 0)
                {
                    Map<String, String> stats = new HashMap<>();

                    stats.put("id", row.getString("playerId"));
                    stats.put("name", row.getString("name"));
                    stats.put("innings", innings.toString());
                    stats.put("runs", row.getString("runs"));
                    stats.put("balls", row.getString("balls"));
                    stats.put("notOuts", row.getString("notouts"));
                    stats.put("fours", row.getString("fours"));
                    stats.put("sixes", row.getString("sixes"));
                    stats.put("highest", row.getString("highest"));
                    stats.put("fifties", row.getString("fifties"));
                    stats.put("hundreds", row.getString("hundreds"));
                    stats.put("twoHundreds", row.getString("twoHundreds"));
                    stats.put("threeHundreds", row.getString("threeHundreds"));
                    stats.put("fourHundreds", row.getString("fourHundreds"));

                    statList.add(stats);
                }
            }
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        statsResponse.setStats(statList);

        return statsResponse;
    }

    public StatsResponse getBowlingStats(FilterRequest filterRequest)
    {
        StatsResponse statsResponse = new StatsResponse();
        List<Map<String, String>> statList = new ArrayList<>();
        String query = "select p.id as playerId, p.name AS name, sum(bf.wickets) AS wickets, sum(bf.runs) as runs, count(0) AS `innings`, sum(`bf`.`balls`) AS `balls`, sum(`bf`.`maidens`) AS `maidens`, count((case when ((`bf`.`wickets` >= 5) and (`bf`.`wickets` < 10)) then 1 end)) AS `fifers`, count((case when (`bf`.`wickets` = 10) then 1 end)) AS `tenWickets` from bowling_figures bf " +
                "inner join players p on p.id = bf.player_id " +
                "inner join matches m on m.id = bf.match_id " +
                "inner join series s on s.id = m.series " +
                "inner join stadiums st on st.id = m.stadium " +
                "inner join teams t on t.id = bf.team_id";

        String countQuery = "select count(distinct p.id) as count from bowling_figures bf " +
                "inner join players p on p.id = bf.player_id " +
                "inner join matches m on m.id = bf.match_id " +
                "inner join series s on s.id = m.series " +
                "inner join stadiums st on st.id = m.stadium " +
                "inner join teams t on t.id = bf.team_id";

        //where
        List<String> whereQueryParts = new ArrayList<>();
        for(Map.Entry<String, List<String>> entry: filterRequest.getFilters().entrySet())
        {
            String field = entry.getKey();
            List<String> valueList = entry.getValue();

            String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
            if(!fieldNameWithTablePrefix.isEmpty() && !valueList.isEmpty())
            {
                whereQueryParts.add(fieldNameWithTablePrefix + " in (" + String.join(", ", valueList) + ")");
            }
        }

        for(Map.Entry<String, Map<String, String>> entry: filterRequest.getRangeFilters().entrySet())
        {
            String field = entry.getKey();
            Map<String, String> rangeValues = entry.getValue();

            String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
            if(!fieldNameWithTablePrefix.isEmpty() && !rangeValues.isEmpty())
            {
                if(rangeValues.containsKey("from"))
                {
                    whereQueryParts.add(fieldNameWithTablePrefix + " >= " +  rangeValues.get("from"));
                }
                if(rangeValues.containsKey("to"))
                {
                    whereQueryParts.add(fieldNameWithTablePrefix + " <= " +  rangeValues.get("to"));
                }

            }
        }

        if(!whereQueryParts.isEmpty())
        {
            query += " where " + String.join(" and ", whereQueryParts);
            countQuery += " where " + String.join(" and ", whereQueryParts);
        }

        query += " group by playerId";

        //sort
        List<String> sortList = new ArrayList<>();
        for(Map.Entry<String, String> entry: filterRequest.getSortMap().entrySet())
        {
            String field = entry.getKey();
            String value = entry.getValue();

            String sortFieldName = getFieldNameForDisplay(field);
            if(!sortFieldName.isEmpty())
            {
                sortList.add(sortFieldName + " " + value);
            }
        }
        if(sortList.isEmpty())
        {
            sortList.add(getFieldNameForDisplay("wickets") + " desc");
        }
        query += " order by " + String.join(", ", sortList);

        //offset limit
        query += " limit " + Integer.min(30, filterRequest.getCount()) + " offset " + filterRequest.getOffset();

        try
        {
            SqlQuery sqlCountQuery = this.db.createSqlQuery(countQuery);
            List<SqlRow> countResult = sqlCountQuery.findList();
            statsResponse.setCount(countResult.get(0).getInteger("count"));

            SqlQuery sqlQuery = this.db.createSqlQuery(query);
            List<SqlRow> result = sqlQuery.findList();

            for(SqlRow row: result)
            {
                Integer innings = row.getInteger("innings");
                if(innings > 0)
                {
                    Map<String, String> stats = new HashMap<>();

                    stats.put("id", row.getString("playerId"));
                    stats.put("name", row.getString("name"));
                    stats.put("innings", innings.toString());
                    stats.put("wickets", row.getString("wickets"));
                    stats.put("runs", row.getString("runs"));
                    stats.put("balls", row.getString("balls"));
                    stats.put("maidens", row.getString("maidens"));
                    stats.put("fifers", row.getString("fifers"));
                    stats.put("tenWickets", row.getString("tenWickets"));

                    statList.add(stats);
                }
            }
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        statsResponse.setStats(statList);
        return statsResponse;
    }

    public String getFieldNameWithTablePrefix(String field)
    {
        String fieldName = "";

        switch(field)
        {
            case "gameType":
                fieldName = "s.game_type";
                break;
            case "stadium":
                fieldName = "m.stadium";
                break;
            case "team":
                fieldName = "t.id";
                break;
            case "opposingTeam":
                fieldName = "IF(t.id = m.team_1, m.team_2, m.team_1)";
                break;
            case "teamType":
                fieldName = "t.team_type_id";
                break;
            case "country":
                fieldName = "p.country_id";
                break;
            case "series":
                fieldName = "s.id";
                break;
            case "year":
                fieldName = "FROM_UNIXTIME(m.start_time / 1000, '%Y')";
                break;
        }

        return fieldName;
    }

    public String getFieldNameForDisplay(String field)
    {
        String fieldName = "";

        switch(field)
        {
            case "runs":
                fieldName = "runs";
                break;
            case "balls":
                fieldName = "balls";
                break;
            case "innings":
                fieldName = "innings";
                break;
            case "notOuts":
                fieldName = "notouts";
                break;
            case "fifties":
                fieldName = "fifties";
                break;
            case "hundreds":
                fieldName = "hundreds";
                break;
            case "highest":
                fieldName = "highest";
                break;
            case "fours":
                fieldName = "fours";
                break;
            case "sixes":
                fieldName = "sixes";
                break;
            case "wickets":
                fieldName = "wickets";
                break;
            case "maidens":
                fieldName = "maidens";
                break;
            case "fifers":
                fieldName = "fifers";
                break;
            case "tenWickets":
                fieldName = "tenWickets";
                break;
        }

        return fieldName;
    }
}
