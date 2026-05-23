package tech.sabitani.core.analytics.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.core.analytics.AnalyticsHelper
import tech.sabitani.core.analytics.CrashlyticsAnalyticsHelper
import tech.sabitani.core.analytics.consent.AnalyticsConsent
import tech.sabitani.core.analytics.consent.DataStoreAnalyticsConsent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindsAnalyticsHelper(impl: CrashlyticsAnalyticsHelper): AnalyticsHelper

    @Binds
    @Singleton
    abstract fun bindsAnalyticsConsent(impl: DataStoreAnalyticsConsent): AnalyticsConsent
}
