package com.rodrigmatrix.sigaaufc.ui.view.sigaa.files

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.net.Uri
import android.system.Os.read
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.persistence.entity.File
import kotlinx.android.synthetic.main.file_row.view.*
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.content.Intent.createChooser
import android.os.*
import android.util.Log
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.internal.TimeoutException
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.selected.ClassActivity
import okhttp3.internal.waitMillis
import java.io.ByteArrayInputStream
import java.net.URLConnection


class FileAdapter(
    private val fileList: MutableList<File>,
    private val cookie: String
): RecyclerView.Adapter<FileViewHolder>() {

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val fileRow = layoutInflater.inflate(R.layout.file_row, parent, false)
        return FileViewHolder(fileRow, cookie)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = fileList[position]
        holder.view.file_name.text = file.name
        holder.view.id_file.text = file.id
        holder.view.request_id_file.text = file.requestId
    }
}

@Suppress("DEPRECATION")
class FileViewHolder(
    val view: View,
    private val cookie: String
): RecyclerView.ViewHolder(view), CoroutineScope{

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var progress: ProgressDialog

    init {
        view.download_button.setOnClickListener {
            checkFilesPermission()
        }
    }

    private fun checkFilesPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(view.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
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

    val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }

    private fun startDownload(){
        launch(handler) {
            downloadFile("4")
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            view.context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1000)
    }

    private suspend fun downloadFile(viewState: String){
        val id = view.id_file.text.toString()
        val requestId = view.request_id_file.text.toString()
        val name = view.file_name.text.toString()
        val formBody = FormBody.Builder()
            .add("formAva", "formAva")
            .add(requestId, requestId)
            .add("id", id)
            .add("javax.faces.ViewState", "j_id$viewState")
            .build()
        val request = Request.Builder()
            .url("https://si3.ufc.br/sigaa/ava/index.jsf")
            .header("Content-Type","multipart/form-data; boundary=--------------------------147775816780075215793937")
            .header("Cookie", "JSESSIONID=$cookie")
            .post(formBody)
            .build()
        progress = ProgressDialog(view.context)
        progress.setMessage(name)
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progress.isIndeterminate = true
        progress.setCancelable(false)
        progress.show()
        var status = true
        withContext(Dispatchers.IO){
            try {
                val response = OkHttpClient
                    .Builder()
                    .readTimeout(90, TimeUnit.SECONDS)
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .build()
                    .newCall(request)
                    .execute()
                if(response.isSuccessful){
                    val content = response.body?.byteStream()!!.readBytes()
                    val directory = Environment.getExternalStorageDirectory().toString() + "/Download"
                    val dir = java.io.File(directory)
                    if(!dir.exists()){
                        dir.mkdirs()
                    }
                    val file = java.io.File("$directory/$name")
                    file.createNewFile()
                    java.io.File(directory, name).writeBytes(content)
                    val res = java.io.File(directory, name).readLines().toString()
                    if(res.contains("html")){
                        status = false
                    }
                    else{
                        Snackbar.make(view,
                            "Arquivo baixado com sucesso. Verifique sua pasta de Downloads", Snackbar.LENGTH_LONG).show()
                        val builder = StrictMode.VmPolicy.Builder()
                        StrictMode.setVmPolicy(builder.build())
                        builder.detectFileUriExposure()
                        val openFile = java.io.File("$directory/$name")
                        val target = Intent(Intent.ACTION_VIEW)
                        target.setDataAndType((Uri.fromFile(openFile)), "application/${getFileType(name)}")
                        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        try {
                            val intent = createChooser(target, "Abrir arquivo ${getFileType(name)}")
                            view.context.startActivity(intent)
                        } catch (e: android.content.ActivityNotFoundException){
                            Snackbar.make(view,
                                "Por favor instale um gerenciador de arquivos para visualizar seus downloads.", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                else{
                    Snackbar.make(view,
                        "Erro ao efetuar download. Feche a disciplina e tente novamente.", Snackbar.LENGTH_LONG).show()
                }
            }
            catch(e: TimeoutException){
                Snackbar.make(view,
                    "Tempo de conexão expirou. Tente novamente", Snackbar.LENGTH_LONG).show()
            }
            progress.dismiss()
        }
        if(!status){
            val directory = Environment.getExternalStorageDirectory().toString() + "/Download"
            val res = java.io.File(directory, name).readLines().toString()
            downloadFile(getViewState(res))
        }
    }



    private fun getViewState(res: String): String{
        return res.split("value=\"j_id")[1].split("\"")[0]
    }
    private fun getFileType(name: String): String{
        return name.split('.').last()
    }
}