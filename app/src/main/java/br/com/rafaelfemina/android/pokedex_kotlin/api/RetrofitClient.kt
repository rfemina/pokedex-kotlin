package br.com.rafaelfemina.android.pokedex_kotlin.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // service retrofit usado pelo repository
    lateinit var service: PokemonService
        private set

    // expõe o OkHttpClient para reuso (ex: Glide)
    lateinit var okHttpClient: OkHttpClient
        private set

    /**
     * Inicializa Retrofit + OkHttp com cache. Deve ser chamado na Application#onCreate().
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun init(context: Context) {
        // Cache: 10 MB
        val cacheSize: Long = 10L * 1024L * 1024L
        val httpCacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(httpCacheDir, cacheSize)

        // Logging
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        // Interceptor para modo offline
        val offlineInterceptor = Interceptor { chain ->
            var request: Request = chain.request()
            if (!hasNetwork(context)) {
                val maxStale = 60 * 60 * 24 * 7 // 7 days
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .build()
            }
            chain.proceed(request)
        }

        // Network interceptor para cache quando online
        val networkInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val maxAge = 60 // 1 minute
            response.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        }

        // cria client e guarda em propriedade pública
        okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(offlineInterceptor)
            .addNetworkInterceptor(networkInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PokemonService::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
