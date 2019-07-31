package com.rodrigmatrix.sigaaufc.ui.view.sigaa.attendance

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry

import com.rodrigmatrix.sigaaufc.R
import kotlinx.android.synthetic.main.attendance_fragment.*

class AttendanceFragment : Fragment() {

    private lateinit var idTurma: String

    companion object {
        fun newInstance() = AttendanceFragment()
    }

    private lateinit var viewModel: AttendanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.attendance_fragment, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(AttendanceViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pie = AnyChart.pie()
        val array = arrayListOf<DataEntry>()
        array.add(ValueDataEntry("Faltas (Horas):", 0))
        array.add(ValueDataEntry("Máximo Permitido (Horas):", 16))
        pie.data(array)
        any_chart_view.setChart(pie)
    }

}
