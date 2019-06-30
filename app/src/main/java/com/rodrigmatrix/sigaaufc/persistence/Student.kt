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
    var lastUpdate: String
)

@Entity(tableName = "classes")
data class Classes(
    @PrimaryKey
    var id: Int,
    var turmaId: Int,
    var name: String,
    var days: String,
    var attendance: Int,
    var missed: Int
)