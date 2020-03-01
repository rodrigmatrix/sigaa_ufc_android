package com.rodrigmatrix.sigaaufc.ui.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import kotlin.coroutines.CoroutineContext

abstract class ScopedActivity: AppCompatActivity(), CoroutineScope, KodeinAware {


    override val kodein by closestKodein()

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