package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import play.db.ebean.EbeanConfig;

import models.Country;
import java.util.List;
import java.util.ArrayList;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

public class CountryRepository
{
	private final DatabaseExecutionContext databaseExecutionContext;
	private final EbeanConfig ebeanConfig;

	@Inject
	public CountryRepository
	(
		EbeanConfig ebeanConfig,
		DatabaseExecutionContext databaseExecutionContext
	)
	{
		this.ebeanConfig = ebeanConfig;
		this.databaseExecutionContext = databaseExecutionContext;
	}

	private EbeanServer getConnection()
	{
		return Ebean.getServer(this.ebeanConfig.defaultServer());
	}

	public CompletionStage<List<Country>> getAll()
	{
		return CompletableFuture.supplyAsync(() -> {
			EbeanServer db = this.getConnection();
			List<Country> countries = new ArrayList<>();

			try
			{
				countries = db.find(Country.class).findList();
			}
			catch(Exception ex)
			{
				String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
				throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
			}

			return countries;
		}, this.databaseExecutionContext);
	}

	public CompletionStage<Country> get(Long id)
	{
		return CompletableFuture.supplyAsync(() -> {
			Country country = null;
			EbeanServer db = this.getConnection();
			try
			{
				country = db.find(Country.class).where().eq("id", id).findOne();
			}
			catch(Exception ex)
			{
				String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
				throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
			}

			if(null == country)
			{
				throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Country"));
			}

			return country;
		});
	}
}