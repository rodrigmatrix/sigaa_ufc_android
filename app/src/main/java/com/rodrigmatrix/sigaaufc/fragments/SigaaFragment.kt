package com.rodrigmatrix.sigaaufc.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.AddCardActivity
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import com.rodrigmatrix.sigaaufc.persistence.StudentsDatabase
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.fragment_sigaa.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.coroutines.CoroutineContext


class SigaaFragment : Fragment(), CoroutineScope {
    private var job = Job()
    private lateinit var database: StudentsDatabase
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sigaa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val apiSigaa = ApiSigaa()
        database = Room.databaseBuilder(
            fragment_sigaa.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var student = database.studentDao().getStudent()
        var cookie = ""
        println(student.jsession)
        if(student != null && student.jsession != ""){
            progress_login.isVisible = false
            cookie = student.jsession
            login_input.setText(student.login)
            password_input.setText(student.password)
        }
        else{
            login_btn?.isEnabled = false
            launch(handler){
                var pair = apiSigaa.getCookie()
                if(pair.first){
                    login_btn.isEnabled = true
                    progress_login?.isVisible = false
                    student.jsession = pair.second
                    database.studentDao().insertStudent(student)
                    cookie = pair.second
                }
                else{
                    login_btn.isEnabled = false
                    progress_login?.isVisible = false
                    Snackbar.make(fragment_sigaa, "Tempo de conexão expirou", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        login_btn.setOnClickListener {
            if(cookie != "") {
                fragment_sigaa.hideKeyboard()
                if(isValid()){
                    progress_login.isVisible = true
                    login_btn.isEnabled = false
                    var login = login_input.text.toString()
                    var password = password_input.text.toString()
                    launch(handler){
                        var res = apiSigaa.login(cookie, login, password)
                        if(res.first != "Success"){
                            runOnUiThread {
                                Snackbar.make(fragment_sigaa, res.first, Snackbar.LENGTH_LONG).show()
                                progress_login.isVisible = false
                                login_btn.isEnabled = true
                            }
                        }
                        else{
                            res.second.forEach {
                                database.studentDao().insertClass(it)
                            }
                            runOnUiThread {
                                progress_login.isVisible = false
                                login_btn.isEnabled = true
                                saveCredentials(login, password)
                            }
                        }

                    }
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
    private fun saveCredentials(login: String, password: String){
        var student = database.studentDao().getStudent()
        if(student.login == ""){
            runOnUiThread {
                MaterialAlertDialogBuilder(fragment_sigaa.context)
                    .setTitle("Salvar Dados")
                    .setMessage("Deseja salvar seus dados de login?")
                    .setPositiveButton("Sim"){ _, _ ->
                        student.login = login
                        student.password = password
                        database.studentDao().insertStudent(student)
                    }
                    .setNegativeButton("Agora Não"){_, _ ->}
                    .show()
            }
        }
        else if(student.login != login){
            runOnUiThread {
                MaterialAlertDialogBuilder(fragment_sigaa.context)
                    .setTitle("Atualizar Dados")
                    .setMessage("Deseja altualizar seus dados de login salvos?")
                    .setPositiveButton("Sim"){ _, _ ->
                        student.login = login
                        student.password = password
                        database.studentDao().insertStudent(student)
                    }
                    .setNegativeButton("Agora Não"){_, _ ->}
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database.close()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun isValid(): Boolean{
        var isValid = true
        if(login_input.text.toString().isEmpty()){
            login_field.error = "Digite o login"
            isValid = false
        }
        else{
            login_field.error = null
        }
        if(password_input.text.toString().isEmpty()){
            isValid = false
            password_field.error = "Digite a senha"
        }
        else{
            password_field.error = null
        }
        return isValid
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Snackbar.make(fragment_sigaa, throwable.toString(), Snackbar.LENGTH_LONG).show()
        Log.e("Exception", ":$throwable")
    }

}
