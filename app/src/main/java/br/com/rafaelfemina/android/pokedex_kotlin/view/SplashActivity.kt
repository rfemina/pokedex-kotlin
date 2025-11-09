package br.com.rafaelfemina.android.pokedex_kotlin.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import br.com.rafaelfemina.android.pokedex_kotlin.R
import com.bumptech.glide.Glide

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashGif = findViewById<ImageView>(R.id.splashPokemonGif)

        // Carrega o GIF animado usando Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.ash_anim)
            .into(splashGif)

        // Aguarda 3 segundos e abre a MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}
