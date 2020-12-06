package com.rodrigmatrix.sigaaufc.ui.view.sigaa.attendance

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_attendance.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AttendanceFragment : ScopedFragment(R.layout.fragment_attendance), KodeinAware {


    override val kodein by closestKodein()
    private val viewModelFactory: AttendanceViewModelFactory by instance()
    private lateinit var idTurma: String
    private var isPrevious = false

    private lateinit var viewModel: AttendanceViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[AttendanceViewModel::class.java]
        idTurma = arguments?.getString("idTurma")!!
        isPrevious = arguments?.getBoolean("isPrevious", false)!!
        observeAttendance()
    }

    @SuppressLint("SetTextI18n")
    private fun observeAttendance(){
        if(isPrevious){
            launch {
                viewModel.getPreviousAttendance(idTurma).observe(viewLifecycleOwner, Observer {
                    if(it == null) return@Observer
                    if(it.attendance != 0){
                        attendance_view.isVisible = true
                        total_hour_text.text = "Horas: ${it.attendance}"
                        total_class_text.text = "Aulas: ${it.attendance/2}"

                        missed_hour_text.text = "Horas: ${it.missed}"
                        missed_class_text.text = "Aulas: ${it.missed/2}"
                    }
                })
            }
        }
        else{
            launch {
                viewModel.getAttendance(idTurma).observe(viewLifecycleOwner, Observer {
                    if(it == null) return@Observer
                    if(it.attendance != 0){
                        attendance_view.isVisible = true
                        total_hour_text.text = "Horas: ${it.attendance}"
                        total_class_text.text = "Aulas: ${it.attendance/2}"

                        missed_hour_text.text = "Horas: ${it.missed}"
                        missed_class_text.text = "Aulas: ${it.missed/2}"
                    }
                })
            }
        }


    }

}
