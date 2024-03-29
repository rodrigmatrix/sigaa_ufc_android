package com.rodrigmatrix.sigaaufc.ui.view.sigaa.documents

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.documents_fragment.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

@Suppress("DEPRECATION")
class DocumentsFragment : ScopedFragment(R.layout.documents_fragment), KodeinAware {

    private lateinit var viewModel: DocumentsViewModel

    override val kodein by closestKodein()
    private val viewModelFactory: DocumentsViewModelFactory by instance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[DocumentsViewModel::class.java]
        card_history.setOnClickListener {
            checkFilesPermission()
        }
    }

    private fun checkFilesPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                makeRequest()
            }
            else{
                startDownload()
            }
        }
        else{
            startDownload()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1000)
    }

    private fun startDownload(){
        val progress = ProgressDialog(view?.context)
        progress.setTitle("Gerando Histórico")
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progress.isIndeterminate = true
        progress.show()
        launch {
            viewModel.getHistorico()
            runOnUiThread {
                val directory = Environment.getExternalStorageDirectory().toString() + "/Download"
                val res = java.io.File(directory, "historico_sigaa.pdf").readLines().toString()
                if(res.contains("html")){
                    launch {
                        val directory = Environment.getExternalStorageDirectory().toString() + "/Download"
                        val res = java.io.File(directory, "historico_sigaa.pdf").readLines().toString()
                        viewModel.saveViewState(res)
                    }
                    Snackbar.make(view!!,
                        "Erro ao baixar arquivo. Tente efetuar login novamente e tentar novamente", Snackbar.LENGTH_LONG).show()
                }
                else{
                    Snackbar.make(view!!,
                        "Arquivo baixado com sucesso. Verifique sua pasta de Downloads", Snackbar.LENGTH_LONG).show()
                    val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())
                    builder.detectFileUriExposure()
                    val openFile = java.io.File("$directory/historico_sigaa.pdf")
                    val target = Intent(Intent.ACTION_VIEW)
                    target.setDataAndType((Uri.fromFile(openFile)), "application/pdf")
                    target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    try {
                        val intent =
                            Intent.createChooser(target, "Abrir histórico")
                        view?.context?.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException){
                        Snackbar.make(view!!,
                            "Por favor instale um gerenciador de arquivos para visualizar seus downloads.", Snackbar.LENGTH_LONG).show()
                    }
                }
                progress.dismiss()
            }
        }
    }

}
