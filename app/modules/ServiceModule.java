package modules;

import services.*;
import services.impl.*;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule
{
	@Override
	public void configure()
	{
		bind(CountryService.class).to(CountryServiceImpl.class).asEagerSingleton();
		bind(StadiumService.class).to(StadiumServiceImpl.class).asEagerSingleton();
		bind(TeamService.class).to(TeamServiceImpl.class).asEagerSingleton();
		bind(PlayerService.class).to(PlayerServiceImpl.class).asEagerSingleton();
		bind(SeriesService.class).to(SeriesServiceImpl.class).asEagerSingleton();
	}
}