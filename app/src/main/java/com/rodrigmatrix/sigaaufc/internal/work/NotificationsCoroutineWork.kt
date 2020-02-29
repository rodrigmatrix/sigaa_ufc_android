package com.rodrigmatrix.sigaaufc.internal.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.data.network.SigaaApi
import com.rodrigmatrix.sigaaufc.data.network.SigaaDataSource
import com.rodrigmatrix.sigaaufc.data.repository.SigaaPreferences
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.internal.Result.Error
import com.rodrigmatrix.sigaaufc.internal.Result.Success
import com.rodrigmatrix.sigaaufc.internal.notification.sendDownloadNotification
import com.rodrigmatrix.sigaaufc.internal.notification.sendGradeNotification
import com.rodrigmatrix.sigaaufc.internal.notification.sendNewsNotification
import com.rodrigmatrix.sigaaufc.internal.notification.sendNotification
import com.rodrigmatrix.sigaaufc.internal.util.getClassNameWithoutCode
import com.rodrigmatrix.sigaaufc.persistence.StudentDao
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_REDIRECT
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_VINCULO
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import com.rodrigmatrix.sigaaufc.serializer.NewSerializer
import kotlinx.coroutines.runBlocking
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class NotificationsCoroutineWork(
    private val context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params), KodeinAware {

    override val kodein by closestKodein(context)

    private val sigaaDataSource: SigaaDataSource by instance()
    private val sigaaRepository: SigaaRepository by instance()
    private val sigaaPreferences: SigaaPreferences by instance()
    private val studentDao: StudentDao by instance()
    private val serializer = NewSerializer()

    private fun login(): Result = runBlocking {
        val student = sigaaRepository.getStudentAsync() ?: return@runBlocking Result.success()
        if(student.login == "") return@runBlocking Result.success()
        val result = sigaaDataSource.login(student.login, student.password)
        if(result is Success){
            return@runBlocking when(result.data.loginStatus){
                LOGIN_VINCULO -> handleVinculo()
                else -> handleClasses()
            }
        }
        if(result is Error){
            // TODO handle error
            //context.sendNotification("erro login", "erro login")
            return@runBlocking Result.success()
        }
        return@runBlocking Result.success()
    }

    private fun handleVinculo(): Result = runBlocking {
        val response = sigaaDataSource.setVinculo(sigaaPreferences.getLastVinculo() ?: "1")
        if(response is Success){
            return@runBlocking handleClasses()
        }
        if(response is Error){
            response.exception.printStackTrace()
            return@runBlocking Result.retry()
        }
        return@runBlocking Result.success()
    }

    private fun handleClasses(): Result = runBlocking {
        sigaaDataSource.redirectHome()
        val response = sigaaDataSource.getClasses()
        if(response is Success){
            response.data.forEach {
                loadClassAndCheckForNotifications(it)
                sigaaDataSource.getClasses()
            }
            return@runBlocking Result.success()
        }
        if(response is Error){
            response.exception.printStackTrace()
            return@runBlocking Result.failure()
        }
        return@runBlocking Result.success()
    }

    private fun loadClassAndCheckForNotifications(studentClass: StudentClass): Result = runBlocking {
        val result = sigaaDataSource.setCurrentClass(studentClass)
        if(result is Success){
            checkForFiles(result.data, studentClass)
            return@runBlocking Result.success()
        }
        if(result is Error){
            result.exception.printStackTrace()
            return@runBlocking Result.failure()
        }
        return@runBlocking Result.success()
    }

    private fun checkForFiles(res: String, studentClass: StudentClass) = runBlocking{
        val files = serializer.parseFiles(res, studentClass.turmaId)
        val cashedFiles = studentDao.getFilesAsync(studentClass.turmaId)
        if(cashedFiles.isNotEmpty()){
            if(cashedFiles.size < files.size){
                files.takeLast(files.size - cashedFiles.size).forEach {
                    val className = studentClass.name.getClassNameWithoutCode()
                    context.sendDownloadNotification(
                        context.getString(R.string.file_notification_title, className),
                        context.getString(R.string.file_notification_body, it.name)
                    )
                }
            }
        }
        studentDao.upsertFiles(files)
    }

    override suspend fun doWork(): Result {
        return login()
    }

}