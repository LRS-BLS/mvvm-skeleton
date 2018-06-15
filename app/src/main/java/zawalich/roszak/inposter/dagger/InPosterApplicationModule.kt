package zawalich.roszak.inposter.dagger

import dagger.Module
import dagger.Provides
import zawalich.roszak.inposter.navigation.EnumBasedViewClassResolverStrategy
import zawalich.roszak.inposter.navigation.NavigationOnRegionsService
import zawalich.roszak.inposter.navigation.NavigationService
import zawalich.roszak.inposter.navigation.ViewClassResolverStrategy
import zawalich.roszak.inposter.repositories.FooRepository
import zawalich.roszak.inposter.repositories.RxPermissionsRepository
import zawalich.roszak.inposter.repositories.interfaces.FooBarRepository
import zawalich.roszak.inposter.repositories.interfaces.PermissionsRepository
import javax.inject.Singleton

@Module
class InPosterApplicationModule {

	@Provides
	@Singleton
	fun provideFooBarRepository(): FooBarRepository {
		return FooRepository()
	}

	@Provides
	@Singleton
	fun provideNavigationService() : NavigationService {
		return NavigationOnRegionsService()
	}

	@Provides
	@Singleton
	fun providePermissionsRepository() : PermissionsRepository {
		return RxPermissionsRepository()
	}

	@Provides
	@Singleton
	fun provideViewClassResolverStrategy() : ViewClassResolverStrategy {
		return EnumBasedViewClassResolverStrategy()
	}
}