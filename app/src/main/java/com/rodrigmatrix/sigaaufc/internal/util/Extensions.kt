package com.rodrigmatrix.sigaaufc.internal.util

import android.annotation.SuppressLint
import com.rodrigmatrix.sigaaufc.persistence.entity.Grade
import java.lang.Exception
import java.lang.IndexOutOfBoundsException


fun <T> List<T>.getUncommonElements(other: List<T>): List<T>{
    val sum = this + other
    return sum.groupBy { it }
        .filter { it.value.size == 1 }
        .flatMap { it.value }
}

@SuppressLint("DefaultLocale")
fun String.capitalizeWords(): String = split(" ").joinToString(" ") {
    when {
        it.length <= 3 && it.contains("i") -> return@joinToString it.toUpperCase()
        it.length >= 3 -> return@joinToString it.capitalize()
        else -> return@joinToString it
    }
}

fun List<Grade>.getUncommonGrades(other: List<Grade>): List<Grade> {
    val uncommon = mutableListOf<Grade>()
    forEachIndexed { index, grade ->
        try {
            if(grade.content != other[index].content){
                uncommon.add(grade)
            }
        }
        catch(e: Exception){
            e.printStackTrace()
        }
    }
    return uncommon
}

fun String.getClassNameWithoutCode(): String {
    return try {
        split(" - ")[1]
    }
    catch(e: IndexOutOfBoundsException){
        this
    }
    catch(e: Exception){
        this
    }
}