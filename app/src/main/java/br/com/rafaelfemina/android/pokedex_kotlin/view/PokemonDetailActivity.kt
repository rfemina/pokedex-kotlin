package br.com.rafaelfemina.android.pokedex_kotlin.view

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import br.com.rafaelfemina.android.pokedex_kotlin.R
import br.com.rafaelfemina.android.pokedex_kotlin.api.PokemonRepository
import br.com.rafaelfemina.android.pokedex_kotlin.domain.Pokemon
import br.com.rafaelfemina.android.pokedex_kotlin.util.TypeColorUtil
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_POKEMON_ID = "pokemon_id"
        const val EXTRA_POKEMON_NAME = "pokemon_name"
        const val EXTRA_PRIMARY_TYPE = "primary_type"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var ivPokemon: ImageView
    private lateinit var tvNumber: TextView
    private lateinit var tvName: TextView
    private lateinit var typesContainer: LinearLayout
    private lateinit var tvCharacteristic: TextView
    private lateinit var tvStatsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)

        toolbar = findViewById(R.id.detail_toolbar)
        ivPokemon = findViewById(R.id.ivPokemonDetail)
        tvNumber = findViewById(R.id.tvNumberDetail)
        tvName = findViewById(R.id.tvNameDetail)
        typesContainer = findViewById(R.id.types_container)
        tvCharacteristic = findViewById(R.id.tv_characteristic)
        tvStatsContainer = findViewById(R.id.stats_container)

        // Setup toolbar as action bar with back/up button
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "" // We'll show name in the layout

        // Get extras
        val pokemonId = intent.getIntExtra(EXTRA_POKEMON_ID, -1)
        val pokemonNameExtra = intent.getStringExtra(EXTRA_POKEMON_NAME) ?: ""
        val primaryTypeExtra = intent.getStringExtra(EXTRA_PRIMARY_TYPE) ?: ""

        // If we have a primary type, set background color accordingly
        val bgColor = TypeColorUtil.getColorResForType(primaryTypeExtra, this)
        findViewById<View>(R.id.detail_root).setBackgroundColor(bgColor)

        // Load pokemon details asynchronously
        lifecycleScope.launch {
            try {
                val pokemonApi = withContext(Dispatchers.IO) {
                    // fetch by id
                    PokemonRepository.getPokemon(pokemonId)
                }

                val pokemon = pokemonApi?.let {
                    Pokemon(it.id, it.name, it.types.map { slot -> slot.type })
                }

                // Populate UI (on main thread)
                populateUI(pokemon ?: Pokemon(pokemonId, pokemonNameExtra, emptyList()))

                // Try fetch characteristic as a best-effort (see notes)
                lifecycleScope.launch {
                    try {
                        val charId = (pokemonId % 30) + 1 // heuristic fallback
                        val charResult = withContext(Dispatchers.IO) { PokemonRepository.getCharacteristic(charId) }
                        val description = charResult?.descriptions?.firstOrNull { it.language?.name == "en" }?.description
                        tvCharacteristic.text = description ?: getString(R.string.characteristic_not_found)
                    } catch (e: Exception) {
                        tvCharacteristic.text = getString(R.string.characteristic_not_found)
                    }
                }

                // Also show base stats from the pokemonApi (if present)
                pokemonApi?.let {
                    showStats(it)
                }
            } catch (e: Exception) {
                // fallback: minimal display
                populateUI(Pokemon(pokemonId, pokemonNameExtra, emptyList()))
                tvCharacteristic.text = getString(R.string.characteristic_not_found)
            }
        }
    }

    private fun populateUI(pokemon: Pokemon) {
        // image
        Glide.with(this).load(pokemon.imageUrl).into(ivPokemon)

        // number & name
        tvNumber.text = "Nº ${pokemon.formattedNumber}"
        tvName.text = pokemon.formattedName

        // types badges
        typesContainer.removeAllViews()
        pokemon.types.forEach { type ->
            val tv = TextView(this)
            tv.text = type.name.capitalize()
            tv.setTextColor(resources.getColor(android.R.color.black))
            tv.textSize = 14f
            val bg = GradientDrawable().apply {
                cornerRadius = 12f
                setColor(TypeColorUtil.getColorResForType(type.name, this@PokemonDetailActivity))
            }
            tv.background = bg
            val pad = (12 * resources.displayMetrics.density).toInt()
            tv.setPadding(pad, 8, pad, 8)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(8, 0, 8, 0)
            typesContainer.addView(tv, lp)
        }
    }

    private fun showStats(apiResult: br.com.rafaelfemina.android.pokedex_kotlin.api.model.PokemonApiResult) {
        // Clear and add stats (HP, attack, defense, etc.) if present
        tvStatsContainer.removeAllViews()
        apiResult.stats?.forEach { statSlot ->
            val tv = TextView(this)
            tv.text = "${statSlot.stat.name.capitalize()}: ${statSlot.base_stat}"
            tv.setTextColor(resources.getColor(android.R.color.black))
            tv.textSize = 14f
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 8, 0, 8)
            tvStatsContainer.addView(tv, lp)
        }
    }

    // Handle toolbar back/up press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
