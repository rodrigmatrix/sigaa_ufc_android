package com.rodrigmatrix.sigaaufc.persistence.entity

data class LoginStatus(
    val loginStatus: Int = LOGIN_ERROR,
    val loginMessage: String = ""
) {
    companion object {
        const val LOGIN_SUCCESS = 0
        const val LOGIN_ERROR = 1
    }
}

