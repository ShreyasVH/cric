package repositories;

import com.google.inject.Inject;
import enums.ErrorCode;
import enums.GameType;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.ManOfTheSeries;
import models.Series;
import models.SeriesTeamsMap;
import modules.DatabaseExecutionContext;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SeriesRepository
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public SeriesRepository
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

    public CompletionStage<List<Series>> getAll()
    {
        return CompletableFuture.supplyAsync(() -> {
            List<Series> series;

            try
            {
                series = this.db.find(Series.class).setDisableLazyLoading(true).findList();
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return series;
        }, this.databaseExecutionContext);
    }

    public Series get(Long id)
    {
        Series series;

        try
        {
            series = this.db.find(Series.class).where().eq("id", id).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return series;
    }

    public Series get(String name, GameType gameType, Long startTime)
    {
        Series series;

        try
        {
            series = this.db.find(Series.class).where().eq("name", name).eq("gameType", gameType).eq("startTime", startTime).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return series;
    }

    public List<Series> get(String keyword)
    {
        try
        {
            return this.db.find(Series.class).where().icontains("name", keyword).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public Series save(Series series)
    {
        try
        {
            this.db.save(series);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return series;
    }

    public List<Series> getSeriesListForTour(Long tourId)
    {
        return this.db.find(Series.class).where().eq("tourId", tourId).orderBy("startTime ASC, id ASC").findList();
    }

    public List<SeriesTeamsMap> getTeamsForSeries(Long seriesId)
    {
        return this.db.find(SeriesTeamsMap.class).where().eq("seriesId", seriesId).findList();
    }

    public void addTeamsToSeries(List<Long> teamIds, Long seriesId)
    {
        if(teamIds.size() > 0)
        {
            try
            {
                List<SeriesTeamsMap> seriesTeamsMaps = new ArrayList<>();
                for(Long teamId: teamIds)
                {
                    SeriesTeamsMap seriesTeamsMap = new SeriesTeamsMap();
                    seriesTeamsMap.setSeriesId(seriesId);
                    seriesTeamsMap.setTeamId(teamId);

                    seriesTeamsMaps.add(seriesTeamsMap);
                }
                this.db.saveAll(seriesTeamsMaps);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public void removeTeamsFromSeries(List<Long> teamIds, Long seriesId)
    {
        if(teamIds.size() > 0)
        {
            try
            {
                this.db.deleteAll(
                    this.db.find(SeriesTeamsMap.class).where().eq("seriesId", seriesId).in("teamId", teamIds).findList()
                );
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public void addManOfTheSeriesToSeries(List<ManOfTheSeries> manOfTheSeriesList)
    {
        if(manOfTheSeriesList.size() > 0)
        {
            try
            {
                this.db.saveAll(manOfTheSeriesList);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public void removeManOfTheSeriesToSeries(List<ManOfTheSeries> manOfTheSeriesList)
    {
        if(manOfTheSeriesList.size() > 0)
        {
            try
            {
                this.db.deleteAll(manOfTheSeriesList);
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }
        }
    }

    public List<ManOfTheSeries> getManOfTheSeriesForSeries(Long seriesId)
    {
        List<ManOfTheSeries> manOfTheSeriesList = new ArrayList<>();
        try
        {
            manOfTheSeriesList = this.db.find(ManOfTheSeries.class).where().eq("seriesId", seriesId).findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return manOfTheSeriesList;
    }
}
