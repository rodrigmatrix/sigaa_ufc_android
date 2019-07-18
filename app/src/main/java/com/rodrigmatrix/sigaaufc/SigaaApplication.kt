package com.rodrigmatrix.sigaaufc

import android.app.Application
import androidx.preference.PreferenceManager
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.data.SigaaApi
import com.rodrigmatrix.sigaaufc.data.network.*
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepositoryImpl
import com.rodrigmatrix.sigaaufc.persistence.StudentDatabase
import com.rodrigmatrix.sigaaufc.serializer.Serializer
import com.rodrigmatrix.sigaaufc.ui.sigaa.login.LoginViewModelFactory
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class SigaaApplication: Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@SigaaApplication))
        bind() from singleton { StudentDatabase(instance()) }
        bind() from singleton { instance<StudentDatabase>().studentDao() }
        bind<ConnectivityInterceptor>() with singleton {
            ConnectivityInterceptorImpl(context = instance())
        }
        bind<OkHttpClient>() with singleton {
            OkHttpClient()
        }
        bind<SigaaRepository>() with singleton {
            SigaaRepositoryImpl(sigaaNetworkDataSource = instance(), studentDao = instance())
        }
        bind() from singleton {
            Serializer()
        }
        bind() from singleton {
            SigaaApi(httpClient = instance(), sigaaSerializer = instance(), studentDatabase = instance())
        }
        bind<SigaaNetworkDataSource>() with singleton {
            SigaaNetworkDataSourceImpl(sigaaApi = instance(), sigaaSerializer = instance())
        }
        bind() from provider {
            LoginViewModelFactory(sigaaRepository = instance())
        }
    }

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}