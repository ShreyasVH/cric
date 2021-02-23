package repositories;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.*;
import models.Tour;
import modules.DatabaseExecutionContext;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TourRepository
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public TourRepository
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

    public Tour get(Long id)
    {
        Tour tour;

        try
        {
            tour = this.db.find(Tour.class).where().eq("id", id).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return tour;
    }

    public Tour get(String name, Long startTime)
    {
        Tour tour;

        try
        {
            tour = this.db.find(Tour.class).where().eq("name", name).eq("startTime", startTime).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return tour;
    }

    public Tour save(Tour tour)
    {
        try
        {
            this.db.save(tour);
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return tour;
    }

    public List<Tour> filter(int year, Integer offset, Integer count)
    {
        List<Tour> tours;

        try
        {
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startTime = calendar.getTime();

            calendar.set(Calendar.YEAR, year + 1);
            Date endTime = calendar.getTime();

            tours = this.db.find(Tour.class)
                    .setDisableLazyLoading(true)
                    .where()
                    .ge("startTime", startTime.getTime())
                    .lt("startTime", endTime.getTime())
                    .setMaxRows(count)
                    .setFirstRow(offset)
                    .order("startTime DESC")
                    .findList();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return tours;
    }

    public List<Integer> getYears()
    {
        List<Integer> years = new ArrayList<>();

        try
        {
            String query = "SELECT DISTINCT DATE_FORMAT(DATE_ADD(FROM_UNIXTIME(0), INTERVAL start_time / 1000 SECOND), '%Y') AS year FROM `tours` ORDER BY `year` DESC";
            SqlQuery sqlQuery = this.db.createSqlQuery(query);
            List<SqlRow> result = sqlQuery.findList();

            for(SqlRow row: result)
            {
                Integer year = row.getInteger("year");
                years.add(year);
            }
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        return years;
    }
}
