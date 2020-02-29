package com.rodrigmatrix.sigaaufc.internal.util

import java.lang.Exception
import java.lang.IndexOutOfBoundsException


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