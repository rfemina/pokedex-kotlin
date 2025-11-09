package br.com.rafaelfemina.android.pokedex_kotlin.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import br.com.rafaelfemina.android.pokedex_kotlin.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Toolbar e botão de voltar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_about)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Foto do desenvolvedor - acessibilidade
        findViewById<ImageView>(R.id.about_photo).contentDescription = getString(R.string.about_photo_desc)

        // GitHub link e botão
        val githubUrl = "https://github.com/rfemina"
        val tvGithub = findViewById<TextView>(R.id.about_github_link)
        val btnGithub = findViewById<MaterialButton>(R.id.about_github_button)
        val githubRow = findViewById<LinearLayout>(R.id.about_github_row)

        tvGithub.text = githubUrl
        val openGithub = View.OnClickListener { openUrlSafe(githubUrl) }
        tvGithub.setOnClickListener(openGithub)
        btnGithub.setOnClickListener(openGithub)
        githubRow.setOnClickListener(openGithub)

        // Card da estrutura do projeto
        val structureCard = findViewById<MaterialCardView>(R.id.about_structure_card)
        structureCard.setOnClickListener {
            val items = resources.getStringArray(R.array.structure_items).toList()
            val dlg = ProjectStructureDialogFragment(items)
            dlg.show(supportFragmentManager, "project_structure")
        }
    }

    private fun openUrlSafe(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            // opcional: Toast ou log de erro
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
