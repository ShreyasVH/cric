package modules;

import services.CountryService;
import services.impl.CountryServiceImpl;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule
{
	@Override
	public void configure()
	{
		bind(CountryService.class).to(CountryServiceImpl.class).asEagerSingleton();
	}
}