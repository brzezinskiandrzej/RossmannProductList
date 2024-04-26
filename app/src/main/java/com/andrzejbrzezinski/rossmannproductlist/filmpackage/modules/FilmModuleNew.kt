package com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.connection.LoadFirebaseData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.connection.LoadOfflineData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.workers.UploadFilmWorker
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.workers.UploadFilmWorkerFactory
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginStateService
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Singleton
import dagger.MapKey
import dagger.assisted.AssistedFactory
import kotlin.reflect.KClass
@Module
@InstallIn(SingletonComponent::class)

object FilmModuleNew {
    @Named("FirebaseData")
    @Singleton
    @Provides
    fun provideLoadFirebaseData(loginStateService: LoginStateService): ILoadData {
        return LoadFirebaseData(FirebaseDatabase.getInstance(),loginStateService)
    }

    @Named("OfflineData")
    @Singleton
    @Provides
    fun provideTestRepository(loginStateService: LoginStateService): ILoadData {
        return LoadOfflineData(loginStateService)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
}

@Module
@InstallIn(SingletonComponent::class)
class LoginModule {

    @Singleton
    @Provides
    fun provideLoginStateService(sharedPreferences: SharedPreferences): LoginStateService {
        return LoginStateService(sharedPreferences)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        setDefaultValuesIfNotExists(sharedPreferences)
        return sharedPreferences
    }

    private fun setDefaultValuesIfNotExists(sharedPreferences: SharedPreferences) {
        with(sharedPreferences.edit()) {
            if (!sharedPreferences.contains(PreferencesKeys.KEY_USERNAME)) {
                putString(PreferencesKeys.KEY_USERNAME, PreferencesKeys.DEFAULT_USERNAME)
            }
            if (!sharedPreferences.contains(PreferencesKeys.KEY_IS_LOGGED_IN)) {
                putBoolean(PreferencesKeys.KEY_IS_LOGGED_IN, PreferencesKeys.DEFAULT_IS_LOGGED_IN)
            }
            apply()
        }
    }
}
object PreferencesKeys {
    const val KEY_USERNAME = "username"
    const val KEY_IS_LOGGED_IN = "isLoggedIn"
    const val DEFAULT_USERNAME = ""
    const val DEFAULT_IS_LOGGED_IN = false
}

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(UploadFilmWorker::class)
    abstract fun bindUploadFilmWorker(factory: UploadFilmWorkerFactory): ChildWorkerFactory
}
@MapKey
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}


