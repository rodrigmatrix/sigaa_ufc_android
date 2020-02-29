package com.rodrigmatrix.sigaaufc.ui.view.sigaa.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.services.ProductPurchasedListListener
import com.igorronner.irinterstitial.services.ProductPurchasedListener
import com.igorronner.irinterstitial.services.ProductsListListener
import com.igorronner.irinterstitial.services.PurchaseService
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.Vinculo
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardViewModel
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.main.SigaaActivity
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.runOnUiThread
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
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadCookie()
        launch(handler) {
            viewModel.getStudent().observe(viewLifecycleOwner, Observer { student ->
                if (student == null) {
                    return@Observer
                }
                println(student)
                runOnUiThread {
                    login_input.setText(student.login)
                    password_input.setText(student.password)
                }
            })
        }

        login_btn?.setOnClickListener {
            fragment_login.hideKeyboard()
            if (isValid()) {
                progress_login?.isVisible = true
                login_btn?.isEnabled = false
                val login = login_input.text.toString()
                val password = password_input.text.toString()
                launch(handler) {
                    val cookie = viewModel.getStudentAsync().jsession
                    val loginResponse = viewModel.login(cookie, login, password)
                    handleLogin(login, password, loginResponse)
                }
            }
            fragment_login.hideKeyboard()
            super.onViewCreated(view, savedInstanceState)
        }
    }

    private suspend fun handleLogin(login: String, password: String, res: String){
        when (res) {
            "Success" -> {
                runOnUiThread {
                    progress_login?.isVisible = false
                }
                saveCredentials(login, password, false)
            }
            "Vinculo" -> {
                saveCredentials(login, password, true)
                setVinculo(viewModel.getVinculos())
            }
            else -> runOnUiThread {
                Snackbar.make(fragment_login, res, Snackbar.LENGTH_LONG).show()
                login_btn?.isEnabled = true
                progress_login?.isVisible = false

            }
        }
    }

    private fun setVinculo(vinculos: MutableList<Vinculo>){
        val arrayList = arrayListOf<String>()
        vinculos.forEach {
            arrayList.add(it.content)
            println(it)
        }
        arrayList.forEach {
            println(it)
        }
        val array = arrayOfNulls<String>(arrayList.size)
        arrayList.toArray(array)
        MaterialAlertDialogBuilder(context)
            .setTitle("Escolher Vínculo")
            .setSingleChoiceItems(array, -1) { dialogInterface, i ->
                println(array[i])
                println(vinculos[i].id)
                setVinculo(vinculos[i].id)
                dialogInterface.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun setVinculo(vinculo: String){
        launch {
            viewModel.setVinculo(vinculo)
            runOnUiThread {
                progress_login?.isVisible = false
                openSigaa()
            }
        }
    }


    private fun loadCookie(){
        launch(handler) {
            runOnUiThread {
                progress_login?.isVisible = true
                login_btn?.isEnabled = false
            }
            if(!viewModel.getCookie()){
                runOnUiThread {
                    Snackbar.make(fragment_login, "Erro ao carregar cookie", Snackbar.LENGTH_LONG)
                        .setAction("Recarregar") {
                            loadCookie()
                        }.show()
                }
            }
            runOnUiThread {
                progress_login?.isVisible = false
                login_btn?.isEnabled = true
            }
        }
    }

    private fun saveCredentials(login: String, password: String, isVinculo: Boolean){
        launch(handler) {
            val student = viewModel.getStudentAsync()
            if(student?.login == ""){
                saveDialog(true, login, password, isVinculo)
            }
            else if((student?.login != login) || (student?.password != password)){
                saveDialog(false, login, password, isVinculo)
            }
            else if(!isVinculo){
                openSigaa()
            }
        }

    }

    private fun openSigaa(){
        runOnUiThread {
            login_btn?.isEnabled = true
            val intent = Intent(fragment_login.context, SigaaActivity::class.java)
            this.startActivity(intent)
        }
    }

    private fun saveDialog(newLogin: Boolean, login: String, password: String, isVinculo: Boolean){
        if(newLogin){
            runOnUiThread {
                MaterialAlertDialogBuilder(fragment_login.context)
                    .setTitle("Salvar Dados")
                    .setMessage("Deseja salvar seus dados de login?")
                    .setPositiveButton("Sim"){ _, _ ->
                        launch(handler) {
                            viewModel.saveLogin(login, password)
                            if(!isVinculo){
                                openSigaa()
                            }
                        }
                    }
                    .setNegativeButton("Agora Não"){_, _ ->
                        if(!isVinculo){
                            openSigaa()
                        }
                    }
                    .setOnCancelListener {
                        if(!isVinculo){
                            openSigaa()
                        }
                    }
                    .show()
            }
        }
        else {
            runOnUiThread {
                MaterialAlertDialogBuilder(fragment_login.context)
                    .setTitle("Atualizar Dados")
                    .setMessage("Deseja altualizar seus dados de login salvos?")
                    .setPositiveButton("Sim"){ _, _ ->
                        launch(handler) {
                            viewModel.saveLogin(login, password)
                            if(!isVinculo){
                                openSigaa()
                            }
                        }
                    }
                    .setNegativeButton("Agora Não"){_, _ ->
                        if(!isVinculo){
                            openSigaa()
                        }
                    }
                    .setOnCancelListener {
                        if(!isVinculo){
                            openSigaa()
                        }
                    }
                    .show()
            }
        }
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

}
