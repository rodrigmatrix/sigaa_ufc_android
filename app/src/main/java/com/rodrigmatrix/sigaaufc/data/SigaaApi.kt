package com.rodrigmatrix.sigaaufc.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface SigaaApi {

    @Headers("Referer: https://si3.ufc.br/sigaa/verTelaLogin.do")
    @POST("logar.do?dispatch=logOn")
    suspend fun login(
        @Header("Cookie") cookie: String,
        @Field("user.login") login: String,
        @Field("user.senha") password: String,
        @Field("entrar") comando: String
    ): String

    companion object {
        operator fun invoke(
            connectivityInterceptor: Interceptor
        ): SigaaApi {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://si3.ufc.br/")
                .build()
                .create(SigaaApi::class.java)
        }
    }
}