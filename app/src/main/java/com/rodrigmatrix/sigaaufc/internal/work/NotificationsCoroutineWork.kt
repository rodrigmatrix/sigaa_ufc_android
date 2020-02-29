package com.rodrigmatrix.sigaaufc.internal.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rodrigmatrix.sigaaufc.data.network.SigaaApi
import com.rodrigmatrix.sigaaufc.data.network.SigaaDataSource
import com.rodrigmatrix.sigaaufc.internal.Result.Error
import com.rodrigmatrix.sigaaufc.internal.Result.Success
import com.rodrigmatrix.sigaaufc.internal.notification.sendNotification
import kotlinx.coroutines.runBlocking

class NotificationsCoroutineWork(
    private val context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {

    private var sigaaDataSource = SigaaDataSource(SigaaApi.invoke(context))

    private fun getAllClasses() = runBlocking{
        val result = sigaaDataSource.login("", "")
        if(result is Success){
            println(result.data)
            context.sendNotification("Nova nota em Introducao a arquitetura e organizacao de computadores", "Nota da AP2 disponivel")
            context.sendNotification("Novo arquivo em Introducao a arquitetura e organizacao de computadores", "Arquivo NomeDoArquivo.pdf")
            context.sendNotification("Nova notícia em Introducao a arquitetura e organizacao de computadores", "Titulo: Nome da Noticia \nConteudo: Conteudo da noticia")
        }
        if(result is Error){
            context.sendNotification("erro login", "erro login")
        }
    }

    override suspend fun doWork(): Result {
        //context.sendNotification("teste", "teste")
        getAllClasses()
        return Result.success()
    }


}