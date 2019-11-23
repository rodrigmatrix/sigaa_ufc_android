package com.rodrigmatrix.sigaaufc.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rodrigmatrix.sigaaufc.persistence.entity.*

@Dao
interface StudentDao {

    @Query("SELECT * FROM students where id = 0")
    fun getStudent(): LiveData<Student>

    @Query("SELECT * FROM students where id = 0")
    fun getStudentAsync(): Student

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertStudent(student: Student)

    @Query("DELETE FROM students")
    fun deleteStudent()

    @Query("SELECT * FROM view_faces where id = 0")
    fun getViewState(): LiveData<JavaxFaces>

    @Query("SELECT * FROM view_faces where id = 0")
    fun getViewStateAsync(): JavaxFaces

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertViewState(viewState: JavaxFaces)

    @Query("DELETE FROM view_faces")
    fun deleteViewState()

    @Query("SELECT * FROM ru_card where id = 0")
    fun getRuCard(): LiveData<RuCard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertRuCard(ruCard: RuCard)

    @Query("DELETE FROM ru_card")
    fun deleteRuCard()


    @Query("SELECT * FROM classes WHERE isPrevious = 0")
    fun getClasses(): MutableList<StudentClass>

    @Query("SELECT * FROM classes WHERE (isPrevious = 0) AND (turmaId = :idTurma)")
    fun getClassWithIdTurmaAsync(idTurma: String): StudentClass

    @Query("SELECT * FROM classes WHERE (isPrevious = 0) AND (turmaId = :idTurma)")
    fun getClassWithIdTurma(idTurma: String): LiveData<StudentClass>

    @Query("SELECT * FROM classes WHERE (isPrevious = 1) AND (turmaId = :idTurma)")
    fun getPreviousClassWithIdTurmaAsync(idTurma: String): StudentClass

    @Query("SELECT * FROM classes WHERE (isPrevious = 1) AND (turmaId = :idTurma)")
    fun getPreviousClassWithIdTurma(idTurma: String): LiveData<StudentClass>

    @Query("SELECT * FROM classes where isPrevious = 1")
    fun getPreviousClasses(): LiveData<MutableList<StudentClass>>

    @Query("SELECT * FROM classes where isPrevious = 1")
    fun getPreviousClassesAsync(): MutableList<StudentClass>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertClass(studentStudentClass: StudentClass)

    @Query("DELETE FROM classes")
    fun deleteClasses()


    @Query("SELECT * FROM historyru")
    fun getHistoryRU(): LiveData<MutableList<HistoryRU>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRU(historyRU: HistoryRU)

    @Query("DELETE FROM historyru")
    fun deleteHistoryRU()


    @Query("SELECT * FROM news WHERE idTurma LIKE :idTurma")
    fun getNews(idTurma: String): LiveData<MutableList<News>>

    @Query("SELECT * FROM news WHERE newsId LIKE :newsId")
    fun getNewsWithId(newsId: String): LiveData<News>

    @Query("SELECT * FROM news WHERE newsId LIKE :newsId")
    fun getNewsWithIdAsync(newsId: String): News

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: News)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertNewsContent(news: News)

    @Query("DELETE FROM news WHERE idTurma LIKE :idTurma")
    fun deleteNews(idTurma: String)



    @Query("SELECT * FROM grades WHERE idTurma LIKE :idTurma")
    fun getGrades(idTurma: String): LiveData<MutableList<Grade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertGrade(grade: Grade)

    @Query("DELETE FROM grades")
    fun deleteGrades()

    @Query("DELETE FROM grades WHERE idTurma LIKE :idTurma")
    fun deleteGradesFromClass(idTurma: String)

    @Query("SELECT * FROM ira")
    fun getIra(): LiveData<MutableList<Ira>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertIra(ira: MutableList<Ira>)

    @Query("DELETE FROM ira")
    fun deleteIra()

    @Query("SELECT * FROM files WHERE idTurma LIKE :idTurma")
    fun getFiles(idTurma: String): LiveData<MutableList<File>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertFile(file: File)

    @Query("DELETE FROM files WHERE idTurma LIKE :idTurma")
    fun deleteFiles(idTurma: String)

    @Query("SELECT * FROM vinculos")
    fun getVinculos(): MutableList<Vinculo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertVinculos(vinculo: Vinculo)

    @Query("DELETE FROM vinculos")
    fun deleteVinculos()

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