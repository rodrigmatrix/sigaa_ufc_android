package com.rodrigmatrix.sigaaufc.internal.util

import java.lang.Exception
import java.lang.IndexOutOfBoundsException


fun <T> List<T>.getUncommonElements(other: List<T>): List<T>{
    return this.toSet().minus(other.toSet()).toList()
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