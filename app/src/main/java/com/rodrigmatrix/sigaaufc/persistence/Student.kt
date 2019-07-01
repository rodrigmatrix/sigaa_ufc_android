package com.rodrigmatrix.sigaaufc.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey
    var id: Int,
    var jsession: String,
    var login: String,
    var password: String,
    var name: String,
    var course: String,
    var degree: String,
    var entrance: String,
    var matricula: String,
    var theme: String,
    var hasSavedData: Boolean,
    var lastUpdate: String,
    var creditsRU: Int,
    var nameRU: String,
    var matriculaRU: String,
    var cardRU: String
)

@Entity(tableName = "classes")
data class Class(
    @PrimaryKey
    var id: Int,
    var turmaId: Int,
    var name: String,
    var days: String,
    var attendance: Int,
    var missed: Int
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