package com.rodrigmatrix.sigaaufc.ui.view.ru.card_view

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardActivity
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardViewModel
import kotlinx.android.synthetic.main.fragment_restaurante_universiario.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class RestauranteUniversiarioFragment : ScopedFragment(R.layout.fragment_restaurante_universiario), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: RuViewModelFactory by instance()

    private lateinit var viewModel: RuViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[RuViewModel::class.java]
        add_card.setOnClickListener {
            val options =  ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                add_card,
                "shared_element_container"
            )
            val intent = Intent(context, AddCardActivity::class.java)
            this.startActivity(intent, options.toBundle())
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        var hasSavedData = false
        bindData()
        launch(handler) {
            viewModel.getRuCard().observe(this@RestauranteUniversiarioFragment, Observer {
                if(it == null){
                    card_view_ru?.isVisible = false
                    no_card?.isVisible = true
                    return@Observer
                }
                card_holder_text.text = it.nameRU
                credits_text.text = "${it.creditsRU} Créditos"
                if(!hasSavedData){
                    fetchRu(it.cardRU, it.matriculaRU)
                    hasSavedData = true
                }
//                else{
//                    Handler().postDelayed({
//                        fetchRu(it.cardRU, it.matriculaRU)
//                    }, 10000)
//                }
            })
        }
    }
    private fun fetchRu(cardRu: String, matricula: String){
        launch(handler) {
            withContext(Dispatchers.IO) {
                viewModel.fetchRuData(cardRu, matricula)
            }
        }
    }

    private fun bindData(){
        launch(handler) {
            viewModel.historyRu.await().observe(this@RestauranteUniversiarioFragment, Observer {
                if(it == null) return@Observer
                if(it.size != 0){
                    runOnUiThread {
                        card_view_ru?.isVisible = true
                        recyclerView_ru?.layoutManager = LinearLayoutManager(context)
                        recyclerView_ru?.adapter = RestauranteUniversitarioAdapter(it)
                        no_card?.isVisible = false
                    }
                }
            })
        }
    }

}
