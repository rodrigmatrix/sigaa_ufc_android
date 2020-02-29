package com.rodrigmatrix.sigaaufc.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val STUDENT_ID = 0


data class Attendance(val attended: Int, val missed: Int)

@Entity(tableName = "students")
data class Student(
    var jsession: String,
    var login: String,
    var password: String,
    var name: String,
    var course: String,
    var matricula: String,
    var hasSavedData: Boolean,
    var lastUpdate: String,
    var profilePic: String
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = STUDENT_ID
}

@Entity(tableName = "ru_card")
data class RuCard(
    var creditsRU: Int,
    var nameRU: String,
    var matriculaRU: String,
    var cardRU: String
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = STUDENT_ID
}

@Entity(tableName = "view_faces")
data class JavaxFaces(
    var ongoing: Boolean,
    var valueState: String
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = STUDENT_ID
}

@Entity(tableName = "classes")
data class StudentClass(
    @PrimaryKey
    var turmaId: String,
    var id: String,
    var isPrevious: Boolean,
    var credits: String,
    var code: String,
    var name: String,
    var location: String,
    var period: String,
    var days: String,
    var attendance: Int,
    var missed: Int,
    var synced: Boolean = false
)

@Entity(tableName = "news")
data class News(
    @PrimaryKey
    var newsId: String,
    var requestId: String,
    var requestId2: String,
    var idTurma: String,
    var title: String,
    var date: String,
    var content: String
)

@Entity(tableName = "historyru")
data class HistoryRU(
    @PrimaryKey
    var id: Int,
    var date: String,
    var time: String,
    var operation: String,
    var content: String
)

@Entity(tableName = "grades")
data class Grade(
    @PrimaryKey
    var id: String,
    var idTurma: String,
    var name: String,
    var content: String
)

@Entity(tableName = "ira")
data class Ira(
    @PrimaryKey
    var id: String,
    var period: String,
    var iraI: Double,
    var iraG: Double
)

@Entity(tableName = "files")
data class File(
    @PrimaryKey
    var id: String,
    var idTurma: String,
    var name: String,
    var requestId: String
)

@Entity(tableName = "vinculos", primaryKeys = ["name", "id"])
data class Vinculo(
    var name: String,
    var status: String,
    var content: String,
    var id: String
)




