package com.rodrigmatrix.sigaaufc.internal

import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

class NoConnectivityException: IOException()

class TimeoutException: SocketTimeoutException()

class LoginException(message: String): Exception(message)