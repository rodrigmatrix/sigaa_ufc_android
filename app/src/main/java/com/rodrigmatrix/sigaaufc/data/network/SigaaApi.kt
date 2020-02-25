package com.rodrigmatrix.sigaaufc.data.network

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.*
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface SigaaApi {

    @GET
    suspend fun getCookie()

    @POST("logar.do")
    suspend fun login(
        @Body formBody: FormBody,
        @Query("dispatch") dispatch: String = "logOn",
        @Header("Referer") referer: String = "https://si3.ufc.br/sigaa/verTelaLogin.do"

    ): ResponseBody

    @GET("escolhaVinculo.do")
    suspend fun setVinculo(
        @QueryMap params: HashMap<String, String>
    ): ResponseBody



    companion object {
        operator fun invoke(context: Context): SigaaApi {
            val requestInterceptor = Interceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Connection","close")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build()
                return@Interceptor chain.proceed(request)
            }
            val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .addInterceptor(requestInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://si3.ufc.br/sigaa/")
                .build()
                .create(SigaaApi::class.java)
        }
    }
}