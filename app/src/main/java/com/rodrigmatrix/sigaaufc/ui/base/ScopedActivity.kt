package com.rodrigmatrix.sigaaufc.ui.base

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rodrigmatrix.sigaaufc.data.repository.SigaaPreferences
import com.rodrigmatrix.sigaaufc.firebase.FirebaseEvents
import com.rodrigmatrix.sigaaufc.firebase.RemoteConfig
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

abstract class ScopedActivity: AppCompatActivity(), CoroutineScope, KodeinAware {


    override val kodein by closestKodein()
    val remoteConfig: RemoteConfig by instance()
    val sigaaPreferences: SigaaPreferences by instance()
    val events: FirebaseEvents by instance()

    private val job = SupervisorJob()

    override val coroutineContext = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }

    val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }
}