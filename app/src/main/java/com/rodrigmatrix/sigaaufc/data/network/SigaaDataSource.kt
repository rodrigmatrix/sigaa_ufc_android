package com.rodrigmatrix.sigaaufc.data.network

import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.internal.LoginException
import com.rodrigmatrix.sigaaufc.internal.Result
import com.rodrigmatrix.sigaaufc.persistence.entity.File
import com.rodrigmatrix.sigaaufc.persistence.entity.Grade
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_ERROR
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_SUCCESS
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_VINCULO
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import com.rodrigmatrix.sigaaufc.serializer.NewSerializer
import com.rodrigmatrix.sigaaufc.serializer.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import retrofit2.HttpException
import java.lang.Exception

class SigaaDataSource(
    private val sigaaApi: SigaaApi,
    private val sigaaRepository: SigaaRepository
) {

    private val serializer = NewSerializer()

    suspend fun login(
        login: String,
        password: String
    ): Result<LoginStatus> = withContext(Dispatchers.IO){
        return@withContext try {
            val formBody = FormBody.Builder()
                .add("user.login", login)
                .add("user.senha", password)
                .add("entrar", "Entrar")
                .build()
            val request = sigaaApi.login(formBody)
            val loginResponse = serializer.parseLogin(request.string())
            when(loginResponse.loginStatus){
                LOGIN_SUCCESS -> Result.Success(loginResponse)
                LOGIN_VINCULO -> Result.Success(loginResponse)
                else -> Result.Error(LoginException(loginResponse.loginMessage))
            }
        }
        catch(e: HttpException){
            Result.Error(e)
        }
        catch(e: Exception){
            Result.Error(e)
        }
    }

    suspend fun getClasses(): Result<List<StudentClass>> = withContext(Dispatchers.IO){
        return@withContext try {
            val request = sigaaApi.getCurrentClasses()
            val result = request.string()
            sigaaRepository.saveViewState(result)
            Result.Success(serializer.parseClasses(result))
        }
        catch(e: HttpException){
            Result.Error(e)
        }
        catch(e: Exception){
            Result.Error(e)
        }
    }

    suspend fun redirectHome(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = sigaaApi.openHomePage()
            Result.Success(request.string())
        }
        catch(e: HttpException){
            Result.Error(e)
        }
        catch(e: Exception){
            Result.Error(e)
        }
    }


    suspend fun setCurrentClass(studentClass: StudentClass): Result<String> = withContext(Dispatchers.IO){
        return@withContext try {
            val id = studentClass.id
            val formBody = FormBody.Builder()
                .add("idTurma", studentClass.turmaId)
                .add("form_acessarTurmaVirtual$id", "form_acessarTurmaVirtual$id")
                .add("form_acessarTurmaVirtual$id:turmaVirtual$id", "form_acessarTurmaVirtual$id:turmaVirtual$id")
                .add("javax.faces.ViewState", sigaaRepository.getViewStateId())
                .build()
            val request = sigaaApi.setCurrentClass(formBody)
            val response = request.string()
            sigaaRepository.saveViewState(response)
            Result.Success(response)
        }
        catch(e: HttpException){
            Result.Error(e)
        }
        catch(e: Exception){
            Result.Error(e)
        }
    }

    suspend fun getGrades(res: String, studentClass: StudentClass): Result<List<Grade>> = withContext(Dispatchers.IO){
        return@withContext try {
            val requestId = serializer.parseGradesRequestId(res)
            val formBody = FormBody.Builder()
                .add("formMenu", "formMenu")
                .add(requestId, requestId)
                .add("javax.faces.ViewState", sigaaRepository.getViewStateId())
                .build()
            val request = sigaaApi.getGrades(formBody)
            val response = request.string()
            sigaaRepository.saveViewState(response)
            val gradesList = serializer.parseGrades(response, studentClass.turmaId)
            Result.Success(gradesList)
        }
        catch(e: HttpException){
            Result.Error(e)
        }
        catch(e: Exception){
            Result.Error(e)
        }
    }



    suspend fun setVinculo(vinculo: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val map = hashMapOf<String, String>()
            map["dispatch"] = "escolher"
            map["vinculo"] = vinculo
            val request = sigaaApi.setVinculo(map)
            sigaaRepository.saveViewState(request.string())
            Result.Success(request.string())
        }
        catch(e: HttpException){
            Result.Error(e)
        }
        catch(e: Exception){
            Result.Error(e)
        }
    }


}