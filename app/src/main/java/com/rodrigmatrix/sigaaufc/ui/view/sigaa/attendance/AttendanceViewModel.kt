package com.rodrigmatrix.sigaaufc.ui.view.sigaa.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.Attendance
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass

class AttendanceViewModel(
    private val sigaaRepository: SigaaRepository
) : ViewModel() {


    suspend fun getAttendance(idTurma: String): LiveData<out StudentClass>{
        return sigaaRepository.getClass(idTurma)
    }
}
