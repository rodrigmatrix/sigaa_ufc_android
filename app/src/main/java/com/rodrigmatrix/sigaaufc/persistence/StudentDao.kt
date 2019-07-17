package com.rodrigmatrix.sigaaufc.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.persistence.entity.News
import com.rodrigmatrix.sigaaufc.persistence.entity.Student

@Dao
interface StudentDao {

    @Query("SELECT * FROM students where id = 0")
    fun getStudent(): LiveData<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(student: Student)

    @Query("DELETE FROM students")
    fun deleteStudent()


    @Query("SELECT * FROM classes where isPrevious = 0")
    fun getClasses(): MutableList<StudentClass>

    @Query("SELECT * FROM classes where isPrevious = 1")
    fun getPreviousClasses(): MutableList<StudentClass>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(studentStudentClass: StudentClass)

    @Query("DELETE FROM classes")
    fun deleteClasses()


    @Query("SELECT * FROM historyru")
    fun getHistoryRU(): MutableList<HistoryRU>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRU(historyRU: HistoryRU)

    @Query("DELETE FROM historyru")
    fun deleteHistoryRU()


    @Query("SELECT * FROM news WHERE idTurma LIKE :idTurma")
    fun getNews(idTurma: String): MutableList<News>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: News)

    @Query("DELETE FROM news WHERE idTurma LIKE :idTurma")
    fun deleteNews(idTurma: String)
//
//
//
//    @Query("SELECT * FROM grades WHERE classId LIKE :id")
//    fun getGrades(id: String): MutableList<Grade>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertGrade(grade: Grade)
//
//    @Query("DELETE FROM grades")
//    fun deleteGrades()
//
//    @Query("DELETE FROM grades WHERE classId LIKE :id")
//    fun deleteGradesFromClass(id: String)
//
//
//
//    @Query("SELECT * FROM classPlan WHERE classId LIKE :id")
//    fun getClassPlan(id: String): MutableList<ClassPlan>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertClassPlan(classPlan: ClassPlan)
//
//    @Query("DELETE FROM classPlan")
//    fun deleteClassPlan()
//
//    @Query("DELETE FROM classPlan WHERE classId LIKE :id")
//    fun deleteClassPlanFromClass(id: String)
//
//
//
//    @Query("SELECT * FROM files WHERE classId LIKE :id")
//    fun getFiles(id: String): MutableList<File>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertFile(file: File)
//
//    @Query("DELETE FROM files")
//    fun deleteFiles()
//
//    @Query("DELETE FROM files WHERE classId LIKE :id")
//    fun deleteFilesFromClass(id: String)
//
//
//
//    @Query("SELECT * FROM horasComplementares")
//    fun getHoras(): MutableList<HoraComplementar>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertHora(hora: HoraComplementar)
//
//    @Query("DELETE FROM horasComplementares")
//    fun deleteHoras()
}