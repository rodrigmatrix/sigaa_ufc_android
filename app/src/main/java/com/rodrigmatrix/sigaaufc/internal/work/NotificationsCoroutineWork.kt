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
import com.rodrigmatrix.sigaaufc.internal.util.getUncommonElements
import com.rodrigmatrix.sigaaufc.internal.util.getUncommonGrades
import com.rodrigmatrix.sigaaufc.persistence.StudentDao
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_REDIRECT
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_VINCULO
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import com.rodrigmatrix.sigaaufc.serializer.NewSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
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

    private fun login(): Result = runBlocking(Dispatchers.IO) {
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

    private fun handleVinculo(): Result = runBlocking(Dispatchers.IO) {
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

    private fun handleClasses(): Result = runBlocking(Dispatchers.IO) {
        sigaaDataSource.redirectHome()
        val response = sigaaDataSource.getClasses()
        val studentClasses = studentDao.getClasses()
        if(response is Success){
            studentClasses.forEach {
                loadClassAndCheckForNotifications(it)
                it.synced = true
                studentDao.upsertClass(it)
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

    private fun loadClassAndCheckForNotifications(studentClass: StudentClass): Result = runBlocking(Dispatchers.IO) {
        val result = sigaaDataSource.setCurrentClass(studentClass)
        if(result is Success){
            checkForFiles(result.data, studentClass)
            checkForGrades(result.data, studentClass)
            return@runBlocking Result.success()
        }
        if(result is Error){
            result.exception.printStackTrace()
            return@runBlocking Result.failure()
        }
        return@runBlocking Result.success()
    }

    private fun checkForGrades(res: String, studentClass: StudentClass) = runBlocking(Dispatchers.IO) {
        val response = sigaaDataSource.getGrades(res, studentClass)
        if(response is Success){
            val grades = response.data
            val cashedGrades = studentDao.getGradesAsync(studentClass.turmaId)
            if(!studentClass.synced){
                grades.forEach {
                    studentDao.upsertGrade(it)
                }
                return@runBlocking
            }
            val newGrades = grades.getUncommonGrades(cashedGrades)
            newGrades.forEach {
                val className = studentClass.name.getClassNameWithoutCode()
                context.sendGradeNotification(
                    context.getString(R.string.grade_notification_title, className),
                    context.getString(R.string.grade_notification_body, it.name)
                )
            }
            grades.forEach {
                studentDao.upsertGrade(it)
            }
        }
        if(response is Error){
            response.exception.printStackTrace()
        }
    }

    private fun checkForFiles(res: String, studentClass: StudentClass) = runBlocking(Dispatchers.IO) {
        val files = serializer.parseFiles(res, studentClass.turmaId)
        val cashedFiles = studentDao.getFilesAsync(studentClass.turmaId)
        if(!studentClass.synced){
            files.forEach {
                studentDao.upsertFile(it)
            }
            return@runBlocking
        }
        val newFiles = files.getUncommonElements(cashedFiles)
        newFiles.forEach {
            if(it.name != "null"){
                studentDao.upsertFile(it)
                val className = studentClass.name.getClassNameWithoutCode()
                context.sendDownloadNotification(
                    context.getString(R.string.file_notification_title, className),
                    context.getString(R.string.file_notification_body, it.name)
                )
            }
        }
    }

    override suspend fun doWork(): Result {
        return login()
    }

}