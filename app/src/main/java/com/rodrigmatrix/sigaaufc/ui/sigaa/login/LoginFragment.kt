package com.rodrigmatrix.sigaaufc.ui.sigaa.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.StudentDatabase
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class LoginFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: LoginViewModelFactory by instance()

    private lateinit var viewModel: LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(LoginViewModel::class.java)
        launch {
            val loginResponse = viewModel.login("", "", "")
            loginResponse.observe(this@LoginFragment, Observer {
                if(it == null) return@Observer
                println(it)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        val student = database.studentDao().getStudent()
//        var cookie = ""
//        println(student.jsession)
//        if(student != null && student.jsession != ""){
//            progress_login.isVisible = false
//            cookie = student.jsession
//            login_input.setText(student.login)
//            password_input.setText(student.password)
//        }
//        else{
//            login_btn?.isEnabled = false
//            launch(handler){
//                val pair = apiSigaa.getCookie()
//                if(pair.first){
//                    login_btn.isEnabled = true
//                    progress_login?.isVisible = false
//                    student.jsession = pair.second
//                    database.studentDao().insertStudent(student)
//                    cookie = pair.second
//                }
//                else{
//                    login_btn.isEnabled = false
//                    progress_login?.isVisible = false
//                    Snackbar.make(fragment_login, "Tempo de conexão expirou", Snackbar.LENGTH_LONG).show()
//                }
//            }
//        }
        login_btn.setOnClickListener {
            fragment_login.hideKeyboard()
            if(isValid()){
                progress_login.isVisible = true
                login_btn.isEnabled = false
                val login = login_input.text.toString()
                val password = password_input.text.toString()
//                launch(handler){
//
//                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
//    private fun saveCredentials(login: String, password: String){
//        val student = database.studentDao().getStudent()
//        if(student.login == ""){
//            runOnUiThread {
//                MaterialAlertDialogBuilder(fragment_login.context)
//                    .setTitle("Salvar Dados")
//                    .setMessage("Deseja salvar seus dados de login?")
//                    .setPositiveButton("Sim"){ _, _ ->
//                        student.login = login
//                        student.password = password
//                        database.studentDao().insertStudent(student)
//                    }
//                    .setNegativeButton("Agora Não"){_, _ ->}
//                    .show()
//            }
//        }
//        else if(student.login != login){
//            runOnUiThread {
//                MaterialAlertDialogBuilder(fragment_login.context)
//                    .setTitle("Atualizar Dados")
//                    .setMessage("Deseja altualizar seus dados de login salvos?")
//                    .setPositiveButton("Sim"){ _, _ ->
//                        student.login = login
//                        student.password = password
//                        database.studentDao().insertStudent(student)
//                    }
//                    .setNegativeButton("Agora Não"){_, _ ->}
//                    .show()
//            }
//        }
//    }

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
        Snackbar.make(fragment_login, throwable.toString(), Snackbar.LENGTH_LONG).show()
        login_btn?.isEnabled = true
        progress_login?.isVisible = false
        Log.e("Exception", ":$throwable")
    }

}
