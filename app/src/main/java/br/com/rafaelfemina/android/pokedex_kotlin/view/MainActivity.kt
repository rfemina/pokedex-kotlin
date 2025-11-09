package br.com.rafaelfemina.android.pokedex_kotlin.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rafaelfemina.android.pokedex_kotlin.R
import br.com.rafaelfemina.android.pokedex_kotlin.api.PokemonRepository
import br.com.rafaelfemina.android.pokedex_kotlin.domain.Pokemon
import br.com.rafaelfemina.android.pokedex_kotlin.viewmodel.PokemonViewModel
import br.com.rafaelfemina.android.pokedex_kotlin.viewmodel.PokemonViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvPokemons) }
    private val spinnerType by lazy { findViewById<AutoCompleteTextView>(R.id.spinner_type) }
    private val spinnerGeneration by lazy { findViewById<AutoCompleteTextView>(R.id.spinner_generation) }
    private val progressLoading by lazy { findViewById<ProgressBar>(R.id.progress_loading_generation) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.topAppBar) }
    private val searchEditText by lazy { findViewById<TextInputEditText>(R.id.searchEditText) }

    private val fullscreenOverlay by lazy { findViewById<FrameLayout>(R.id.fullscreen_overlay) }
    private val fullscreenMessage by lazy { findViewById<TextView>(R.id.fullscreen_message) }
    private val fullscreenProgress by lazy { findViewById<ProgressBar>(R.id.fullscreen_progress) }

    private val viewModel by lazy {
        ViewModelProvider(this, PokemonViewModelFactory())
            .get(PokemonViewModel::class.java)
    }

    private val adapter = PokemonAdapter()

    private var allPokemons: List<Pokemon> = emptyList()
    private val generationPokemonCache = mutableMapOf<String, List<Pokemon>>()
    private var currentGenerationKey: String? = null
    private val detailedCache = mutableMapOf<Int, Pokemon>()
    private val inProgressIds = mutableSetOf<Int>()
    private val fallbackTypes = listOf(
        "normal","fighting","flying","poison","ground","rock","bug","ghost",
        "steel","fire","water","grass","electric","psychic","ice","dragon",
        "dark","fairy","unknown","shadow"
    )
    private var batchSize = 3
    private var lookahead = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Pokedex"

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observers
        viewModel.loading.observe(this) { isLoading ->
            progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.pokemons.observe(this) { list ->
            allPokemons = list.filterNotNull()
            val merged = allPokemons.map { p -> detailedCache[p.number] ?: p }
            adapter.submitList(merged)
            setupTypeDropdown()
            setupGenerationDropdown()
            applyFilters()
            recyclerView.post { prefetchAroundVisible() }
        }

        if (viewModel.pokemons.value.isNullOrEmpty()) {
            viewModel.refresh()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                super.onScrollStateChanged(rv, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) prefetchAroundVisible()
            }
        })

        setupTypeFilter()
        setupGenerationFilter()
        setupSearch()
    }

    // ---------- SEARCH ----------
    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { applyFilters() }
        })
    }

    // ---------- FILTERS ----------
    private fun setupTypeFilter() {
        spinnerType.setOnItemClickListener { parent, _, position, _ ->
            val selectedRaw = parent.getItemAtPosition(position)?.toString() ?: "All"
            val selected = selectedRaw.trim()
            spinnerType.setText(selected, false)
            lifecycleScope.launch { loadTypeFilter(selected) }
        }
    }

    private fun setupGenerationFilter() {
        spinnerGeneration.setOnItemClickListener { parent, _, position, _ ->
            val selectedRaw = parent.getItemAtPosition(position)?.toString() ?: "All"
            val selected = selectedRaw.trim()
            spinnerGeneration.setText(selected, false)
            lifecycleScope.launch { loadGenerationFilter(selected) }
        }
    }

    // ---------- DROPDOWNS (preenchimento + comportamento de tocar/abrir) ----------
    private fun setupTypeDropdown() {
        // garantia: abrir dropdown ao clicar/focar
        spinnerType.threshold = 0
        spinnerType.setOnClickListener { spinnerType.showDropDown() }
        spinnerType.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) spinnerType.showDropDown() }

        lifecycleScope.launch {
            try {
                val typesResult = withContext(Dispatchers.IO) { PokemonRepository.listTypes() }
                val typeNames = mutableListOf("All")
                typesResult?.results?.forEach { t -> typeNames.add(t.name) }
                if (typeNames.size <= 1) typeNames.addAll(fallbackTypes)
                val adapterType = ArrayAdapter(this@MainActivity, R.layout.dropdown_item, typeNames)
                spinnerType.setAdapter(adapterType)
                spinnerType.setText("All", false)
            } catch (e: Exception) {
                val adapterType = ArrayAdapter(this@MainActivity, R.layout.dropdown_item, listOf("All") + fallbackTypes)
                spinnerType.setAdapter(adapterType)
                spinnerType.setText("All", false)
            }
        }
    }

    private fun setupGenerationDropdown() {
        // garantia: abrir dropdown ao clicar/focar
        spinnerGeneration.threshold = 0
        spinnerGeneration.setOnClickListener { spinnerGeneration.showDropDown() }
        spinnerGeneration.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) spinnerGeneration.showDropDown() }

        lifecycleScope.launch {
            try {
                val gens = withContext(Dispatchers.IO) { PokemonRepository.listGenerations() }
                val genNames = mutableListOf("All")
                gens?.results?.forEach { genNames.add(it.name) }
                val adapterGen = ArrayAdapter(this@MainActivity, R.layout.dropdown_item, genNames)
                spinnerGeneration.setAdapter(adapterGen)
                spinnerGeneration.setText("All", false)
            } catch (e: Exception) {
                val adapterGen = ArrayAdapter(this@MainActivity, R.layout.dropdown_item, listOf("All"))
                spinnerGeneration.setAdapter(adapterGen)
                spinnerGeneration.setText("All", false)
            }
        }
    }

    // ---------- FILTER / PREFETCH / OVERLAY (mantive tudo igual) ----------
    private suspend fun loadTypeFilter(selected: String) {
        if (selected.equals("All", ignoreCase = true)) {
            applyFilters()
            return
        }

        val sourceList = currentGenerationKey?.let { generationPokemonCache[it] } ?: allPokemons
        showFullscreenOverlay("Carregando tipos (pode demorar)...")
        try {
            val chunks = sourceList.chunked(batchSize)
            for (chunk in chunks) {
                val deferreds = chunk.map { p ->
                    lifecycleScope.async(Dispatchers.IO) {
                        if (detailedCache.containsKey(p.number)) return@async null
                        if (!inProgressIds.add(p.number)) return@async null
                        try {
                            val api = try { PokemonRepository.getPokemon(p.number) } catch (e: Exception) {
                                try { PokemonRepository.getPokemon(p.number) } catch (_: Exception) { null }
                            }
                            api?.let { Pokemon(it.id, it.name, it.types.map { s -> s.type }) }
                        } finally { inProgressIds.remove(p.number) }
                    }
                }
                val results = deferreds.awaitAll().filterNotNull()
                results.forEach { det -> detailedCache[det.number] = det; adapter.updateItem(det) }
            }
            applyFilters()
        } finally { hideFullscreenOverlay() }
    }

    private suspend fun loadGenerationFilter(selected: String) {
        if (selected.equals("All", ignoreCase = true)) {
            currentGenerationKey = null
            applyFilters()
            prefetchAroundVisible()
            return
        }

        val key = selected.lowercase()
        currentGenerationKey = key
        showFullscreenOverlay("Carregando geração...")
        try {
            generationPokemonCache[key]?.let { cached ->
                val merged = cached.map { detailedCache[it.number] ?: it }
                adapter.submitList(merged)
                applyFilters()
                prefetchAroundVisible()
                return
            }

            val genDetail = withContext(Dispatchers.IO) { PokemonRepository.getGeneration(key) }
            val speciesList = genDetail?.pokemon_species?.map { it.name } ?: emptyList()
            if (speciesList.isEmpty()) {
                generationPokemonCache[key] = emptyList()
                adapter.submitList(emptyList())
                applyFilters()
                return
            }

            val pokemonsOfGen = mutableListOf<Pokemon>()
            val nameChunks = speciesList.chunked(batchSize)
            for (chunk in nameChunks) {
                val deferred = chunk.map { name ->
                    lifecycleScope.async(Dispatchers.IO) {
                        try {
                            val api = PokemonRepository.getPokemonByName(name)
                            api?.let { Pokemon(it.id, it.name, it.types.map { s -> s.type }) }
                        } catch (e: Exception) { null }
                    }
                }
                val results = deferred.awaitAll().filterNotNull()
                results.forEach { detailedCache[it.number] = it }
                pokemonsOfGen.addAll(results)
                adapter.submitList(pokemonsOfGen.map { detailedCache[it.number] ?: it }.toList())
            }
            val sorted = pokemonsOfGen.sortedBy { it.number }
            generationPokemonCache[key] = sorted
            adapter.submitList(sorted.map { detailedCache[it.number] ?: it })
            applyFilters()
            prefetchAroundVisible()
        } catch (e: Exception) {
            currentGenerationKey = null
            applyFilters()
        } finally { hideFullscreenOverlay() }
    }

    private fun applyFilters() {
        val query = searchEditText.text?.toString()?.trim()?.lowercase() ?: ""
        val selectedType = spinnerType.text?.toString()?.lowercase() ?: "all"
        val sourceList = currentGenerationKey?.let { generationPokemonCache[it] } ?: allPokemons

        val filtered = sourceList.filter { p ->
            val det = detailedCache[p.number] ?: p

            val matchesSearch = query.isEmpty() ||
                    det.name.contains(query, ignoreCase = true) ||
                    det.number.toString().contains(query) ||
                    det.types.any { it.name.contains(query, ignoreCase = true) }

            val matchesType = selectedType == "all" ||
                    det.types.any { it.name.equals(selectedType, ignoreCase = true) }

            matchesSearch && matchesType
        }

        adapter.submitList(filtered.map { detailedCache[it.number] ?: it })
    }

    private fun prefetchAroundVisible() {
        val lm = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val first = lm.findFirstVisibleItemPosition().coerceAtLeast(0)
        val last = lm.findLastVisibleItemPosition().coerceAtLeast(0)
        if (last < first) return

        val start = max(0, first - lookahead)
        val end = min((adapter.itemCount - 1).coerceAtLeast(0), last + lookahead)
        val toFetch = mutableListOf<Pokemon>()
        for (pos in start..end) {
            val p = adapter.getItem(pos) ?: continue
            if (p.types.isNotEmpty()) continue
            if (detailedCache.containsKey(p.number)) continue
            if (!inProgressIds.add(p.number)) continue
            toFetch.add(p)
        }

        if (toFetch.isEmpty()) return

        lifecycleScope.launch {
            try {
                val chunks = toFetch.chunked(batchSize)
                for (chunk in chunks) {
                    val deferred = chunk.map { p ->
                        lifecycleScope.async(Dispatchers.IO) {
                            try {
                                val api = PokemonRepository.getPokemon(p.number)
                                api?.let { Pokemon(it.id, it.name, it.types.map { s -> s.type }) }
                            } finally { inProgressIds.remove(p.number) }
                        }
                    }
                    val results = deferred.awaitAll().filterNotNull()
                    results.forEach { det -> detailedCache[det.number] = det; adapter.updateItem(det) }
                }
            } catch (_: Exception) {}
        }
    }

    // ---------- OVERLAY ----------
    private fun showFullscreenOverlay(message: String) {
        fullscreenMessage.text = message
        fullscreenOverlay.visibility = View.VISIBLE
    }

    private fun hideFullscreenOverlay() {
        fullscreenOverlay.visibility = View.GONE
    }
}
