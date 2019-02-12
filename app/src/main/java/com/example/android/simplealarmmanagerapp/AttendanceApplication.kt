package com.example.android.simplealarmmanagerapp

import android.app.Application
import com.example.android.simplealarmmanagerapp.fragments.*
import com.example.android.simplealarmmanagerapp.models.daos.AppDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.*

class AttendanceApplication: Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        bind<AppDatabase>() with singleton { AppDatabase.getAppDatabase(this@AttendanceApplication)!! }
        bind<SectionDao>() with singleton { instance<Database>().sectionDao }
        bind<SectionRepository>() with singleton { SectionRepositoryImpl(instance()) }
        bind() from provider { HomeFragmentViewModel(instance()) }
    }
}