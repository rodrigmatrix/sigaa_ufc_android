package com.rodrigmatrix.sigaaufc.ui.view.ru.add_card

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.fragment_restaurante_universiario.*
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class AddCardActivity : ScopedActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: AddCardViewModelFactory by instance()

    private lateinit var viewModel: AddCardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(AddCardViewModel::class.java)
        bindData()
        progress_add_card.isVisible = false
        add_card_button.setOnClickListener {
            if(isValid()) {
                add_card_activity.hideKeyboard()
                progress_add_card.isVisible = true
                add_card_button.isEnabled = false
                add_credits_button.isEnabled = false
                val numeroCartao = card_number_input.text.toString()
                val matricula = matricula_number_input.text.toString()
                launch(handler) {
                    addCard(numeroCartao, matricula)
                }
            }
        }
        add_credits_button.setOnClickListener {
            Snackbar.make(add_card_activity, "Esse recurso estará disponível em breve", Snackbar.LENGTH_LONG).show()
        }

    }
    private suspend fun addCard(numeroCartao: String, matricula: String){
        val res = viewModel.fetchRu(numeroCartao, matricula)
        if(res == "Success"){
            finish()
        }
        else{
            runOnUiThread {
                Snackbar.make(add_card_activity, res, Snackbar.LENGTH_LONG).show()
                progress_add_card.isVisible = false
                add_card_button.isEnabled = true
                add_credits_button.isEnabled = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(){
        launch(handler) {
            viewModel.getRuCard().observe(this@AddCardActivity, Observer {
                if(it == null) return@Observer
                card_number_input.setText(it.cardRU)
                matricula_number_input.setText(it.matriculaRU)
            })
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


    private fun dialogData(triple: Triple<String, Pair<String, Int>, MutableList<HistoryRU>>){
        MaterialAlertDialogBuilder(add_card_activity.context)
            .setTitle("Confirme o cartão")
            .setMessage("Este cartão pertence à ${triple.second.first}?")
            .setPositiveButton("Sim"){ _, _ ->
                add_card_activity.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                //saveData(triple)
            }
            .setNegativeButton("Não"){_, _ ->}
            .show()
        add_card_button.isEnabled = true
        add_credits_button.isEnabled = true
        progress_add_card.isVisible = false
    }

    private fun isValid(): Boolean{
        var isValid = true
        if(card_number_input.text.toString().isEmpty()){
            card_number_field.error = "Digite o número do cartão"
            isValid = false
        }
        else{
            card_number_field.error = null
        }
        if(matricula_number_input.text.toString().isEmpty()){
            isValid = false
            matricula_number_field.error = "Digite a matrícula"
        }
        else{
            matricula_number_field.error = null
        }
        return isValid
    }
}
