package com.rodrigmatrix.sigaaufc.ui.view.ru.card_view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardActivity
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.adapters.RestauranteUniversitarioAdapter
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_restaurante_universiario.*
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class RestauranteUniversiarioFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: RuViewModelFactory by instance()

    private lateinit var viewModel: RuViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(RuViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        card_view_ru.isVisible = false
        ru_refresh?.isVisible = false
        no_card?.isVisible = true
        ru_refresh?.isRefreshing = false
        launch {
            viewModel.student.await().observe(this@RestauranteUniversiarioFragment, Observer { student ->
                if(student == null) return@Observer
                if(student.cardRU != ""){
                    card_holder_text?.text = student.nameRU
                    credits_text?.text = "${student.creditsRU} créditos"
                    bindData()
                }
            })
        }
    }

    private fun bindData(){
        launch {
            viewModel.historyRu.await().observe(this@RestauranteUniversiarioFragment, Observer {
                card_view_ru?.isVisible = true
                recyclerView_ru?.layoutManager = LinearLayoutManager(context)
                recyclerView_ru?.adapter = RestauranteUniversitarioAdapter(it)
                ru_refresh?.isVisible = true
                no_card?.isVisible = false
                ru_refresh?.isRefreshing = false
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ru_refresh.setColorSchemeResources(R.color.colorPrimary)
        ru_refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        add_card.setOnClickListener {
            val intent = Intent(context, AddCardActivity::class.java)
            this.startActivity(intent)
        }
        ru_refresh?.setOnRefreshListener {
            ru_refresh?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurante_universiario, container, false)
    }
}
