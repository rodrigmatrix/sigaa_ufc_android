package com.rodrigmatrix.sigaaufc

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
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
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.fragment.ClassesViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.documents.DocumentsViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.files.FilesViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades.GradesViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.ira.IraViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.login.LoginViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.fragment.NewsViewModelFactory
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.view.NewsContentViewModelFactory
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
        bind() from provider {
            IraViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            NewsViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            NewsContentViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            FilesViewModelFactory(sigaaRepository = instance())
        }
        bind() from provider {
            DocumentsViewModelFactory(sigaaRepository = instance())
        }
    }

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        setTheme(preferences.getString("THEME", "SYSTEM_DEFAULT"))
        fcmId()
    }

    private fun fcmId(){
        val TAG = "fcm"
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                Log.d(TAG, token)
            })
    }

    private fun setTheme(theme: String?){
        when(theme){
            "LIGHT" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "DARK" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "BATTERY_SAVER" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            "SYSTEM_DEFAULT" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }



}