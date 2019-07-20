package com.rodrigmatrix.sigaaufc.internal

import java.io.IOException
import java.net.SocketTimeoutException

class NoConnectivityException: IOException()

class TimeoutException: SocketTimeoutException()