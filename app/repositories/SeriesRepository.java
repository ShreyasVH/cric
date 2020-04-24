package repositories;

import com.google.inject.Inject;
import enums.ErrorCode;
import enums.GameType;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Series;
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
            List<Series> series = new ArrayList<>();

            try
            {
                series = this.db.find(Series.class).findList();
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return series;
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Series> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> {
            Series series = null;

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
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Series> get(String name, GameType gameType)
    {
        return CompletableFuture.supplyAsync(() -> {
            Series series = null;

            try
            {
                series = this.db.find(Series.class).where().eq("name", name).eq("gameType", gameType).findOne();
            }
            catch(Exception ex)
            {
                String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
                throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
            }

            return series;
        }, this.databaseExecutionContext);
    }

    public CompletionStage<Series> save(Series series)
    {
        return CompletableFuture.supplyAsync(() -> {
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
        }, this.databaseExecutionContext);
    }
}
