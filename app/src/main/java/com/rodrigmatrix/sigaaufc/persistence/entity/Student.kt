package com.rodrigmatrix.sigaaufc.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val STUDENT_ID = 0

@Entity(tableName = "students")
data class Student(
    var jsession: String,
    var login: String,
    var password: String,
    var name: String,
    var course: String,
    var degree: String,
    var entrance: String,
    var matricula: String,
    var hasSavedData: Boolean,
    var lastUpdate: String,
    var creditsRU: Int,
    var nameRU: String,
    var matriculaRU: String,
    var cardRU: String
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = STUDENT_ID
}

@Entity(tableName = "classes")
data class StudentClass(
    @PrimaryKey
    var turmaId: String,
    var id: Int,
    var isPrevious: Boolean,
    var credits: String,
    var code: String,
    var name: String,
    var days: String,
    var attendance: Int,
    var missed: Int
)

@Entity(tableName = "news")
data class News(
    @PrimaryKey
    var id: String,
    var idTurma: String,
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