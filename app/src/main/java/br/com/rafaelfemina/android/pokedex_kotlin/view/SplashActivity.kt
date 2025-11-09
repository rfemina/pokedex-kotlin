package br.com.rafaelfemina.android.pokedex_kotlin.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.rafaelfemina.android.pokedex_kotlin.R
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animationView = findViewById<LottieAnimationView>(R.id.lottieView)

        animationView.setAnimationFromUrl("https://lottie.host/c075318c-15ac-4248-96f3-b1bf4f8bf9be/67xRB7WE9U.lottie")
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.speed = 3f
        animationView.playAnimation()

        // Aguarda 3 segundos e abre a MainActivity
        animationView.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}
