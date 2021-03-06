package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import play.db.ebean.EbeanConfig;

import models.Country;
import java.util.List;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;
import play.db.ebean.EbeanDynamicEvolutions;

public class CountryRepository
{
	private final EbeanServer db;
	private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public CountryRepository
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

	public CompletionStage<List<Country>> getAll()
	{
		return CompletableFuture.supplyAsync(() -> {
			List<Country> countries;

			try
			{
				countries = this.db.find(Country.class).findList();
			}
			catch(Exception ex)
			{
				String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
				throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
			}

			return countries;
		}, this.databaseExecutionContext);
	}

	public Country get(Long id)
	{
		Country country;
		try
		{
			country = this.db.find(Country.class).where().eq("id", id).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return country;
	}

	public Country save(Country country)
	{
		try
		{
			this.db.save(country);
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
		return country;
	}

	public Country get(String name)
	{
		Country country;
		try
		{
			country = this.db.find(Country.class).where().eq("name", name).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return country;
	}
}