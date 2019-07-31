package com.rodrigmatrix.sigaaufc

import android.app.Application
import androidx.preference.PreferenceManager
import com.rodrigmatrix.sigaaufc.data.SigaaApi
import com.rodrigmatrix.sigaaufc.data.network.*
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepositoryImpl
import com.rodrigmatrix.sigaaufc.persistence.StudentDatabase
import com.rodrigmatrix.sigaaufc.serializer.Serializer
import com.rodrigmatrix.sigaaufc.ui.view.main.MainActivityViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.ru.card_view.RuViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.attendance.AttendanceViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.selected.ClassViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.view.ClassesViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades.GradesViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.login.LoginViewModelFactory
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import java.util.concurrent.TimeUnit

class SigaaApplication: Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@SigaaApplication))
        bind() from singleton { StudentDatabase(instance()) }
        bind() from singleton { instance<StudentDatabase>().studentDao() }
        bind<ConnectivityInterceptor>() with singleton {
            ConnectivityInterceptorImpl(context = instance())
        }
        bind<OkHttpClient.Builder>() with singleton {
            OkHttpClient
                .Builder()
                .readTimeout(25, TimeUnit.SECONDS)
                .connectTimeout(25, TimeUnit.SECONDS)
        }
        bind() from singleton {
            SigaaApi(httpClient = instance(),
                sigaaSerializer = instance(),
                studentDatabase = instance(),
                connectivityInterceptor = instance())
        }
        bind<SigaaNetworkDataSource>() with singleton {
            SigaaNetworkDataSourceImpl(sigaaApi = instance())
        }
        bind<SigaaRepository>() with singleton {
            SigaaRepositoryImpl(sigaaNetworkDataSource = instance(), studentDao = instance())
        }
        bind() from singleton {
            Serializer()
        }
        bind() from provider {
            LoginViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            RuViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            AddCardViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            MainActivityViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            ClassesViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            GradesViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            ClassViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            AttendanceViewModelFactory(sigaaRepository = instance())
        }

    }

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}