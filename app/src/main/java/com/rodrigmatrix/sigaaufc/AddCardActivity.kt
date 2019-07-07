package com.rodrigmatrix.sigaaufc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.room.Room
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import com.rodrigmatrix.sigaaufc.persistence.HistoryRU
import com.rodrigmatrix.sigaaufc.persistence.Student
import com.rodrigmatrix.sigaaufc.persistence.StudentsDatabase
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.coroutines.*
import org.jetbrains.anko.contentView
import kotlin.coroutines.CoroutineContext

class AddCardActivity : AppCompatActivity(), CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    lateinit var database: StudentsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        progress_add_card.isVisible = false
        database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var student = database.studentDao().getStudent()
        card_number_input.setText(student.cardRU)
        matricula_number_input.setText(student.matriculaRU)
        add_card_button.setOnClickListener {
            if(isValid()){
                add_card_activity.hideKeyboard()
                progress_add_card.isVisible = true
                add_card_button.isEnabled = false
                add_credits_button.isEnabled = false
                launch(handler){
                    val api = ApiSigaa()
                    var triple = api.getRU(card_number_input.text.toString(), matricula_number_input.text.toString())
                    println(triple)
                    if(triple.first == "Success"){
                        runOnUiThread {
                            dialogData(triple)
                        }
                    }
                    else{
                        runOnUiThread {
                            Snackbar.make(add_card_activity, triple.first, Snackbar.LENGTH_LONG).show()
                            progress_add_card.isVisible = false
                            add_card_button.isEnabled = true
                            add_credits_button.isEnabled = false
                        }
                    }
                }
            }
        }
        add_credits_button.setOnClickListener {
            Snackbar.make(add_card_activity, "Esse recurso estará disponível em breve", Snackbar.LENGTH_LONG).show()
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
                saveData(triple)
            }
            .setNegativeButton("Não"){_, _ ->}
            .show()
        add_card_button.isEnabled = true
        add_credits_button.isEnabled = true
        progress_add_card.isVisible = false
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        database.close()
    }

    private fun saveData(triple: Triple<String, Pair<String, Int>, MutableList<HistoryRU>>){
        var student = database.studentDao().getStudent()
        student.creditsRU = triple.second.second
        student.nameRU = triple.second.first
        student.cardRU = card_number_input.text.toString()
        student.matriculaRU = matricula_number_input.text.toString()
        database.studentDao().deleteHistoryRU()
        triple.third.forEach {
            database.studentDao().insertRU(it)
        }
        database.studentDao().insertStudent(student)
        println("call finish")
        this.finish()
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
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }
}
