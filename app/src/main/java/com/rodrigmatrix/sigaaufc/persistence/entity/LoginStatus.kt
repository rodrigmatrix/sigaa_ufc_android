package com.rodrigmatrix.sigaaufc.persistence.entity

data class LoginStatus(
    val loginStatus: Int = LOGIN_ERROR,
    val loginMessage: String = "",
    val response: String = ""
) {
    companion object {
        const val LOGIN_SUCCESS = 0
        const val LOGIN_VINCULO = 1
        const val LOGIN_REDIRECT = 2
        const val LOGIN_ERROR = 3
    }
}

