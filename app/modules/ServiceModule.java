package modules;

import services.CountryService;
import services.StadiumService;
import services.impl.CountryServiceImpl;

import com.google.inject.AbstractModule;
import services.impl.StadiumServiceImpl;

public class ServiceModule extends AbstractModule
{
	@Override
	public void configure()
	{
		bind(CountryService.class).to(CountryServiceImpl.class).asEagerSingleton();
		bind(StadiumService.class).to(StadiumServiceImpl.class).asEagerSingleton();
	}
}