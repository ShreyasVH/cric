package repositories;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import play.db.ebean.EbeanConfig;

import models.Country;
import java.util.List;
import java.util.ArrayList;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

public class CountryRepository
{
	private final EbeanServer db;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public CountryRepository
	(
		EbeanConfig ebeanConfig,
		DatabaseExecutionContext databaseExecutionContext
	)
	{
		this.db = Ebean.getServer(ebeanConfig.defaultServer());
		this.databaseExecutionContext = databaseExecutionContext;
	}

	public CompletionStage<List<Country>> getAll()
	{
		return CompletableFuture.supplyAsync(() -> {
			List<Country> countries = new ArrayList<>();

			try
			{
				countries = this.db.find(Country.class).findList();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}

			return countries;
		}, this.databaseExecutionContext);
	}
}