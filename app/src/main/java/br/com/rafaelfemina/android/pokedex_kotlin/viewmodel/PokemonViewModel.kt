package br.com.rafaelfemina.android.pokedex_kotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rafaelfemina.android.pokedex_kotlin.api.PokemonRepository
import br.com.rafaelfemina.android.pokedex_kotlin.domain.Pokemon
import br.com.rafaelfemina.android.pokedex_kotlin.domain.PokemonType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel que carrega inicialmente a lista LEVE de todos os pokémons
 * (apenas id/name/imageUrl/number) usando listPokemons(limit = 100000).
 *
 * O objetivo é mostrar a lista imediatamente ao abrir o app. Detalhes (types, stats)
 * podem ser carregados sob demanda (não feito automaticamente aqui).
 */
class PokemonViewModel : ViewModel() {

    // LiveData observada pela Activity
    val pokemons = MutableLiveData<List<Pokemon>>(emptyList())
    val loading = MutableLiveData<Boolean>(false)
    val error = MutableLiveData<String?>(null)

    init {
        loadAllPokemonsLight()
    }

    /**
     * Busca a listagem completa (names + urls) e cria Pokemons "leves" para exibição imediata.
     */
    private fun loadAllPokemonsLight() {
        viewModelScope.launch {
            loading.value = true
            error.value = null

            try {
                // chama o repository (suspend) no IO
                val result = withContext(Dispatchers.IO) {
                    // Pede todos os pokémons disponíveis; API suporta limit grande
                    PokemonRepository.listPokemons(limit = 100000)
                }

                // Mapear resultados para objetos Pokemon 'leves':
                // - extrair número do url (ex: https://pokeapi.co/api/v2/pokemon/25/)
                // - criar Pokemon(number, name, emptyList())
                val list = result?.results?.mapNotNull { pr ->
                    try {
                        // extrai o número da url
                        val url = pr.url.trim()
                        val numStr = url.removeSuffix("/").substringAfterLast("/")
                        val number = numStr.toIntOrNull() ?: return@mapNotNull null

                        // construir objeto Pokemon leve (sem types ainda)
                        Pokemon(
                            number = number,
                            name = pr.name,
                            types = emptyList() // carregaremos sob demanda se necessário
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                // publica a lista leve imediatamente (UI mostrará imagens e nomes)
                pokemons.value = list
            } catch (e: Exception) {
                error.value = e.message ?: "Erro ao carregar lista de pokémons"
            } finally {
                loading.value = false
            }
        }
    }

    /**
     * Função pública auxiliar: carrega detalhes (types, stats, etc.) para um pokemon específico
     * por id. Retorna o Pokemon completo ou null.
     *
     * Útil para carregar tipos ao abrir detalhe ou ao aplicar filtros por type.
     */
    fun loadPokemonDetailsById(id: Int, onComplete: (Pokemon?) -> Unit) {
        viewModelScope.launch {
            try {
                val api = withContext(Dispatchers.IO) { PokemonRepository.getPokemon(id) }
                val p = api?.let {
                    Pokemon(
                        number = it.id,
                        name = it.name,
                        types = it.types.map { slot -> slot.type }
                    )
                }
                onComplete(p)
            } catch (e: Exception) {
                onComplete(null)
            }
        }
    }

    /**
     * Recarrega a lista leve (por exemplo, se quiser refresh).
     */
    fun refresh() {
        loadAllPokemonsLight()
    }
}
