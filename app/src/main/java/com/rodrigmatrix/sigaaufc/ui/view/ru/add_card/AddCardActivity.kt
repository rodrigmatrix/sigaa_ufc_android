package com.rodrigmatrix.sigaaufc.ui.view.ru.add_card

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.firebase.PROFILE_BUTTON
import com.rodrigmatrix.sigaaufc.internal.glide.GlideApp
import com.rodrigmatrix.sigaaufc.internal.util.showProfileDialog
import com.rodrigmatrix.sigaaufc.persistence.StudentDao
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_add_card.toolbar
import kotlinx.coroutines.*
import org.kodein.di.generic.instance
import java.lang.Exception

class AddCardActivity : ScopedActivity() {

    private val viewModelFactory: AddCardViewModelFactory by instance()
    private val studentDao: StudentDao by instance()
    private lateinit var viewModel: AddCardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        //findViewById(android.R.id.content).transitionName = "shared_element_container"
        window.sharedElementEnterTransition = MaterialContainerTransform(this).apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform(this).apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
        window.sharedElementEnterTransition = MaterialContainerTransform(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        loadProfilePic()
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        viewModel = ViewModelProvider(this, viewModelFactory)[AddCardViewModel::class.java]
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

    private fun loadProfilePic() = launch{
        try {
            val student = withContext(Dispatchers.IO) {
                studentDao.getStudentAsync()
            }
            val profilePic = student.profilePic
            if(profilePic != "/sigaa/img/no_picture.png"){
                GlideApp.with(this@AddCardActivity)
                    .load("https://si3.ufc.br/$profilePic")
                    .into(profile_pic)
            }
            else{
                profile_pic.setImageResource(R.drawable.avatar_circle_blue)
            }
            profile_pic_card.setOnClickListener {
                events.addEvent(PROFILE_BUTTON + "_RU")
                showProfileDialog(profile_pic_card, student)
            }
        }
        catch(e: Exception){
            e.printStackTrace()
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
