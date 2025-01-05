package com.prafull.roadmapcreator

import android.app.Application
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.roadmapcreator.app.AppViewModel
import com.prafull.roadmapcreator.app.data.RoadmapCreatorDB
import com.prafull.roadmapcreator.app.history.HistoryRepository
import com.prafull.roadmapcreator.app.history.HistoryRepositoryImpl
import com.prafull.roadmapcreator.app.history.HistoryViewModel
import com.prafull.roadmapcreator.utils.ApiKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(module)
        }
    }
}

val module = module {

    single<RoadmapCreatorDB> {
        Room.databaseBuilder(
            androidContext(),
            RoadmapCreatorDB::class.java,
            "roadmaps_db"
        ).build()
    }
    single<ApiKey> {
        runBlocking {
            ApiKey(
                FirebaseFirestore.getInstance().collection("apiKey").document("apiKey").get()
                    .await()
                    .getString("apiKey")
                    .toString()
            )
        }
    }
    single {
        get<RoadmapCreatorDB>().roadmapDao()
    }
    viewModel {
        AppViewModel(get(), get())
    }
    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }
    viewModel {
        HistoryViewModel(get())
    }
}
